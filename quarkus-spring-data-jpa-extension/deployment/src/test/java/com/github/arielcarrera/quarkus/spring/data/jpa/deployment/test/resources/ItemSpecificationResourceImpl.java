package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemSpecificationRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.specs.ItemSpecifications;

@Transactional
public class ItemSpecificationResourceImpl implements ItemSpecificationResource {

	@Inject
	ItemSpecificationRepository repository;

	public ItemSpecificationResourceImpl() {
		super();
	}
	
	@Override
    public Optional<ItemEntity> findOneBySpec(Integer value) {
		try {
			return repository.findOne(ItemSpecifications.isValueLessThan(value));			
		} catch (Exception e) {
			throw new WebApplicationException(Status.CONFLICT);
		}
    }
	
	@Override
    public List<ItemEntity> findAllBySpecWithSort(Integer value, boolean sortAsc) {
		return repository.findAll(ItemSpecifications.isValueLessThan(value), Sort.by(sortAsc ? Direction.ASC : Direction.DESC, "id"));
    }

	@Override
    public long countBySpec(Integer value) {
    	return repository.count(ItemSpecifications.isValueLessThan(value));
    }

}
