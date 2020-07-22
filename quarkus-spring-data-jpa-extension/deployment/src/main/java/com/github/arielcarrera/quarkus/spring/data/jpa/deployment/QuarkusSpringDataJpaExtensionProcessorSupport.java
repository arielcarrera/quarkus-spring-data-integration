package com.github.arielcarrera.quarkus.spring.data.jpa.deployment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;
import org.jboss.logging.Logger;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.RepositoryConfigurationBeanBuildItem.CdiConfigurationBeanItem;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.RepositoryConfigurationBuildItem.CdiConfigurationItem;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.RepositoryContext;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.RepositoryFactory;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.RepositoryFragmentsSupport;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.JPAEntityManagerProvider;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.ClientProxy;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.InjectableBean;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.arc.deployment.BeanRegistrationPhaseBuildItem.BeanConfiguratorBuildItem;
import io.quarkus.arc.impl.CreationalContextImpl;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.arc.processor.BeanRegistrar.RegistrationContext;
import io.quarkus.arc.processor.BuiltinScope;
import io.quarkus.arc.processor.ScopeInfo;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;

public class QuarkusSpringDataJpaExtensionProcessorSupport {

	private static final Logger LOGGER = Logger.getLogger(QuarkusSpringDataJpaExtensionProcessorSupport.class.getName());
	
	static final MethodDescriptor ARC_CONTAINER = MethodDescriptor.ofMethod(Arc.class, "container", ArcContainer.class);
	static final MethodDescriptor ARC_CONTAINER_INSTANCE = MethodDescriptor.ofMethod(ArcContainer.class, "instance",
			InstanceHandle.class, InjectableBean.class);
	static final MethodDescriptor ARC_CONTAINER_BEAN = MethodDescriptor.ofMethod(ArcContainer.class, "bean",
			InjectableBean.class, String.class);
	static final MethodDescriptor INSTANCE_HANDLE_GET = MethodDescriptor.ofMethod(InstanceHandle.class, "get",
			Object.class);
	static final MethodDescriptor CONTEXTUAL_CREATE = MethodDescriptor.ofMethod(Contextual.class, "create",
			Object.class, CreationalContext.class);
	static final MethodDescriptor CONTEXTUAL_DESTROY = MethodDescriptor.ofMethod(Contextual.class, "destroy",
			void.class, Object.class, CreationalContext.class);
	static final MethodDescriptor CLIENT_PROXY_CONTEXTUAL_INSTANCE = MethodDescriptor.ofMethod(ClientProxy.class,
			"arc_contextualInstance", Object.class);

	static final MethodDescriptor ARC_CONTAINER_INSTANCE_FOR_TYPE = MethodDescriptor.ofMethod(ArcContainer.class,
			"instance", InstanceHandle.class, Class.class, Annotation[].class);
	static final MethodDescriptor INSTANCE_HANDLE_GET_BEAN = MethodDescriptor.ofMethod(InstanceHandle.class, "getBean",
			InjectableBean.class);
	static final MethodDescriptor INJECTABLE_BEAN_HANDLE_GET_SCOPE = MethodDescriptor.ofMethod(InjectableBean.class,
			"getScope", Class.class);
	
	/**
	 * Scans the index for {@link Repository} implementors
	 * 
	 * @param index
	 * @param pendingImplementors
	 * @param allImplementors
	 * @return
	 */
	public static Set<DotName> scanRepositoryImplementors(IndexView index, Set<DotName> pendingImplementors,
			Set<DotName> allImplementors) {
		Set<DotName> pendings = new HashSet<>();
		for (Iterator<DotName> iterator = pendingImplementors.iterator(); iterator.hasNext();) {
			DotName currentClass = iterator.next();
			Collection<ClassInfo> collection = index.getKnownDirectImplementors(currentClass);
			for (ClassInfo c : collection) {
				if (!allImplementors.contains(c.name()) && !pendingImplementors.contains(c.name())) {
					pendings.add(c.name());
				}
			}
			allImplementors.add(currentClass);
		}
		return pendings;
	}
	
	/**
	 * Scans the index for {@link Repository} types
	 * 
	 * @param indexItem
	 * @param index
	 * @return
	 */
	public static Map<ClassInfo, List<AnnotationInstance>> scanRepositoryTypes(CombinedIndexBuildItem indexItem,
			IndexView index) {
		Map<ClassInfo, List<AnnotationInstance>> repositoryTypes = new HashMap<>();
		Set<DotName> pendings = index.getKnownDirectImplementors(DotNames.SPRING_DATA_REPOSITORY).stream()
				.map(m -> m.name()).collect(Collectors.toSet());
		Set<DotName> allImplementors = new HashSet<>();
		do {
			pendings = QuarkusSpringDataJpaExtensionProcessorSupport.scanRepositoryImplementors(index, pendings, allImplementors);
		} while (!pendings.isEmpty());

		Collection<ClassInfo> allKnownClasses = indexItem.getIndex().getKnownClasses();
		for (ClassInfo classInfo : allKnownClasses) {
			// look for repository types
			if (QuarkusSpringDataJpaExtensionProcessorSupport.isRepository(classInfo, allImplementors)) {
				// get qualifiers of the repository type
				List<AnnotationInstance> qualifiers = QuarkusSpringDataJpaExtensionProcessorSupport
						.getQualifiers(classInfo, index);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format("Discovered repository type '%s' with qualifiers %s.", classInfo.name(),
							qualifiers));
				}
				// Store the repository type using its qualifiers.
				repositoryTypes.put(classInfo, qualifiers);
			}
		}
		return repositoryTypes;
	}

	/**
	 * Scans the index for {@link CdiRepositoryConfiguration }
	 * 
	 * @param cdiRepositoryConfigurationQualifiers
	 * @param index
	 */
	public static void scanRepoConfigurationBeans(List<CdiConfigurationItem> cdiRepositoryConfigurationQualifiers,
			IndexView index) {
		Collection<ClassInfo> allKnownImplementors = index
				.getAllKnownImplementors(DotNames.SPRING_DATA_CDI_REPOSITORY_CONFIGURATION);
		for (ClassInfo classInfo : allKnownImplementors) {
			// if the class hasn't the Vetoed annotation... it is discoverable
			if (classInfo.classAnnotation(DotNames.VETOED) == null) {
				List<AnnotationInstance> qualifiers = classInfo.classAnnotations().stream()
						.filter(a -> QuarkusSpringDataJpaExtensionProcessorSupport.isQualifier(a, index))
						.collect(Collectors.toList());
				// Adds Default qualifier if collection is empty
				if (qualifiers.isEmpty()) {
					qualifiers
							.add(AnnotationInstance.create(DotNames.QUALIFIER_DEFAULT, null, Collections.emptyList()));
				}
				// Adds Any qualifier for all
				qualifiers.add(AnnotationInstance.create(DotNames.QUALIFIER_ANY, null, Collections.emptyList()));

				cdiRepositoryConfigurationQualifiers.add(new CdiConfigurationItem(classInfo, qualifiers));
			}
		}
	}

	/**
	 * Scans the index for {@link CdiRepositoryConfiguration} types
	 * @param cdiRepositoryConfigurationQualifiers
	 * @param index
	 */
	public static void scanRepoConfigurationProducers(List<CdiConfigurationItem> cdiRepositoryConfigurationQualifiers,
			IndexView index) {
		Collection<AnnotationInstance> annotations = index.getAnnotations(DotNames.PRODUCES);
		for (AnnotationInstance annotation : annotations) {
			// look for producer
			if (Kind.METHOD.equals(annotation.target().kind())) {
				MethodInfo method = annotation.target().asMethod();
				if (method.returnType().name().equals(DotNames.SPRING_DATA_CDI_REPOSITORY_CONFIGURATION)) {
					List<AnnotationInstance> qualifiers = new ArrayList<>();
					List<AnnotationInstance> methodAnnotations = method.annotations();
					for (AnnotationInstance a : methodAnnotations) {
						if (QuarkusSpringDataJpaExtensionProcessorSupport.isQualifier(a, index)) {
							qualifiers.add(a);
						}
					}
					// Adds Default qualifier if collection is empty
					if (qualifiers.isEmpty()) {
						qualifiers.add(
								AnnotationInstance.create(DotNames.QUALIFIER_DEFAULT, null, Collections.emptyList()));
					}
					// Adds Any qualifier for all
					qualifiers.add(AnnotationInstance.create(DotNames.QUALIFIER_ANY, null, Collections.emptyList()));

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format("Discovered entity manager producer with qualifiers %s.",
								method.annotations()));
					}
					cdiRepositoryConfigurationQualifiers
							.add(new CdiConfigurationItem(method.declaringClass(), qualifiers));
				}
			}
		}
	}
	
	/**
	 * Removes the unused {@link DefaultBean} from the list of {@link CdiRepositoryConfiguration} beans
	 * @param list
	 */
	public static void removeUnusedDefaultBeans(List<CdiConfigurationBeanItem> list) {
		if (list.size() > 1) {
			// removing @DefaultBean if there is another bean replacing him
			List<CdiConfigurationBeanItem> itemsToRemove = new ArrayList<>();
			for (CdiConfigurationBeanItem item : list) {
				ClassInfo classInfo = item.getClassInfo();
				AnnotationInstance classAnnotation = classInfo.classAnnotation(DotNames.DEFAULT_BEAN);
				if (classAnnotation != null) {
					// check if it needs to be removed
					for (CdiConfigurationBeanItem item2 : list) {
						if (item != item2) {
							if (item.getAnnotationsInstance().size() == item2.getAnnotationsInstance().size()) {
								List<AnnotationInstance> listToCheck = new ArrayList<>(item2.getAnnotationsInstance());
								for (AnnotationInstance aInstance : item.getAnnotationsInstance()) {
									for (AnnotationInstance aInstanceCheckList : listToCheck) {
										if (aInstance.toString().equals(aInstanceCheckList.toString())) {
											listToCheck.remove(aInstanceCheckList);
											break;
										}
									}
								}
								if (listToCheck.isEmpty()) {
									// Detected
									itemsToRemove.add(item);
									break;
								}
							}
						}
					}
				}
			}
			list.removeAll(itemsToRemove);
		}
	}
	
	/**
	 * Method to write a synthetic bean
	 * 
	 * @param beanConfigurators
	 * @param context
	 * @param entityManagerIdentifier
	 * @param entityManagerScope
	 * @param cdiRepositoryConfigurationQualifiers
	 * @param repositoryTypes
	 * @param classInfo
	 */
	public static void writeSyntheticBean(BuildProducer<BeanConfiguratorBuildItem> beanConfigurators,
			RegistrationContext context, final String entityManagerIdentifier, final ScopeInfo entityManagerScope,
			final List<CdiConfigurationBeanItem> cdiRepositoryConfigurationQualifiers,
			Map<ClassInfo, List<AnnotationInstance>> repositoryTypes, ClassInfo classInfo) {
		DotName repoDotName = classInfo.name();
		List<AnnotationInstance> qualifierList = new ArrayList<>();
		List<AnnotationInstance> qualifiers = repositoryTypes.get(classInfo);
		qualifierList.addAll(qualifiers);
		final BeanInfo configBean = findConfigBeanInfo(cdiRepositoryConfigurationQualifiers, qualifiers);
		if (configBean == null) {
			String qualifiersStr = qualifiers.stream().map(q -> q.name().toString())
					.collect(Collectors.joining(","));
			LOGGER.warnf(
					"No configuration bean found for Repository Type: %s with qualifiers: %s. Bean no registered.",
					classInfo.name().toString(), qualifiersStr);
		} else {
			beanConfigurators.produce(new BeanConfiguratorBuildItem(context.configure(repoDotName)
					.types(Type.create(repoDotName, org.jboss.jandex.Type.Kind.CLASS))
					.qualifiers(qualifierList.toArray(new AnnotationInstance[qualifierList.size()])).creator(mc -> {
						ResultHandle configInstance = getInstanceHandle(mc, configBean.getIdentifier(),
								configBean.getScope());

						ResultHandle contextInstance = mc
								.newInstance(MethodDescriptor.ofConstructor(RepositoryContext.class));
						mc.ifNull(contextInstance).trueBranch().throwException(UnsatisfiedResolutionException.class,
								"Repository context must not be null");

						// QuarkusSpringDataJpaExtensionProcessorSupport.getRepositoryFragments(repositoryType,
						// cdiRepositoryConfiguration, context);
						ResultHandle repoFragments = mc.invokeStaticMethod(
								MethodDescriptor.ofMethod(RepositoryFragmentsSupport.class,
										"getRepositoryFragments", RepositoryFragments.class, Class.class,
										CdiRepositoryConfiguration.class, RepositoryContext.class),
								mc.loadClass(repoDotName.toString()), configInstance, contextInstance);

						mc.ifNull(repoFragments).trueBranch().throwException(UnsatisfiedResolutionException.class,
								"Repository fragments must not be null");

						ResultHandle emInstance = getInstanceHandle(mc, entityManagerIdentifier,
								entityManagerScope);
						mc.ifNull(emInstance).trueBranch().throwException(UnsatisfiedResolutionException.class,
								"EntityManager not found");

						ResultHandle emCreationInstance = mc.invokeStaticMethod(MethodDescriptor
								.ofMethod(JPAEntityManagerProvider.class, "create", EntityManager.class));
						mc.ifNull(emCreationInstance).trueBranch().throwException(
								UnsatisfiedResolutionException.class, "Creational EntityManager not found");

						// new RepositoryFactory(Class<?> repoClass, EntityManager entityManager,
						// EntityManager entityManagerCreation,
						// CdiRepositoryConfiguration config, RepositoryFragments repoFragments,
						// RepositoryContext context) {
						ResultHandle repoHandle = mc.newInstance(
								MethodDescriptor.ofConstructor(RepositoryFactory.class, Class.class,
										EntityManager.class, EntityManager.class, CdiRepositoryConfiguration.class,
										RepositoryFragments.class, RepositoryContext.class),
								mc.loadClass(repoDotName.toString()), emInstance, emCreationInstance,
								configInstance, repoFragments, contextInstance);
						ResultHandle ret = mc.invokeVirtualMethod(
								MethodDescriptor.ofMethod(RepositoryFactory.class, "create", Object.class),
								repoHandle);
						mc.returnValue(ret);
					})));
		}
	}

	/**
	 * Method to get a {@link BeanInfo} for a given list of qualifiers and
	 * configurations items
	 * 
	 * @param configs
	 * @param qualifiers
	 * @return bean info
	 */
	private static BeanInfo findConfigBeanInfo(List<CdiConfigurationBeanItem> configs, List<AnnotationInstance> qualifiers) {
		List<AnnotationInstance> qList = qualifiers.stream()
				.filter(a -> !QuarkusSpringDataJpaExtensionProcessorSupport.hasScopeOrDefaultOrAny(a.name()))
				.collect(Collectors.toList());
		for (CdiConfigurationBeanItem item : configs) {
			List<AnnotationInstance> listToCheck = new ArrayList<>(qList);
			List<AnnotationInstance> qToCheck = item.getAnnotationsInstance().stream()
					.filter(a -> !QuarkusSpringDataJpaExtensionProcessorSupport.hasScopeOrDefaultOrAny(a.name()))
					.collect(Collectors.toList());

			if (qList.isEmpty()) {
				if (qToCheck.isEmpty())
					return item.getBeanInfo();
			} else {
				for (AnnotationInstance aInstance : qToCheck) {
					for (AnnotationInstance aInstanceCheckList : listToCheck) {
						if (aInstance.toString().equals(aInstanceCheckList.toString())) {
							listToCheck.remove(aInstanceCheckList);
							break;
						}
					}
				}
				if (listToCheck.isEmpty()) {
					// Detected
					return item.getBeanInfo();
				}
			}
		}
		return null;
	}

	/**
	 * Method to write a get instance by bean identifier with the
	 * {@link MethodCreator}
	 * 
	 * @param mc             {@link MethodCreator}
	 * @param beanIdentifier
	 * @param scopeInfo
	 * @return result handle representing the instance
	 */
	private static ResultHandle getInstanceHandle(MethodCreator mc, String beanIdentifier, ScopeInfo scopeInfo) {
		ResultHandle instanceHandle = null;
		ResultHandle arcContainer = mc.invokeStaticMethod(ARC_CONTAINER);
		ResultHandle beanHandle = mc.invokeInterfaceMethod(ARC_CONTAINER_BEAN, arcContainer, mc.load(beanIdentifier));
		mc.ifNull(beanHandle).trueBranch().throwException(UnsatisfiedResolutionException.class,
				"Bean instance not found");

		if (BuiltinScope.DEPENDENT.is(scopeInfo) || BuiltinScope.SINGLETON.is(scopeInfo)) {
			ResultHandle contextHandle = mc.newInstance(
					MethodDescriptor.ofConstructor(CreationalContextImpl.class, Contextual.class), beanHandle);
			// Create a dependent instance
			instanceHandle = mc.invokeInterfaceMethod(CONTEXTUAL_CREATE, beanHandle, contextHandle);
			// But destroy the instance immediately
			mc.invokeInterfaceMethod(CONTEXTUAL_DESTROY, beanHandle, instanceHandle, contextHandle);
		} else {
			// Obtains the instance from the context
			// InstanceHandle<T> handle = Arc.container().instance(bean);
			instanceHandle = mc.invokeInterfaceMethod(ARC_CONTAINER_INSTANCE, arcContainer, beanHandle);
			if (scopeInfo.isNormal()) {
				// We need to unwrap the client proxy ((ClientProxy)
				// handle.get()).arc_contextualInstance();
				ResultHandle proxyHandle = mc.checkCast(mc.invokeInterfaceMethod(INSTANCE_HANDLE_GET, instanceHandle),
						ClientProxy.class);
				instanceHandle = mc.invokeInterfaceMethod(CLIENT_PROXY_CONTEXTUAL_INSTANCE, proxyHandle);
			} else {
				instanceHandle = mc.invokeInterfaceMethod(INSTANCE_HANDLE_GET, instanceHandle);
			}
		}
		return instanceHandle;
	}
	
	
	/**
	 * Check if a class is a {@link Repository}
	 * 
	 * @param classInfo
	 * @param implementorsSet
	 * @return
	 */
	public static boolean isRepository(final ClassInfo classInfo, final Set<DotName> implementorsSet) {
		boolean isInterface = Modifier.isInterface(classInfo.flags());
		if (!isInterface)
			return false;

		DotName name = classInfo.name();
		boolean isSpringDataClass = DotNames.SPRING_DATA_REPOSITORY_CLASSES.contains(name);
		if (isSpringDataClass)
			return false;

		boolean isAnnotated = classInfo.annotations().containsKey(DotNames.SPRING_DATA_REPOSITORY_DEFINITION);
		boolean excludedByAnnotation = classInfo.annotations().containsKey(DotNames.SPRING_DATA_NO_REPOSITORY_BEAN);
		boolean extendsRepository = implementorsSet.contains(name);

		return (extendsRepository || isAnnotated) && !excludedByAnnotation;
	}

	/**
	 * Returns the qualifiers for a given class
	 * 
	 * @param classInfo
	 * @return {@link List} of {@link AnnotationInstance}
	 */
	public static List<AnnotationInstance> getQualifiers(final ClassInfo classInfo, IndexView view) {
		List<AnnotationInstance> qualifiers = new ArrayList<>();
		Map<DotName, List<AnnotationInstance>> annotationMap = classInfo.annotations();
		annotationMap.keySet().stream().filter(a -> isQualifier(a, view)).map(a -> annotationMap.get(a))
				.forEach(qualifiers::addAll);

		// Adds Default qualifier if collection is empty
		if (qualifiers.isEmpty()) {
			qualifiers.add(AnnotationInstance.create(DotNames.QUALIFIER_DEFAULT, null, Collections.emptyList()));
		}

		// Adds Any qualifier for all
		qualifiers.add(AnnotationInstance.create(DotNames.QUALIFIER_ANY, null, Collections.emptyList()));
		return qualifiers;
	}

	/**
	 * Remove {@link Repository} classes annotated with {@link NoRepositoryBean}
	 * 
	 * @param interfacesExtendingCrudRepository
	 */
	public static void removeNoRepositoryBeanClasses(List<ClassInfo> interfacesExtendingCrudRepository) {
		Iterator<ClassInfo> iterator = interfacesExtendingCrudRepository.iterator();
		while (iterator.hasNext()) {
			ClassInfo next = iterator.next();
			if (next.classAnnotation(DotNames.SPRING_DATA_NO_REPOSITORY_BEAN) != null) {
				iterator.remove();
			}
		}
	}

	/**
	 * Check if a {@link DotName} (representing an {@link Annotation}) is a
	 * {@link Qualifier}
	 * 
	 * @param a
	 * @param index
	 * @return
	 */
	public static boolean isQualifier(DotName a, IndexView index) {
		if (hasScope(a))
			return true;
		ClassInfo clazz = index.getClassByName(a);
		if (clazz != null && !DotNames.PRODUCES.equals(a)) {
			Collection<AnnotationInstance> classAnnotations = clazz.classAnnotations();
			for (AnnotationInstance annotation : classAnnotations) {
				if (DotNames.QUALIFIER.equals(annotation.name()) || DotNames.STEREOTYPE.equals(annotation.name())
						|| hasScopeOrDefaultOrAny(annotation.name())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Check if an {@link AnnotationInstance} is a {@link Qualifier}
	 * 
	 * @param a
	 * @param index
	 * @return
	 */
	public static boolean isQualifier(AnnotationInstance a, IndexView index) {
		return isQualifier(a.name(), index);
	}

	/**
	 * Check if an {@link Annotation}) is Scope, {@link Qualifier} or
	 * {@link Stereotype}
	 * 
	 * @param annotation
	 * @return
	 */
	public static boolean isQualifier(Annotation a) {
		if (hasScope(a.annotationType()))
			return true;
		if (a.annotationType().getAnnotation(Qualifier.class) != null
				|| a.annotationType().getAnnotation(Stereotype.class) != null) {
			return true;
		}

		return false;
	}

	/**
	 * Check if a {@link DotName} (representing an {@link Annotation}) is Scope
	 * annotation
	 * 
	 * @param annotation
	 * @return
	 */
	public static boolean hasScope(DotName annotation) {
		return DotNames.APPLICATION_SCOPED.equals(annotation) || DotNames.SESSION_SCOPED.equals(annotation)
				|| DotNames.REQUEST_SCOPED.equals(annotation) || DotNames.DEPENDENT.equals(annotation)
				|| DotNames.SINGLETON.equals(annotation);
	}

	/**
	 * Check if {@link DotName} (representing an {@link Annotation}) is Scope,
	 * {@link Default} or {@link Any} qualifiers
	 * 
	 * @param annotation
	 * @return
	 */
	public static boolean hasScopeOrDefaultOrAny(DotName annotation) {
		return hasScope(annotation) || DotNames.QUALIFIER_DEFAULT.equals(annotation)
				|| DotNames.QUALIFIER_ANY.equals(annotation);
	}

	/**
	 * Check if an {@link Annotation} class is Scope annotation
	 * 
	 * @param annotation
	 * @return
	 */
	private static boolean hasScope(Class<? extends Annotation> annotation) {
		return ApplicationScoped.class.equals(annotation) || SessionScoped.class.equals(annotation)
				|| RequestScoped.class.equals(annotation) || Dependent.class.equals(annotation)
				|| Singleton.class.equals(annotation);
	}

}
