package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Codifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Identifiable;

public interface AbstractRWResource<T extends Identifiable<ID> & Codifiable<CODE>, ID, CODE>
		extends AbstractReadResource<T, ID, CODE> {

	String DEFAULT_ERROR_MESSAGE = "default error message";
	String ITEM_NOT_FOUND = "Item not found";

	@POST
	T save(@QueryParam("flush") @DefaultValue("false") boolean flush, @NotNull T entity);

	@PUT
	@Path("/{id}")
	T update(@PathParam("id") @NotNull ID id, @NotNull T entity);

	@POST
	@Path("/all")
	Iterable<T> saveAll(@NotEmpty List<T> entities);

	@POST
	@Path("/flush")
	T flush(@NotNull T entity);

}