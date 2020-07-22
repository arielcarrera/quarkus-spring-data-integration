package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import org.springframework.data.repository.Repository;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.OrderEntity;

public interface OrderPersistableRepository extends Repository<OrderEntity,Integer> {

	OrderEntity findById(Integer id);
	
	OrderEntity save(OrderEntity entity);
    
}
