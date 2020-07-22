package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.custom.CustomConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemMultipleConfig1Repository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemMultipleConfig2Repository;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.exceptions.DataAccessException;

@Transactional
public class ItemMultipleConfigResourceImpl implements ItemMultipleConfigResource {

	@Inject
	ItemMultipleConfig1Repository repository1;
	
	@Inject @CustomConfig
	ItemMultipleConfig2Repository repository2;

	public ItemMultipleConfigResourceImpl() {
		super();
	}

	@Override
	public ItemEntity saveRepoConfig1(@NotNull ItemEntity entity) {
		try {
			return repository1.save(entity);
		} catch (DataAccessException e) {
			if (e.getCause() instanceof IllegalArgumentException) {
				throw new WebApplicationException(e.getCause().getMessage(), Response.Status.BAD_REQUEST);
			}
			throw e;
		}
	}

	@Override
	public ItemEntity saveRepoConfig2(@NotNull ItemEntity entity) {
		try {
			return repository2.save(entity);
		} catch (DataAccessException e) {
			if (e.getCause() instanceof IllegalArgumentException) {
				throw new WebApplicationException(e.getCause().getMessage(), Response.Status.BAD_REQUEST);
			}
			throw e;
		}
	}
	
    
}
