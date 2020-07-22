package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import org.springframework.data.repository.Repository;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

public interface ItemCustomBaseRepository extends Repository<ItemEntity,Integer> {
	
	ItemEntity save(ItemEntity entity);
}
