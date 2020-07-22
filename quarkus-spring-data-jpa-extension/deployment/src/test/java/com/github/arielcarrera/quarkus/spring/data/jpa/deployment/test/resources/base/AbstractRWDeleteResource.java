package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Codifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Identifiable;

public interface AbstractRWDeleteResource<T extends Identifiable<ID> & Codifiable<CODE>, ID, CODE>
		extends AbstractRWResource<T, ID, CODE> {

	String DEFAULT_ERROR_MESSAGE = "default error message";
	String ITEM_NOT_FOUND = "Item not found";

	@DELETE
	@Path("/{id}")
	void deleteById(@PathParam("id") @NotNull ID id);

	@DELETE
	void delete(@QueryParam("detached") @DefaultValue("false") boolean flush, T entity);

	@DELETE
	@Path("/list")
	void deleteAll(List<T> entities);

	@DELETE
	@Path("/all")
	void deleteAll();

	@DELETE
	@Path("/batch")
	void deleteInBatch(@NotNull List<T> entities);

	@DELETE
	@Path("/all/batch")
	void deleteAllInBatch();

}