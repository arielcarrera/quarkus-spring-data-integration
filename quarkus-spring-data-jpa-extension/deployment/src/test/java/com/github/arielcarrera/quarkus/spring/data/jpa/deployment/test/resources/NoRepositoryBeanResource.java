package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.enterprise.inject.Vetoed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Vetoed
@Path("/tests/no-repository-bean")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface NoRepositoryBeanResource {

	@RegisterRestClient(configKey="item-api")
	@Path("/tests/no-repository-bean")
	public interface NoRepositoryBeanClient extends NoRepositoryBeanResource {
	}
	
    @GET
    boolean checkNoRepositoryBeanInjection();
    
    @GET
    @Path("/usage")
    boolean checkItemNoRepositoryBeanInjection();
    
}
