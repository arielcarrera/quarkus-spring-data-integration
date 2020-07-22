package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import org.springframework.data.repository.Repository;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;


public interface ItemMultipleConfig1Repository extends Repository<ItemEntity,Integer> {
	
	ItemEntity save(ItemEntity entity);
}
