package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ErrorResponse;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<WebApplicationException> {

	@Override
	public Response toResponse(WebApplicationException ex) {
		return Response.status(ex.getResponse().getStatus()).type(MediaType.APPLICATION_JSON)
				.entity(new ErrorResponse(ex.getMessage())).build();
	}
}