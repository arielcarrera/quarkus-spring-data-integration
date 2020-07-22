package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.List;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

@Vetoed
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemLazyResource {

	@RegisterRestClient(configKey="item-api")
	@Path("/items")
	public interface ItemLazyClient extends ItemLazyResource {
	}
	
    @GET
    List<ItemEntity> findAll();

    @GET
    @Path("/lazy/{value}")
    List<ItemEntity> findByLazyValue(@PathParam("value") @NotNull Integer value);
    
    @GET
    @Path("/lazy/{value}/alternative")
    List<ItemEntity> findByLazy_Value(@PathParam("value") @NotNull Integer value);

    
}
