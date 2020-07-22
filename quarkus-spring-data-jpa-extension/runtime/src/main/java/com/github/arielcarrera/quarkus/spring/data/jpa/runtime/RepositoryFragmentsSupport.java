package com.github.arielcarrera.quarkus.spring.data.jpa.runtime;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryFragmentConfiguration;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.data.util.Optionals;

/**
 * Helper class related to Repository Fragments
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public class RepositoryFragmentsSupport {

	/**
	 * Get repository fragments by a given repository class
	 * 
	 * @param repositoryClass
	 * @param configuration
	 * @param context
	 * @return value object representing an ordered list of
	 *         {@link RepositoryFragment fragments}
	 */
	public static RepositoryFragments getRepositoryFragments(Class<?> repositoryClass,
			CdiRepositoryConfiguration configuration, RepositoryContext context) {

		Optional<Bean<?>> customImplementationBean = findCustomImplementation(repositoryClass, configuration, context);
		Optional<Object> customImplementation = customImplementationBean
				.map(b -> getContextualInstance(b, b.getBeanClass()));

		List<RepositoryFragment<?>> repositoryFragments = findRepositoryFragments(configuration, repositoryClass,
				context);

		RepositoryFragments customRepoFragments = customImplementation.map(RepositoryFragments::just)
				.orElseGet(RepositoryFragments::empty);

		return RepositoryFragments.from(repositoryFragments).append(customRepoFragments);
	}

	/**
	 * Find a custom implementation for a given repository class / configuration
	 * 
	 * @param repositoryClass
	 * @param configuration
	 * @param context
	 * @return
	 */
	private static Optional<Bean<?>> findCustomImplementation(Class<?> repositoryClass,
			CdiRepositoryConfiguration configuration, RepositoryContext context) {
		return context.getCustomImplementationClass(repositoryClass, configuration).flatMap(type -> getBean(type));
	}

	/**
	 * Find repository fragments for the given repository class / configuration
	 * 
	 * @param config          cdi repository configuration
	 * @param repositoryClass
	 * @param context         repository-context
	 * @return list of repository fragments for the given repository class and
	 *         configuration
	 */
	@SuppressWarnings("unchecked")
	private static List<RepositoryFragment<?>> findRepositoryFragments(CdiRepositoryConfiguration config,
			Class<?> repositoryClass, RepositoryContext context) {
		Stream<RepositoryFragmentConfiguration> fragmentConfigurations = context.findRepositoryFragmentsConfigs(config,
				repositoryClass);

		List<RepositoryFragment<?>> collect = fragmentConfigurations.flatMap(it -> {
			Class<Object> interfaceClass = (Class<Object>) lookupFragmentInterface(repositoryClass,
					it.getInterfaceName());

			Class<?> implementationClass = context.loadClass(it.getClassName());
			Optional<Bean<?>> bean = getBean(implementationClass);

			return Optionals.toStream(bean.map(b -> getContextualInstance(b, b.getBeanClass()))
					.map(implementation -> RepositoryFragment.implemented(interfaceClass, implementation)));

		}).collect(Collectors.toList());
		return collect;
	}

	private static Class<?> lookupFragmentInterface(Class<?> repositoryClass, String interfaceName) {
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		classSet.addAll(lookupHierarchy(repositoryClass));
		return classSet.stream().filter(it -> it.getName().equals(interfaceName)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(String.format("Type %s not found in %s", interfaceName,
						Arrays.asList(repositoryClass.getInterfaces()))));

	}

	private static Set<Class<?>> lookupHierarchy(Class<?> cls) {
		if (cls == null || cls.equals(Object.class)) {
			throw new IllegalArgumentException("Invalid parameters during repository fragment search");
		}
		Set<Class<?>> interfacesSet = new HashSet<Class<?>>();

		for (final Class<?> clazz : cls.getInterfaces()) {
			if (!interfacesSet.contains(clazz)) {
				interfacesSet.add(clazz);
				lookupHierarchy(clazz, interfacesSet);
			}
		}
		return interfacesSet;
	}

	/**
	 * Lookup in the hierarchy of the class for repository fragment interfaces
	 * 
	 * @param cls
	 * @param interfaces
	 */
	private static void lookupHierarchy(Class<?> cls, Set<Class<?>> interfaces) {
		if (cls == null || cls.equals(Object.class) || interfaces == null) {
			throw new IllegalArgumentException("Invalid parameters during repository fragment search");
		}
		for (final Class<?> clazz : cls.getInterfaces()) {
			if (!interfaces.contains(clazz)) {
				interfaces.add(clazz);
				lookupHierarchy(clazz, interfaces);
			}
		}
	}

	/**
	 * method that gets a contextual instance for the given bean / type
	 * 
	 * @param <S>
	 * @param bean
	 * @param clazz
	 * @return instance
	 */
	@SuppressWarnings("unchecked")
	private static <S> S getContextualInstance(Bean<S> bean, Class<?> clazz) {
		BeanManager beanManager = CDI.current().getBeanManager();
		CreationalContext<S> creationalContext = beanManager.createCreationalContext(bean);
		return (S) beanManager.getReference(bean, clazz, creationalContext);
	}

	/**
	 * method that get a bean representation for a given type
	 * 
	 * @param beanType
	 * @return optional of the bean representation
	 */
	private static Optional<Bean<?>> getBean(Class<?> beanType) {
		BeanManager beanManager = CDI.current().getBeanManager();
		Optional<Bean<?>> findFirst = beanManager.getBeans(beanType, Default.Literal.INSTANCE).stream().findFirst();
		return findFirst;
	}

}
