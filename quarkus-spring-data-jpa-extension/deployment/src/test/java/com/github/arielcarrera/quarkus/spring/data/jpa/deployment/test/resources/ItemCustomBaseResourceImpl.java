package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemCustomBaseRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.exceptions.DataAccessException;

@Transactional
public class ItemCustomBaseResourceImpl implements ItemCustomBaseResource {

	@Inject
	ItemCustomBaseRepository repository;

	public ItemCustomBaseResourceImpl() {
		super();
	}
	
	public ItemEntity save(ItemEntity entity) {
		try {
			return repository.save(entity);
		} catch (DataAccessException e) {
			if (e.getCause() instanceof IllegalArgumentException) {
				throw new WebApplicationException(e.getCause().getMessage(), Response.Status.BAD_REQUEST);
			}
			throw e;
		}
	}
	
    
}
