package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

public interface ItemLazyRepository extends Repository<ItemEntity,Integer> {

	//Simple Jpa Repository implementations:
	List<ItemEntity> findAll();
	
    // query method - property expression
    List<ItemEntity> findByLazyValue(Integer value);
    
    List<ItemEntity> findByLazy_Value(Integer value);
}
