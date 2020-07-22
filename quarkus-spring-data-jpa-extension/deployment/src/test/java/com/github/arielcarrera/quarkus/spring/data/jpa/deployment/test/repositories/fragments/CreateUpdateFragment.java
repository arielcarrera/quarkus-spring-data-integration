package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Identifiable;

/**
 * Fragment interface of a data repository that implements write operations over an entity
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PK
 */
@Transactional(TxType.REQUIRED)
public interface CreateUpdateFragment<T extends Identifiable<ID>, ID> {

	/**
	 * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
	 * entity instance completely.
	 *
	 * @param entity must not be {@literal null}.
	 * @return the saved entity will never be {@literal null}.
	 */
	<S extends T> S save(S entity);

	/**
	 * Saves all given entities.
	 *
	 * @param entities must not be {@literal null}.
	 * @return the saved entities will never be {@literal null}.
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 */
	<S extends T> Iterable<S> saveAll(Iterable<S> entities);

	/**
	 * Saves an entity and flushes changes instantly.
	 *
	 * @param entity
	 * @return the saved entity
	 */
	<S extends T> S saveAndFlush(S entity);

	/**
	 * Flushes all pending changes to the database.
	 */
	void flush();
}