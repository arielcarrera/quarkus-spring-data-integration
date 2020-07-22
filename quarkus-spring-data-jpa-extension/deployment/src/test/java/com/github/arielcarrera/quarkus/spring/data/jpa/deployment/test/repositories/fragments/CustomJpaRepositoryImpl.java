package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import io.quarkus.arc.Unremovable;

/**
 * Helper Class for custom JPA queries insidea a Repository Fragment
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 */
@ApplicationScoped @Unremovable
public class CustomJpaRepositoryImpl implements CustomJpaRepository {

	public CustomJpaRepositoryImpl() {
		super();
	}

	@Inject
	EntityManager entityManager;

	@Override
	public EntityManager entityManager() {
		return entityManager;
	}

	@Override
	public boolean contains(Object object) {
		return entityManager.contains(object);
	}

}
