package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

@Vetoed
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemMultipleConfigResource {

	@RegisterRestClient(configKey="item-api")
	@Path("/items")
	public interface ItemMultipleConfigClient extends ItemMultipleConfigResource {
	}
	
    @POST
    @Path("/config/1")
	ItemEntity saveRepoConfig1(@NotNull ItemEntity entity);
	
    @POST
    @Path("/config/2")
	ItemEntity saveRepoConfig2(@NotNull ItemEntity entity);
}
