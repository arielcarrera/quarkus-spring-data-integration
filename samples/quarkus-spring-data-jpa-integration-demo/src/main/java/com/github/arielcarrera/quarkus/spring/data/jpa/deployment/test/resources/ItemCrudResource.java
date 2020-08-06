package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

@Path("/crud")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemCrudResource {

	@Path("/crud")
	public interface ItemCrudClient extends ItemCrudResource {
	}
	
	@GET
    List<ItemEntity> findAll();

	@GET
    @Path("/{id}")
	Optional<ItemEntity> findById(@PathParam("id") @NotNull Integer id); 

	@GET
	@Path("/filter")
	List<ItemEntity> findAllById(@QueryParam("id") List<Integer> ids);
	
	@HEAD
	@Path("/{id}")
	void existsById(@PathParam("id") @NotNull Integer id);
	
	@GET
	@Path("/count")
	long count();
	
    @POST
	ItemEntity save(@NotNull ItemEntity entity);
    
    @PUT
    @Path("/{id}")
    ItemEntity update(@PathParam("id") @NotNull Integer id, @NotNull ItemEntity entity);

    @POST
    @Path("/all")
    Iterable<ItemEntity> saveAll(@NotEmpty List<ItemEntity> entities);

    @DELETE
	@Path("/{id}")
	void deleteById(@PathParam("id") @NotNull Integer id);

	@DELETE
	void delete(ItemEntity entity);

	@DELETE
	@Path("/list")
	void deleteAll(List<ItemEntity> entities);

	@DELETE
	@Path("/all")
	void deleteAll();
	
	// Query method
    @GET
    @Path("/values/{value}")
	List<ItemEntity> findByValue(@PathParam("value") @NotNull Integer value);
	
}
