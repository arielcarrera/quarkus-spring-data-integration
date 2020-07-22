package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.custom;

import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.springframework.data.repository.query.ExtensionAwareQueryMethodEvaluationContextProvider;

import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.DataExceptionPostProcessor;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.TransactionalPostProcessor;

import io.quarkus.arc.Unremovable;

@ApplicationScoped @Unremovable
public class CustomConfigurationProducer {

	public CustomConfigurationProducer() {
		super();
	}
	
	@Produces
	@Dependent @Unremovable
	public ExtensionAwareQueryMethodEvaluationContextProvider evaluationProvider() {
		return new ExtensionAwareQueryMethodEvaluationContextProvider(Collections.emptyList());
	}
	
	@Produces
	@Dependent @Unremovable
	public DataExceptionPostProcessor exceptionProcessor() {
		return new DataExceptionPostProcessor();
	}
	
	@Produces
	@Dependent @Unremovable
	public TransactionalPostProcessor transactionalProcessor() {
		return new TransactionalPostProcessor();
	}
	
	@Produces
	@Dependent @Unremovable
	public CustomQueryCreationListener queryCreationListener() {
		return new CustomQueryCreationListener();
	}
	
}