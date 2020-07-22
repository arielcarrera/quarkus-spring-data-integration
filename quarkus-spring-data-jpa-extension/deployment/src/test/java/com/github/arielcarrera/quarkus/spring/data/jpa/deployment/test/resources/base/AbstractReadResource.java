package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Codifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Identifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;

public interface AbstractReadResource<T extends Identifiable<ID> & Codifiable<CODE>, ID, CODE> {

	String DEFAULT_ERROR_MESSAGE = "default error message";
	String ITEM_NOT_FOUND = "Item not found";
	
	@GET
	List<T> findAll(@QueryParam("sort") @DefaultValue("false") boolean sort);

	@GET
	@Path("/page")
	Page<T> findAllPageable(@QueryParam("sort") @DefaultValue("false") boolean sort, @QueryParam("from") @DefaultValue("0") @Min(0) int from, @QueryParam("size") @DefaultValue("5") @Min(1) int size);
	
	@GET
    @Path("/{id}")
	Optional<T> findById(@PathParam("id") @NotNull ID id, @QueryParam("queryMethod") @DefaultValue("false") boolean queryMethod);

	@HEAD
    @Path("/{id}")
	void existsById(@PathParam("id") @NotNull ID id);
	
	@GET
	@Path("/filter")
	List<T> findAllById(@QueryParam("id") List<ID> ids);
	
	@HEAD
	@Path("/context")
	void existsEntityManager();

	@GET
	@Path("/context/{id}")
	boolean contains(@PathParam("id") @NotNull ID id, @QueryParam("globalTx") @DefaultValue("false") boolean globalTx, @QueryParam("queryMethod") @DefaultValue("false") boolean queryMethod);
	
	
	@GET
	@Path("/count")
	long count();

	@GET
	@Path("/{id}/code")
	CODE findCodeByReference(@PathParam("id") @NotNull ID id, @QueryParam("globalTx") @DefaultValue("false") boolean globalTx, @QueryParam("outTx") @DefaultValue("false") boolean outTx);

	@POST
	@Path("/example")
	List<T> findAllByExample(@QueryParam("sort") @DefaultValue("false") boolean sort, @NotNull T exampleData);

	@POST
    @Path("/example/page")
	Page<T> findAllByExample(@QueryParam("sort") @DefaultValue("false") boolean sort, 
			@QueryParam("from") @DefaultValue("0") @Min(0) int from, 
			@QueryParam("size") @DefaultValue("5") @Min(1) int size,
			@NotNull T exampleData);

	@POST
	@Path("/example/one")
	Optional<T> findOneByExample(@NotNull T exampleData);

	@POST
	@Path("/example/count")
	long countByExample(@NotNull T exampleData);

	@POST
	@Path("/example/exists")
	void existsByExample(@NotNull T exampleData);

}