package com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.quarkus.arc.Arc;
import io.quarkus.arc.DefaultBean;
import io.quarkus.hibernate.orm.runtime.JPAConfig;

/**
 * Entity Manager provider
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
@DefaultBean
public class JPAEntityManagerProvider {

	@Inject
	@PersistenceContext
	EntityManager entityManager;

	public EntityManager get() {
		return entityManager;
	}

	public static EntityManager create() {
		JPAConfig jpaConfig = Arc.container().instance(JPAConfig.class, Default.Literal.INSTANCE).get();
		String pUnit = jpaConfig.getPersistenceUnits().iterator().next();
		return jpaConfig.getEntityManagerFactory(pUnit).createEntityManager();
	}

}