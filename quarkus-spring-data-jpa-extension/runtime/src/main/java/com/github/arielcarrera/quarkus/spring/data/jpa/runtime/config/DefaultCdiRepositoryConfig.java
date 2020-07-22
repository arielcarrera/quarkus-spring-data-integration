package com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.support.PropertiesBasedNamedQueries;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;

import io.quarkus.arc.DefaultBean;

/**
 * 
 * Default {@link CdiRepositoryConfiguration}
 * 
 * The default implementation read the configuration from Quarkus - MP Config
 * 
 * @author Ariel Carrera
 *
 */
@DefaultBean
@ApplicationScoped
public class DefaultCdiRepositoryConfig implements CdiRepositoryConfiguration {

	/**
	 * DataJpa - Microprofile Configuration
	 */
	@Inject
	DataJpaConfig config;
	
	@Inject
	BeanManager beanManager;
	
	Properties properties;
	List<BeanMetadata<RepositoryProxyPostProcessor>> postProcessorsBeans;
	Optional<BeanMetadata<QueryMethodEvaluationContextProvider>> queryMethodEvalContextProvBean;
	Optional<NamedQueries> namedQueries;
	
	static class BeanMetadata<T>{
		Bean<? extends T> bean;
		Class<?> instanceClass;
		
		public BeanMetadata(Bean<? extends T> bean, Class<?> instanceClass) {
			super();
			this.bean = bean;
			this.instanceClass = instanceClass;
		}
		
	}
	
	/**
	 * Default constructor
	 */
	public DefaultCdiRepositoryConfig() {
		super();
		properties = new Properties();
	}
	
	/**
	 * Init method
	 */
	@PostConstruct
	public void init() {
		
		//cache processor beans
		Optional<List<Class<?>>> postProcessors = config.repository.postProcessors;
		if (postProcessors.isPresent() && !postProcessors.get().isEmpty()) {
			postProcessorsBeans = postProcessors.get().stream().map(c -> getBeanMetadata(c, RepositoryProxyPostProcessor.class)).collect(Collectors.toList());
		} else {
			postProcessorsBeans = Collections.emptyList();
		}
		
		//cache query method evaluation provider beans
		Optional<Class<?>> evalContextProviders = config.repository.queryMethodEvaluationContextProvider;
		if (evalContextProviders.isPresent()) {
			queryMethodEvalContextProvBean = Optional.of(getBeanMetadata(evalContextProviders.get(), QueryMethodEvaluationContextProvider.class));
		} else {
			queryMethodEvalContextProvBean = Optional.empty();
		}
		
		//load named queries properties
		Map<String, String> namedQueriesMap = config.namedQueries;
		if (!namedQueriesMap.isEmpty()) {
			namedQueriesMap.entrySet().stream().forEach(e -> properties.put(e.getKey(), e.getValue()));
			//cache named queries
			namedQueries = Optional.of(new PropertiesBasedNamedQueries(properties));			
		} else {
			namedQueries = Optional.empty();
		}
		
	}
	
	@Override
	public String getRepositoryImplementationPostfix() {
		return config.repository.implementationPostfix;
	}

	@Override
	public List<RepositoryProxyPostProcessor> getRepositoryProxyPostProcessors() {
		if (!postProcessorsBeans.isEmpty()) {
			return postProcessorsBeans.stream().map(b -> getInstance(b.bean, b.instanceClass)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public Optional<QueryMethodEvaluationContextProvider> getEvaluationContextProvider() {
		if (queryMethodEvalContextProvBean.isPresent()) {
			return queryMethodEvalContextProvBean.map(b -> getInstance(b.bean, b.instanceClass));
		}
		return Optional.empty();
	}

	@Override
	public Optional<NamedQueries> getNamedQueries() {
		return namedQueries;
	}

	@Override
	public Optional<Key> getQueryLookupStrategy() {
		return config.repository.queryLookupStrategy;
	}

	@Override
	public Optional<Class<?>> getRepositoryBeanClass() {
		return config.repository.baseClass;
	}

	//helper methods:
	/**
	 * Get a bean representation for a given type
	 * @param <T>
	 * @param instanceClass
	 * @param targetClass
	 * @return
	 */
	private <T> BeanMetadata<T> getBeanMetadata(Class<?> instanceClass, Class<T> targetClass) {
		if (!targetClass.isAssignableFrom(instanceClass)) {
			throw new UnsatisfiedResolutionException("Configuration error: Class " + instanceClass.getName() + " cannot be cast to " +  targetClass.getName() + ". Class: "  );
		}
		Set<Bean<?>> beans = beanManager.getBeans(instanceClass, Default.Literal.INSTANCE);
		if (beans.isEmpty()) {
			throw new UnsatisfiedResolutionException("Configuration error: Class " + instanceClass.getName() + " not found. Target: " + targetClass.getSimpleName());
		}
		@SuppressWarnings("unchecked")
		Bean<T> bean = (Bean<T>) beans.iterator().next();
		
		return new BeanMetadata<T>(bean, instanceClass);
	}
	
	/**
	 * Get an instance for a given bean and type
	 * @param <T>
	 * @param bean
	 * @param instanceClass
	 * @return instance of T
	 */
	private <T> T getInstance(Bean<T> bean, Class<?> instanceClass) {
		CreationalContext<? extends T> ctx = beanManager.createCreationalContext(bean);
		@SuppressWarnings("unchecked")
		T instance = (T) beanManager.getReference(bean, instanceClass, ctx);
		if (instance == null) {
			throw new UnsatisfiedResolutionException("Configuration error: Class " + instanceClass.getName() + " not found. Bean class: " + bean.getBeanClass());
		}

		return instance;
	}
	
}