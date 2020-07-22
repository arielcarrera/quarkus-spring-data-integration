package com.github.arielcarrera.quarkus.spring.data.jpa.runtime;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Stereotype;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

/**
 * Factory of {@link Repository} instances
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public class RepositoryFactory {

	public static final CdiRepositoryConfiguration DEFAULT_CONFIGURATION = new CdiRepositoryConfiguration() {};

	private final Class<?> repoClass;
	private final EntityManager entityManager;
	private final EntityManager entityManagerCreation;
	private final CdiRepositoryConfiguration config;
	private final RepositoryFragments repositoryFragments;

	/**
	 * Create a new Jpa Repository Creator
	 * 
	 * @param repoClass
	 * @param entityManager
	 * @param config
	 * @param repoFragments
	 * @param context
	 */
	public RepositoryFactory(Class<?> repoClass, EntityManager entityManager, CdiRepositoryConfiguration config,
			RepositoryFragments repoFragments, RepositoryContext context) {
		this(repoClass, entityManager, null, config, repoFragments, context);
	}

	/**
	 * 
	 * Create a new Jpa Repository Creator
	 * 
	 * @param repoClass
	 * @param entityManager
	 * @param entityManagerCreation
	 * @param config
	 * @param repoFragments
	 * @param context
	 */
	public RepositoryFactory(Class<?> repoClass, EntityManager entityManager, EntityManager entityManagerCreation,
			// Optional<CustomRepositoryImplementationDetector> detector,
			CdiRepositoryConfiguration config, RepositoryFragments repoFragments, RepositoryContext context) {

		Assert.notNull(repoClass, "Repository type must not be null");
		Assert.notNull(entityManager, "EntityManager bean must not be null");
		Assert.notNull(entityManagerCreation, "entityManagerCreation bean must not be null");
		Assert.isTrue(repoClass.isInterface(), "Repository class must be an interface");
		this.repoClass = repoClass;
		this.entityManager = entityManager != null ? entityManager : entityManagerCreation;
		this.entityManagerCreation = entityManagerCreation != null ? entityManagerCreation : entityManager;
		this.config = config != null ? config : DEFAULT_CONFIGURATION;
		Set<Annotation> l = Arrays.asList(repoClass.getAnnotations()).stream().filter(a -> isQualifier(a))
				.collect(Collectors.toSet());
		//add default qualifier
		if (l.isEmpty()) {
			l.add(Default.Literal.INSTANCE);
		}
		this.repositoryFragments = repoFragments;

	}

	/**
	 * Create an instance
	 * 
	 * @return repository instance
	 */
	public Object create() {
		JpaRepositoryFactory factory = new JpaRepositoryFactory(entityManager, entityManagerCreation);
		customize(factory, config);
		return factory.getRepository(repoClass, repositoryFragments);
	}

	/**
	 * Apply configurations from {@link CdiRepositoryConfiguration} to
	 * {@link RepositoryFactorySupport}
	 * 
	 * @param springRepositoryFactory 
	 * @param config
	 */
	public static void customize(RepositoryFactorySupport springRepositoryFactory,
			CdiRepositoryConfiguration config) {
		config.getEvaluationContextProvider().ifPresent(springRepositoryFactory::setEvaluationContextProvider);
		config.getNamedQueries().ifPresent(springRepositoryFactory::setNamedQueries);
		config.getQueryLookupStrategy().ifPresent(springRepositoryFactory::setQueryLookupStrategyKey);
		config.getRepositoryBeanClass().ifPresent(springRepositoryFactory::setRepositoryBaseClass);
		config.getRepositoryProxyPostProcessors().forEach(springRepositoryFactory::addRepositoryProxyPostProcessor);
		config.getQueryCreationListeners().forEach(springRepositoryFactory::addQueryCreationListener);
	}

	/**
	 * Check if annotations is a qualifier
	 * 
	 * @param annotation
	 * @return true if the annotation is a qualifier
	 */
	public static boolean isQualifier(Annotation annotation) {
		if (hasScope(annotation.annotationType()))
			return true;
		if (annotation.annotationType().getAnnotation(Qualifier.class) != null
				|| annotation.annotationType().getAnnotation(Stereotype.class) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Helper method to check if an annotation is Scope / pseudo-scope
	 * @param annotation
	 * @return
	 */
	private static boolean hasScope(Class<? extends Annotation> annotation) {
		return ApplicationScoped.class.equals(annotation) || SessionScoped.class.equals(annotation)
				|| RequestScoped.class.equals(annotation) || Dependent.class.equals(annotation)
				|| Singleton.class.equals(annotation);
	}
}