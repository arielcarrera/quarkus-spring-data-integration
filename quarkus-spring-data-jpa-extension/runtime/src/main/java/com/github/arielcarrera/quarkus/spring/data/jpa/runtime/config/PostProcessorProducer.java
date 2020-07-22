package com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.DataExceptionPostProcessor;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.TransactionalPostProcessor;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;

/**
 * Proxy post-processors cdi producer
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
public class PostProcessorProducer {

	public PostProcessorProducer() {
		super();
	}
	
	@Produces
	@Singleton @Unremovable @DefaultBean
	public DataExceptionPostProcessor exceptionProcessor() {
		return new DataExceptionPostProcessor();
	}
	
	@Produces
	@Singleton @Unremovable @DefaultBean
	public TransactionalPostProcessor transactionalProcessor() {
		return new TransactionalPostProcessor();
	}
	
}