package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemSpecificationRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.specs.ItemSpecifications;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemSpecificationResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemSpecificationResource.ItemSpecificationClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemSpecificationResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class SpecificationRepositoryTest {

	@RegisterExtension
	static final QuarkusUnitTest config = new QuarkusUnitTest()
			.setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class).addAsResource("import.sql")
					.addPackage(ClientExceptionFilter.class.getPackage()).addPackage(ItemEntity.class.getPackage())
					.addPackage(DefaultCdiRepositoryConfig.class.getPackage())
					.addPackage(TestJdbcUtil.class.getPackage()).addClasses(ItemSpecificationRepository.class,
							ItemSpecificationResource.class, ItemSpecificationResourceImpl.class,
							ItemSpecificationClient.class, ItemSpecifications.class))
			.withConfigurationResource("application.properties");

	@RestClient
	ItemSpecificationClient client;

	@Inject
	TestJdbcUtil testUtil;

	@Test
	public void findOneBySpec_ok() {
		Optional<ItemEntity> item = client.findOneBySpec(102);
		assertTrue(item.isPresent());
		assertEquals(101, item.get().getValue());
	}

	@Test
	public void findOneBySpec_multipleResults() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> client.findOneBySpec(103));
		assertEquals(Status.CONFLICT.getStatusCode(), exception.getResponse().getStatus());
	}

	@Test
	public void findAllBySpec_asc_ok() {
		List<ItemEntity> findAll = client.findAllBySpecWithSort(103, true);
		assertNotNull(findAll);
		assertEquals(2, findAll.size());
		assertEquals(101, findAll.get(0).getValue());
		assertEquals(102, findAll.get(1).getValue());
	}
	
	@Test
	public void findAllBySpec_desc_ok() {
		List<ItemEntity> findAll = client.findAllBySpecWithSort(103, false);
		assertNotNull(findAll);
		assertEquals(2, findAll.size());
		assertEquals(102, findAll.get(0).getValue());
		assertEquals(101, findAll.get(1).getValue());
	}
	
	@Test
	public void countBySpec_ok() {
		assertEquals(2, client.countBySpec(103));
	}

}
