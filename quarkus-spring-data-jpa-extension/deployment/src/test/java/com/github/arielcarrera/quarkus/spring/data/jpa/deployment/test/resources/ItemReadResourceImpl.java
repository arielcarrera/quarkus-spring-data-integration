package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemReadRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractReadResourceImpl;

public class ItemReadResourceImpl extends AbstractReadResourceImpl<ItemEntity, Integer, Integer> implements ItemReadResource {

	@Inject
	ItemReadRepository testRepository;

	@Inject
	UserTransaction tx;

	public ItemReadResourceImpl() {
		super();
	}

	@Override
	public ReadRepository<ItemEntity, Integer> getRepository() {
		return testRepository;
	}

	@Override
	public UserTransaction getTx() {
		return tx;
	}

}
