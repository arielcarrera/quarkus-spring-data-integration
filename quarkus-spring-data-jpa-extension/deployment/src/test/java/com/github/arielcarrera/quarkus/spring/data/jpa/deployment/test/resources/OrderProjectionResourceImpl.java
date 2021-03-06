package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.OrderDto;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.OrderProjectionRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.OrderProjectionRepository.OrderProjection;

@Transactional
public class OrderProjectionResourceImpl implements OrderProjectionResource {

	@Inject
	OrderProjectionRepository repository;

	public OrderProjectionResourceImpl() {
		super();
	}
	
	@Override
	public OrderDto findByOrderNumber(Integer id) {
		 OrderProjection projection = repository.findByOrderNumber(id);
		 if (projection == null) return null;
		 return new OrderDto(projection.getId(), projection.getOrderNumber(), null, projection.getTotal());
	}
	
	@Override
	public OrderDto findDtoByOrderNumber(Integer id) {
		 return repository.findDtoByOrderNumber(id);
	}
}
