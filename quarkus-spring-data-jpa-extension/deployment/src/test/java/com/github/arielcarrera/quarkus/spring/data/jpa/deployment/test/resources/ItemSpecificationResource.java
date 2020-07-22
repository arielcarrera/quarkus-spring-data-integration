package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.List;
import java.util.Optional;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

@Vetoed
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemSpecificationResource {

	@RegisterRestClient(configKey="item-api")
	@Path("/items")
	public interface ItemSpecificationClient extends ItemSpecificationResource {
	}
	
	@GET
	@Path("/values/{value}")
	Optional<ItemEntity> findOneBySpec(@PathParam("value") @NotNull Integer value);

	@GET
	List<ItemEntity> findAllBySpecWithSort(@QueryParam("id") @NotNull Integer value, @QueryParam("asc") @DefaultValue("false") boolean sortAsc);

	@GET
	@Path("/values/{value}/count")
	long countBySpec(@PathParam("value") @NotNull Integer value);
	
	

}
