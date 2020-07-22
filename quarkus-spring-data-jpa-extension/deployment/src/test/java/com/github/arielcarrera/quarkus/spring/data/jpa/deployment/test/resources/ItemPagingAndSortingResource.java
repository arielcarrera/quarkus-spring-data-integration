package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.List;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;

@Vetoed
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemPagingAndSortingResource extends ItemCrudResource {

	@RegisterRestClient(configKey="item-api")
	@Path("/items")
	public interface ItemPagingAndSortingClient extends ItemCrudClient, ItemPagingAndSortingResource {
	}
	
	@GET
	@Path("/page")
	Page<ItemEntity> findAllPageable(@QueryParam("sort") @DefaultValue("false") boolean sort, @QueryParam("from") @DefaultValue("0") @Min(0) int from, @QueryParam("size") @DefaultValue("5") @Min(1) int size);
	
	@GET
	@Path("/sort")
	List<ItemEntity> findAllSort(@QueryParam("desc") @DefaultValue("false") boolean desc);
	
}
