package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemRWRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadWriteRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractRWResourceImpl;

public class ItemRWResourceImpl extends AbstractRWResourceImpl<ItemEntity, Integer, Integer> implements ItemRWResource {

	@Inject
	ItemRWRepository testRepository;

	@Inject
	UserTransaction tx;

	public ItemRWResourceImpl() {
		super();
	}

	@Override
	public ReadWriteRepository<ItemEntity, Integer> getRepository() {
		return testRepository;
	}

	@Override
	public UserTransaction getTx() {
		return tx;
	}

}
