package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Identifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments.ReadFragment;

/**
 * Interface of a data repository that implements read operations over an entity
 * 
 * @author Ariel Carrera
 *
 * @param <T>  Type of the entity
 * @param <ID> Entity's PK
 */
@NoRepositoryBean
public interface ReadRepository<T extends Identifiable<ID>, ID>
		extends Repository<T, ID>, ReadFragment<T, ID>, QueryByExampleExecutor<T> {
}