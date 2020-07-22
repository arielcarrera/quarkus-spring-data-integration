package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;

@Path("/queries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemQueryByExampleResource {

	@Path("/queries")
	public interface ItemQueryByExampleClient extends ItemQueryByExampleResource {
	}
	
	@POST
	@Path("/example")
	List<ItemEntity> findAllByExample(@QueryParam("sort") @DefaultValue("false") boolean sort, @NotNull ItemEntity exampleData);

	@POST
    @Path("/example/page")
	Page<ItemEntity> findAllByExample(@QueryParam("sort") @DefaultValue("false") boolean sort, 
			@QueryParam("from") @DefaultValue("0") @Min(0) int from, 
			@QueryParam("size") @DefaultValue("5") @Min(1) int size,
			@NotNull ItemEntity exampleData);

	@POST
	@Path("/example/one")
	Optional<ItemEntity> findOneByExample(@NotNull ItemEntity exampleData);

	@POST
	@Path("/example/count")
	long countByExample(@NotNull ItemEntity exampleData);
	
	@POST
	@Path("/example/exists")
	void existsByExample(@NotNull ItemEntity exampleData);
	
}
