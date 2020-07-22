package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import org.springframework.data.repository.Repository;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.OrderDto;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.OrderEntity;

public interface OrderProjectionRepository extends Repository<OrderEntity,Integer> {

	interface OrderProjection {
		
		Integer getId();

		Integer getNumber();

		Double getTotal();

	}
	
	OrderProjection findByNumber(Integer id);
	
	OrderDto findDtoByNumber(Integer id);
	
}
