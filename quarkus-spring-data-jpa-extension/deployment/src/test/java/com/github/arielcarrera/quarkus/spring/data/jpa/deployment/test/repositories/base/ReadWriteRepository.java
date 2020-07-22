package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base;

import org.springframework.data.repository.NoRepositoryBean;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Identifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments.CreateUpdateFragment;

/**
 * Interface of a data repository that implements read/write operations over an
 * entity
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <PK> Entity's PK
 */
@NoRepositoryBean
public interface ReadWriteRepository<T extends Identifiable<ID>, ID>
		extends ReadRepository<T, ID>, CreateUpdateFragment<T, ID> {

}