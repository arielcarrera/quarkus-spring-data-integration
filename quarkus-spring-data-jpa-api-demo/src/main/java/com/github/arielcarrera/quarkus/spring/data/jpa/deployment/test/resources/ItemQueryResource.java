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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;

@Path("/annotation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemQueryResource {

	@Path("/annotation")
	public interface ItemQueryClient extends ItemQueryResource {
	}
	
	// methods with @Query
	@GET
	@Path("/query/unique")
	List<ItemEntity> queryUniqueValue(@QueryParam("uniqueValue") @NotNull Integer uniqueValue);

	@GET
	@Path("/query/unique/page")
	Page<ItemEntity> queryUniqueValue(@QueryParam("uniqueValue") @NotNull Integer uniqueValue, @QueryParam("from") @DefaultValue("0") @Min(0) int from,
			@QueryParam("size") @DefaultValue("5") @Min(1) int size);

//	@GET
//	@Path("/named-query/unique")
//	List<ItemEntity> namedQueryUniqueValue(@QueryParam("uniqueValue") @NotNull Integer uniqueValue);
//
//	@GET
//	@Path("/named-query/unique/page")
//	Page<ItemEntity> namedQueryUniqueValue(@QueryParam("uniqueValue") @NotNull Integer uniqueValue, @QueryParam("from") @DefaultValue("0") @Min(0) int from,
//			@QueryParam("size") @DefaultValue("5") @Min(1) int size);

//	@GET
//	@Path("/native-query/unique")
//	List<ItemEntity> nativeQueryUniqueValue(@QueryParam("uniqueValue") @NotNull Integer uniqueValue);
//
//	@GET
//	@Path("/native-query/unique/page")
//	Page<ItemEntity> nativeQueryUniqueValue(@QueryParam("uniqueValue") @NotNull Integer uniqueValue, @QueryParam("from") @DefaultValue("0") @Min(0) int from,
//			@QueryParam("size") @DefaultValue("5") @Min(1) int size);

	@GET
	@Path("/query/unique/named-parameter")
	List<ItemEntity> queryUniqueValueNamedParameter(@QueryParam("uniqueValue") @NotNull Integer uniqueValue);

//	@GET
//	@Path("/query/unique/spel-expressions")
//	List<ItemEntity> queryUniqueValueSpelExpressions(@QueryParam("uniqueValue") @NotNull Integer uniqueValue);
	
	@POST
	@Path("/query/value")
	int updateValue(@QueryParam("value") @NotNull Integer value, @QueryParam("id") @NotNull Integer id);

	@DELETE
	@Path("/query/value")
	void deleteInBulkByValue(@QueryParam("value") @NotNull Integer value);
	
}
