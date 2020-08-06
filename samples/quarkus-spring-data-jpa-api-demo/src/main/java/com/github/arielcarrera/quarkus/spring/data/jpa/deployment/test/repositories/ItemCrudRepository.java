package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

public interface ItemCrudRepository extends CrudRepository<ItemEntity,Integer> {

	//Query method implementations:
	List<ItemEntity> findByValue(Integer value);
	
}
