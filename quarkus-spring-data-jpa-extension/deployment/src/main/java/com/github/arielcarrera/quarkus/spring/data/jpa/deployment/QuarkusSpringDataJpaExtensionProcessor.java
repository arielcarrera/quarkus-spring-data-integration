package com.github.arielcarrera.quarkus.spring.data.jpa.deployment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Type;
import org.jboss.logging.Logger;
import org.springframework.data.domain.Auditable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.RepositoryConfigurationBeanBuildItem.CdiConfigurationBeanItem;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.RepositoryConfigurationBuildItem.CdiConfigurationItem;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.RepositoryContext;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.RepositoryFactory;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DataJpaConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DataJpaRepositoryConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DataJpaRepositoryConfig.ClassConverter;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.JPAEntityManagerProvider;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.PostProcessorProducer;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.DataExceptionPostProcessor;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.TransactionalPostProcessor;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanRegistrationPhaseBuildItem;
import io.quarkus.arc.deployment.BeanRegistrationPhaseBuildItem.BeanConfiguratorBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.arc.processor.BeanRegistrar.RegistrationContext;
import io.quarkus.arc.processor.ScopeInfo;
import io.quarkus.deployment.Feature;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.hibernate.orm.deployment.IgnorableNonIndexedClasses;
import io.quarkus.narayana.jta.runtime.interceptor.TransactionalInterceptorMandatory;
import io.quarkus.narayana.jta.runtime.interceptor.TransactionalInterceptorNever;
import io.quarkus.narayana.jta.runtime.interceptor.TransactionalInterceptorNotSupported;
import io.quarkus.narayana.jta.runtime.interceptor.TransactionalInterceptorRequired;
import io.quarkus.narayana.jta.runtime.interceptor.TransactionalInterceptorRequiresNew;
import io.quarkus.narayana.jta.runtime.interceptor.TransactionalInterceptorSupports;

/**
 * Extension for the Spring Data Jpa integration in Quarkus
 * 
 * Allows for better compatibility and minimal rewrite code required for
 * migrating "Spring Boot / Spring Data Jpa" based services to Quarkus.
 * Facilitates integration and compatibility by increasing performance but
 * keeping a complete set of "Spring Data Jpa" features available to Quarkus's
 * community at a compatibility level never seen before.
 * 
 * Also, this extension eliminates restrictions and issues that currently
 * restrict the possibilities of an easy migration to Quarkus in many projects
 * implemented with "Spring" (for example, those that use repositories with the
 * commonly @Query annotation, native queries, named queries, Hints, use of
 * Future, Streamable, Persistable and custom repository base class, etc.).
 * 
 * This extension opens the door to a quick integration of the rest of the
 * Spring Data projects while the quarkus team works in Panache, which is surely
 * the option that one should choose if one has the possibility of starting a
 * project from scratch!
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
class QuarkusSpringDataJpaExtensionProcessor {

	private static final Logger LOGGER = Logger.getLogger(QuarkusSpringDataJpaExtensionProcessor.class.getName());

	/**
	 * Feature
	 * 
	 * @return
	 */
	@BuildStep
	FeatureBuildItem feature() {
		return new FeatureBuildItem(Feature.SPRING_DATA_JPA);
	}

	/**
	 * Ignore classes (never used during build)
	 * 
	 * @param ignorablesIndex
	 */
	@BuildStep
	void addIgnorables(BuildProducer<IgnorableNonIndexedClasses> ignorablesIndex) {
		Set<String> ignorables = new HashSet<>();
		ignorables.add(Auditable.class.getName());
		ignorables.add(Persistable.class.getName());
		ignorablesIndex.produce(new IgnorableNonIndexedClasses(ignorables));
	}

	/**
	 * Adds bean classes to be analyzed and marks them as 'unremovable'
	 * 
	 * @param repositoryTypesItem
	 * @param additionalBeans
	 */
	@BuildStep
	public void addAdditionalBeans(RepositoryTypesBuildItem repositoryTypesItem,
			BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
		AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder();
		builder.setUnremovable();
		builder.setDefaultScope(DotNames.DEPENDENT);
		builder.addBeanClasses(TransactionalInterceptorSupports.class, TransactionalInterceptorNever.class, 
				TransactionalInterceptorRequired.class, TransactionalInterceptorRequiresNew.class, 
				TransactionalInterceptorMandatory.class, TransactionalInterceptorNotSupported.class,
				//others
				Repository.class,CrudRepository.class, PagingAndSortingRepository.class,
				JpaRepository.class, QueryByExampleExecutor.class,
				JpaSpecificationExecutor.class, SimpleJpaRepository.class,
				CdiRepositoryConfiguration.class,
				DataJpaRepositoryConfig.class,
				ClassConverter.class,
				JPAEntityManagerProvider.class
				);
		
		additionalBeans.produce(builder.build());
		Map<ClassInfo, List<AnnotationInstance>> repositoryTypes = repositoryTypesItem.getRepositoryTypes();
		if (!repositoryTypes.isEmpty()) {
			builder = AdditionalBeanBuildItem.builder();
			builder.setUnremovable();
			for (ClassInfo classInfo : repositoryTypes.keySet()) {
				builder.addBeanClass(classInfo.name().toString());
			}
			additionalBeans.produce(builder.build());
		}

		builder = AdditionalBeanBuildItem.builder();
		builder.setUnremovable();
		builder.setDefaultScope(DotNames.SINGLETON);
		builder.addBeanClass(PostProcessorProducer.class);
		builder.addBeanClass(DataExceptionPostProcessor.class);
		builder.addBeanClass(TransactionalPostProcessor.class);
		builder.addBeanClass(DefaultCdiRepositoryConfig.class);
		additionalBeans.produce(builder.build());
	}

	/**
	 * Index Spring Data {@link Repository} classes to get generic types
	 * 
	 * @param additionalIndexedClasses
	 */
	@BuildStep
	void addClassesToIndex(BuildProducer<AdditionalIndexedClassesBuildItem> additionalIndexedClasses) {
		additionalIndexedClasses.produce(new AdditionalIndexedClassesBuildItem(Repository.class.getName(),
				CrudRepository.class.getName(), PagingAndSortingRepository.class.getName(),
				JpaRepository.class.getName(), QueryByExampleExecutor.class.getName(),
				JpaSpecificationExecutor.class.getName(), SimpleJpaRepository.class.getName(),
				CdiRepositoryConfiguration.class.getName(), PostProcessorProducer.class.getName(),
				
				Repository.class.getName(),CrudRepository.class.getName(), PagingAndSortingRepository.class.getName(),
				JpaRepository.class.getName(), QueryByExampleExecutor.class.getName(),
				JpaSpecificationExecutor.class.getName(), SimpleJpaRepository.class.getName(),
				CdiRepositoryConfiguration.class.getName(), 
				DataJpaConfig.class.getName(), DataJpaRepositoryConfig.class.getName(),
				ClassConverter.class.getName(),
				DefaultCdiRepositoryConfig.class.getName(),
				JPAEntityManagerProvider.class.getName()
				));
	}

	/**
	 * Step to find all possibles {@link Repository} types
	 * 
	 * @param indexItemO
	 * @param repositoryTypesIndex
	 */
	@BuildStep
	void findRepositoryTypes(CombinedIndexBuildItem indexItem,
			BuildProducer<RepositoryTypesBuildItem> repositoryTypesIndex) {
		IndexView index = indexItem.getIndex();
		LOGGER.info("Looking for repository types");

		// find Repository implementors
		repositoryTypesIndex.produce(new RepositoryTypesBuildItem(
				QuarkusSpringDataJpaExtensionProcessorSupport.scanRepositoryTypes(indexItem, index)));
	}

	/**
	 * Step to find all repository configurations
	 * 
	 * @param indexItem
	 * @param cdiRepositoryConfigurations
	 * @param unremovableItem
	 */
	@BuildStep
	void findRepositoryConfigurations(CombinedIndexBuildItem indexItem,
			BuildProducer<RepositoryConfigurationBuildItem> cdiRepositoryConfigurations,
			BuildProducer<UnremovableBeanBuildItem> unremovableItem) {

		LOGGER.info("Looking for entity managers and repository configurations");

		List<CdiConfigurationItem> cdiRepositoryConfigurationQualifiers = new ArrayList<>();

		IndexView index = indexItem.getIndex();

		// looking in producers
		QuarkusSpringDataJpaExtensionProcessorSupport.scanRepoConfigurationProducers(cdiRepositoryConfigurationQualifiers, index);

		// looking in annotated beans
		QuarkusSpringDataJpaExtensionProcessorSupport.scanRepoConfigurationBeans(cdiRepositoryConfigurationQualifiers, index);

		if (cdiRepositoryConfigurationQualifiers.isEmpty()) {
			throw new UnsatisfiedResolutionException("Repository configuration is required");
		}
		cdiRepositoryConfigurations.produce(new RepositoryConfigurationBuildItem(cdiRepositoryConfigurationQualifiers));

		List<String> classNames = cdiRepositoryConfigurationQualifiers.stream()
				.map(c -> c.getClassInfo().name().toString()).collect(Collectors.toList());
		unremovableItem
				.produce(UnremovableBeanBuildItem.beanClassNames(classNames.toArray(new String[classNames.size()])));
	}



	/**
	 * Step to find entity managers / repository configuration beans
	 * 
	 * @param beanRegistrationPhase
	 * @param configsItem
	 * @param entityManagersItem
	 * @param configBeanItem
	 * @param configuratorProducer
	 */
	@BuildStep
	void findEntityManagersAndConfigBeans(BeanRegistrationPhaseBuildItem beanRegistrationPhase,
			RepositoryConfigurationBuildItem configsItem, BuildProducer<EntityManagerBeansBuildItem> entityManagersItem,
			BuildProducer<RepositoryConfigurationBeanBuildItem> configBeanItem,
			BuildProducer<BeanConfiguratorBuildItem> configuratorProducer) {

		RegistrationContext context = beanRegistrationPhase.getContext();

		LOGGER.info("looking for entity manager beans");
		BeanInfo emBean = null;
		for (BeanInfo bean : context.beans().producers()) {
			System.out.println("BeanInfo:" + bean.getName() + "," +  bean.getTypes().toString());
			if (bean.getTypes().contains(Type.create(DotNames.ENTITY_MANAGER, org.jboss.jandex.Type.Kind.CLASS))) {
				emBean = bean;
				break;
			}
		}
		if (emBean == null) {
			throw new UnsatisfiedResolutionException("Entity Manager bean not found");
		}
		Set<BeanInfo> emSet = new HashSet<>();
		emSet.add(emBean);
		entityManagersItem.produce(new EntityManagerBeansBuildItem(emSet));

		LOGGER.info("looking for repository configuration beans");

		List<CdiConfigurationBeanItem> list = new ArrayList<>();
		List<CdiConfigurationItem> configs = configsItem.getCdiRepositoryConfigurationQualifiers();
		configs.stream()
				.forEach(item -> context.beans().withBeanType(item.getClassInfo().name()).forEach(beanInfo -> list.add(
						new CdiConfigurationBeanItem(beanInfo, item.getClassInfo(), item.getAnnotationsInstance()))));
		if (list.isEmpty()) {
			throw new UnsatisfiedResolutionException("Repository configuration bean not found");
		}
		QuarkusSpringDataJpaExtensionProcessorSupport.removeUnusedDefaultBeans(list);
		configBeanItem.produce(new RepositoryConfigurationBeanBuildItem(list));
	}

	/**
	 * Step to configure 'unremovable' beans
	 * 
	 * @param indexItem
	 * @param repositoryTypesItem
	 * @param unremovableItem
	 */
	@BuildStep
	void addUnremovables(CombinedIndexBuildItem indexItem, RepositoryTypesBuildItem repositoryTypesItem,
			BuildProducer<UnremovableBeanBuildItem> unremovableItem) {

		IndexView index = indexItem.getIndex();
		Set<ClassInfo> repoTypes = repositoryTypesItem.getRepositoryTypes().keySet();

		Set<DotName> allInterfaces = repoTypes.stream().map(c -> c.interfaceNames()).flatMap(List::stream)
				.collect(Collectors.toSet());

		Set<String> all = allInterfaces.stream().map(i -> index.getAllKnownImplementors(i)).flatMap(Collection::stream)
				.map(c -> c.name().toString()).collect(Collectors.toSet());

		allInterfaces.stream().map(i -> i.toString()).collect(Collectors.toCollection(() -> all));

		all.addAll(Arrays.asList(DefaultCdiRepositoryConfig.class.getName(), RepositoryContext.class.getName(),
				JPAEntityManagerProvider.class.getName()));
		unremovableItem.produce(UnremovableBeanBuildItem.beanClassNames(all));
		unremovableItem.produce(UnremovableBeanBuildItem.beanTypes(EntityManager.class, EntityManagerFactory.class));
	}

	/**
	 * Step to create synthetic bean for {@link RepositoryFactory} invocation
	 * 
	 * @param beanRegistrationPhase
	 * @param repositoryTypesItem
	 * @param entityManagerBeansBuildItem
	 * @param repositoryConfigsItem
	 * @param beanConfigurators
	 */
	@BuildStep
	void syntheticBean(BeanRegistrationPhaseBuildItem beanRegistrationPhase,
			RepositoryTypesBuildItem repositoryTypesItem, EntityManagerBeansBuildItem entityManagerBeansBuildItem,
			RepositoryConfigurationBeanBuildItem repositoryConfigsItem,
			BuildProducer<BeanConfiguratorBuildItem> beanConfigurators) {
		RegistrationContext context = beanRegistrationPhase.getContext();

		BeanInfo emBean = entityManagerBeansBuildItem.getEntityManagerBeans().iterator().next();

		final String entityManagerIdentifier = emBean.getIdentifier();
		final ScopeInfo entityManagerScope = emBean.getScope();
		final List<CdiConfigurationBeanItem> cdiRepositoryConfigurationQualifiers = repositoryConfigsItem
				.getCdiRepositoryConfigurationQualifiers();
		Map<ClassInfo, List<AnnotationInstance>> repositoryTypes = repositoryTypesItem.getRepositoryTypes();

		for (ClassInfo classInfo : repositoryTypes.keySet()) {
			QuarkusSpringDataJpaExtensionProcessorSupport.writeSyntheticBean(beanConfigurators, context, entityManagerIdentifier, entityManagerScope,
					cdiRepositoryConfigurationQualifiers, repositoryTypes, classInfo);
		}
	}
	
	/**
	 * Native image support: delay class initialization
	 * @return
	 */
	@BuildStep
    void delayInitialization(BuildProducer<RuntimeInitializedClassBuildItem> delayInitBuildItem) {
		delayInitBuildItem.produce(new RuntimeInitializedClassBuildItem("org.springframework.beans.BeanUtils$KotlinDelegate"));
		delayInitBuildItem.produce(new RuntimeInitializedClassBuildItem("org.springframework.core.io.VfsUtils"));
//		delayInitBuildItem.produce(new RuntimeInitializedClassBuildItem("org.springframework.cglib.core.ReflectUtils"));
    }

}
