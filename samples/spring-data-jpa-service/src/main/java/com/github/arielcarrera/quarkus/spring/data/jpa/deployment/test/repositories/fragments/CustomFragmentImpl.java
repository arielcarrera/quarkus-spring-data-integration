package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments;

import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

/**
 * Custom Fragment Implementation
 * 
 * @author Ariel Carrera
 *
 * @param <T>  Type of the entity
 * @param <ID> Entity's PK
 */
@Transactional(TxType.REQUIRED)
public class CustomFragmentImpl implements CustomFragment {

	@Inject
	EntityManager em;

	@Override
	public Optional<ItemEntity> custom(Integer id) {
		return Optional.ofNullable(em.find(ItemEntity.class, id));
	}

}