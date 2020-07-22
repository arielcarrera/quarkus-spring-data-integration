package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

public interface ItemSpecificationRepository extends Repository<ItemEntity,Integer>, JpaSpecificationExecutor<ItemEntity> {

	
}
