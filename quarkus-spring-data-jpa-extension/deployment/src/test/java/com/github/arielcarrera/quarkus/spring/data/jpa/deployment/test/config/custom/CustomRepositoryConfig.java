package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.custom;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.CustomBaseRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.DataExceptionPostProcessor;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.TransactionalPostProcessor;

/**
 * 
 * Custom Configuration of Spring Data for CDI
 * (Custom Base Repository)
 * 
 * @author Ariel Carrera
 *
 */
@CustomConfig
@ApplicationScoped
public class CustomRepositoryConfig implements CdiRepositoryConfiguration {

	public CustomRepositoryConfig() {
		super();
	}
	
	@Override
	public String getRepositoryImplementationPostfix() {
		return "Impl";
	}

	@Override
	public List<RepositoryProxyPostProcessor> getRepositoryProxyPostProcessors() {
		return Arrays.asList(new DataExceptionPostProcessor(), new TransactionalPostProcessor());
	}

	@Override
	public Optional<Class<?>> getRepositoryBeanClass() {
		return Optional.of(CustomBaseRepository.class);
	}
	
}