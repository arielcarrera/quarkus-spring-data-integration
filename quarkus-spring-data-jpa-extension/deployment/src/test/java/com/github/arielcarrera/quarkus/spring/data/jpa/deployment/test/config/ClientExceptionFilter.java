package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ErrorResponse;

@Provider
public class ClientExceptionFilter implements ClientResponseFilter {

	@Inject
	ObjectMapper mapper;

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (Family.SUCCESSFUL.compareTo(responseContext.getStatusInfo().getFamily()) < 0) {
        	String msg = "default error message";
            if (responseContext.hasEntity()) {
            	try {
            		ErrorResponse error = mapper.readValue(responseContext.getEntityStream(), ErrorResponse.class);
                    msg = error.getMessage();
            	}catch (Exception ignored) {
				}
            }
            throw new WebApplicationException(msg, responseContext.getStatus());
        }
    }
}
