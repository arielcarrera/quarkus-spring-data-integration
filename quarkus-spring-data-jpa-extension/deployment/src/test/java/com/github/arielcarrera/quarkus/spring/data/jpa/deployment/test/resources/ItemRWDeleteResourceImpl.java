package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemRWDeleteRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadWriteDeleteRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractRWDeleteResourceImpl;

public class ItemRWDeleteResourceImpl extends AbstractRWDeleteResourceImpl<ItemEntity, Integer, Integer> implements ItemRWDeleteResource {

	@Inject
	ItemRWDeleteRepository repository;

	@Inject
	UserTransaction tx;

	public ItemRWDeleteResourceImpl() {
		super();
	}

	@Override
	public ReadWriteDeleteRepository<ItemEntity, Integer> getRepository() {
		return repository;
	}

	@Override
	public UserTransaction getTx() {
		return tx;
	}

}
