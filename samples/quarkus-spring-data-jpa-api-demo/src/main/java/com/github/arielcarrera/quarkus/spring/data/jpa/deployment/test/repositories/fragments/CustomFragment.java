package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments;

import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

/**
 * Custom Fragment
 * 
 * @author Ariel Carrera
 *
 * @param <T>  Type of the entity
 * @param <ID> Entity's PK
 */
@Transactional(TxType.REQUIRED)
public interface CustomFragment {

	Optional<ItemEntity> custom(Integer id);

}