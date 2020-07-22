package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.enterprise.inject.Vetoed;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemReadResource.ItemReadClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractRWResource;


@Vetoed
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemRWResource extends AbstractRWResource<ItemEntity, Integer, Integer>{
	
	@RegisterRestClient(configKey="item-api")
	@Path("/items")
	public interface ItemRWClient extends ItemReadClient, ItemRWResource {
	}
}