package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;


@Path("/simple")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemSimpleResource {

	
	@Path("/simple")
	public interface ItemSimpleClient extends ItemSimpleResource {
	}
	
	@GET
	List<ItemEntity> findAll();

	@POST
	ItemEntity save(@NotNull ItemEntity entity);

	// query method
	@GET
	@Path("/{value}")
	List<ItemEntity> findByValue(@PathParam("value") @NotNull Integer value);

	// derived count query
	@GET
	@Path("/count")
	long countByValue(@QueryParam("value") @NotNull Integer value);

	// derived delete query
	@DELETE
	long deleteByValue(@QueryParam("value") @NotNull Integer value);

	// query method - property expression
	@GET
	@Path("/child/{value}")
	List<ItemEntity> findByChildValue(@PathParam("value") @NotNull Integer childValue);

	@GET
	@Path("/child/{value}/alternative")
	List<ItemEntity> findByChild_Value(@PathParam("value") @NotNull Integer childValue);

	// page
	@GET
	@Path("/page")
	Page<ItemEntity> findAll(@QueryParam("from") @DefaultValue("0") @Min(0) int from,
			@QueryParam("size") @DefaultValue("5") @Min(1) int size);

	// slice
	@GET
	@Path("/slice")
	Page<ItemEntity> findByValue(@QueryParam("value") @NotNull Integer value,
			@QueryParam("from") @DefaultValue("0") @Min(0) int from,
			@QueryParam("size") @DefaultValue("5") @Min(1) int size);

	// first and order by
	@GET
	@Path("/first")
	ItemEntity findFirstByOrderByValueDesc();

	// top 3 by value less than
	@GET
	@Path("/slice/{value}/top3")
	Page<ItemEntity> findTop3ByValueLessThan(@PathParam("value") @NotNull Integer value,
			@QueryParam("from") @DefaultValue("0") @Min(0) int from,
			@QueryParam("size") @DefaultValue("5") @Min(1) int size);

	@GET
	@Path("/streamable/codes")
	List<ItemEntity> findStreamableByCodeIn(@QueryParam("code") @NotNull List<Integer> codes);

	// future - async
	@GET
	@Path("/async/unique")
	List<ItemEntity> findAsyncByUniqueValue(@QueryParam("uniqueValue") @NotNull Integer uniqueValue);

}
