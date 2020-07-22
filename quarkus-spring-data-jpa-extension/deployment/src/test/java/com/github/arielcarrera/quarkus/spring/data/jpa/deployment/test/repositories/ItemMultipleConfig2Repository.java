package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import org.springframework.data.repository.Repository;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.custom.CustomConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

@CustomConfig
public interface ItemMultipleConfig2Repository extends Repository<ItemEntity,Integer> {
	
	ItemEntity save(ItemEntity entity);
	
}
