package com.github.arielcarrera.quarkus.spring.data;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemCrudResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemQueryByExampleResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemQueryResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemSimpleResource;

@Component
@ApplicationPath("api")
public class JaxrsConfig extends ResourceConfig {
	
	/**
	 * JAX-RS configuration
	 */
	public JaxrsConfig() {
		register(ObjectMapperProvider.class);
		register(ItemCrudResource.class);
		register(ItemQueryByExampleResource.class);
		register(ItemQueryResource.class);
		register(ItemSimpleResource.class);
	}
}