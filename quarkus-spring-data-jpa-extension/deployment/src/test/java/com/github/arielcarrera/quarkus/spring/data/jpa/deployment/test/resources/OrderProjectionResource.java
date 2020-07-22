package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.OrderDto;

@Vetoed
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OrderProjectionResource {

	@RegisterRestClient(configKey="order-api")
	@Path("/orders")
	public interface OrderProjectionClient extends OrderProjectionResource {
	}
	
    @GET
    @Path("/{id}")
    OrderDto findByNumber(@PathParam("id") @NotNull Integer id);

    @GET
    @Path("/{id}/dto")
	OrderDto findDtoByNumber(@PathParam("id") @NotNull Integer id);
    
}
