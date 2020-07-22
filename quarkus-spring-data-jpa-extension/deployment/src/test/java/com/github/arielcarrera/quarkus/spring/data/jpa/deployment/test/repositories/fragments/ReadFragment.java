package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Identifiable;

/**
 * Fragment interface of a data repository that implements read operations over an entity
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PK
 */
@Transactional(TxType.REQUIRED)
public interface ReadFragment<T extends Identifiable<ID>, ID> 
	extends QueryByExampleFragment<T>, CustomJpaRepository {

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none found
	 * @throws IllegalArgumentException if {@code id} is {@literal null}.
	 */
	Optional<T> findById(ID id);
	
	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or null
	 * @throws IllegalArgumentException if {@code id} is {@literal null}.
	 */
	T findOneById(ID id);

	/**
	 * Returns whether an entity with the given id exists.
	 *
	 * @param id must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@code id} is {@literal null}.
	 */
	boolean existsById(ID id);

	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	List<T> findAll();
	
	/**
	 * Realiza la busqueda con ordenamiento
	 * @param sort
	 * @return all entities roted
	 */
	List<T> findAll(Sort sort);
	
	/**
	 * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
	 *
	 * @param pageable
	 * @return a page of entities
	 */
	Page<T> findAll(Pageable pageable);

	/**
	 * Returns all instances of the type with the given Ids.
	 *
	 * @param ids
	 * @return
	 */
	List<T> findAllById(Iterable<ID> ids);
	
	/**
	 * Returns the number of entities available.
	 *
	 * @return the number of entities
	 */
	long count();
	
	/**
	 * Returns a reference to the entity with the given identifier. Depending on how the JPA persistence provider is
	 * implemented this is very likely to always return an instance and throw an
	 * {@link javax.persistence.EntityNotFoundException} on first access. Some of them will reject invalid identifiers
	 * immediately.
	 *
	 * @param id must not be {@literal null}.
	 * @return a reference to the entity with the given identifier.
	 * @see EntityManager#getReference(Class, Object) for details on when an exception is thrown.
	 */
	T getOne(ID id);
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.QueryByExampleExecutor#findAll(org.springframework.data.domain.Example)
	 */
	@Override
	<S extends T> List<S> findAll(Example<S> example);
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.QueryByExampleExecutor#findAll(org.springframework.data.domain.Example, org.springframework.data.domain.Sort)
	 */
	@Override
	<S extends T> List<S> findAll(Example<S> example, Sort sort);
}