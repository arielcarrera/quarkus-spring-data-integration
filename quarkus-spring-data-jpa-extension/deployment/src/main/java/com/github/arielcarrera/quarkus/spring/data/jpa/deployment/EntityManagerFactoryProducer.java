package com.github.arielcarrera.quarkus.spring.data.jpa.deployment;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import io.quarkus.arc.DefaultBean;

/**
 * {@link EntityManagerFactory} producer
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
public class EntityManagerFactoryProducer {

	@Inject
	private BeanManager beanManager;

	@Produces @DefaultBean
	@ApplicationScoped
	public EntityManagerFactory produceEntityManagerFactoryCreation() {
		Map<String, Object> props = new HashMap<>();
		props.put("javax.persistence.bean.manager", beanManager);
		return Persistence.createEntityManagerFactory("persistenceUnit", props);
	}

	public void closeCreation(@Disposes EntityManagerFactory emf) {
		emf.close();
	}
}
