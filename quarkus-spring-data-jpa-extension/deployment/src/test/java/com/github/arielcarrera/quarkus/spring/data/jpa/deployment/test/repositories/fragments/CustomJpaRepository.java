package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Interface that provides custom methods for work with JPA inside Repository Fragments
 *  
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PKzz
 */
@Transactional
public interface CustomJpaRepository {

	EntityManager entityManager();
	
	boolean contains(Object object);
	
}
