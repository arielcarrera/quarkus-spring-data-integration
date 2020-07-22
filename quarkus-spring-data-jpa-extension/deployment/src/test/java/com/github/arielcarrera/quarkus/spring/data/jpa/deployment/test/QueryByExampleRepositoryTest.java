package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemQueryByExampleRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemQueryByExampleResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemQueryByExampleResource.ItemQueryByExampleClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemQueryByExampleResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class QueryByExampleRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(ItemQueryByExampleRepository.class, ItemQueryByExampleResource.class, ItemQueryByExampleResourceImpl.class, ItemQueryByExampleClient.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    ItemQueryByExampleClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
	@Test
	public void findAll_example_OK() {
		List<ItemEntity> l = client.findAllByExample(false, new ItemEntity(null, 105));
		assertNotNull(l);
		assertEquals(2, l.size());
		assertEquals(105, l.get(0).getValue());
		assertEquals(105, l.get(1).getValue());
	}

	@Test
	public void findAll_example_NotFound() {
		List<ItemEntity> l = client.findAllByExample(false, new ItemEntity(null, 0));
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void findAll_example_sort_OK() {
		//sort by id desc
		List<ItemEntity> l = client.findAllByExample(true, new ItemEntity(null, 105));
		assertNotNull(l);
		assertEquals(2, l.size());
		assertEquals(105, l.get(0).getValue());
		assertEquals(105, l.get(1).getValue());
		assertTrue(l.get(0).getId() >= l.get(1).getId());
	}

	@Test
	public void findAll_example_pageable_OK() {
		//sort by id desc
		Page<ItemEntity> p = client.findAllByExample(true, 0, 1, new ItemEntity(null, 105));
		assertNotNull(p);
		assertEquals(1, p.getNumberOfElements());
		assertEquals(0, p.getNumber());
		assertEquals(2L, p.getTotalElements());
		assertEquals(2, p.getTotalPages());
		assertEquals(10, p.getContent().get(0).getId());
	}

	@Test
	public void findOne_example_OK() {
		Optional<ItemEntity> op = client.findOneByExample(new ItemEntity(null, 101));
		assertNotNull(op);
		assertTrue(op.isPresent());
		assertEquals(1, op.get().getId());
	}

	@Test
	public void findOne_example_NotFound() {
		Optional<ItemEntity> op = client.findOneByExample(new ItemEntity(null, -1));
		assertNotNull(op);
		assertFalse(op.isPresent());
	}

	@Test
	public void findOne_example_ErrorMultipleResults() throws Throwable {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.findOneByExample(new ItemEntity(null, 105, null));
		});
		assertEquals(Status.CONFLICT.getStatusCode(), exception.getResponse().getStatus());
	}

	@Test
	public void count_example_OK() {
		assertEquals(2, client.countByExample(new ItemEntity(null, 105)));
	}
	
	@Test
	public void exists_example_OK() {
		client.existsByExample(new ItemEntity(null, 105));
	}
	
	@Test
	public void exists_example_notFound() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> client.existsByExample(new ItemEntity(null, 23123)));
		assertEquals(Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
	}
}
