package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.OrderEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.OrderDto;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.OrderPersistableRepository;

@Transactional
public class OrderPersistableResourceImpl implements OrderPersistableResource {

	@Inject
	OrderPersistableRepository repository;

	public OrderPersistableResourceImpl() {
		super();
	}
	
	public OrderDto findById(Integer id) {
		 OrderEntity o = repository.findById(id);
		 if (o != null) return new OrderDto(o.getId(), o.getOrderNumber(), o.getProductId(), o.getTotal());
		 return null;
	}
	
	public OrderDto save(OrderDto entity) {
		OrderEntity o =  repository.save(new OrderEntity(entity.getId(), entity.getOrderNumber(), entity.getProductId(), "test", 200D));
		if (o != null) return new OrderDto(o.getId(), o.getOrderNumber(), o.getProductId(), o.getTotal());
		return null;
	}
	
}
