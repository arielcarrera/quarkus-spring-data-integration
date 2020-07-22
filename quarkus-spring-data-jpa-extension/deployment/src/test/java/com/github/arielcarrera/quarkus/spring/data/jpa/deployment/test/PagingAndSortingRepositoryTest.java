package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemCrudRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemPagingAndSortingRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemCrudResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemCrudResource.ItemCrudClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemCrudResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemPagingAndSortingResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemPagingAndSortingResource.ItemPagingAndSortingClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemPagingAndSortingResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class PagingAndSortingRepositoryTest extends AbstractItemCrudRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(ItemPagingAndSortingRepository.class, ItemPagingAndSortingResource.class, ItemPagingAndSortingResourceImpl.class, ItemPagingAndSortingClient.class,
                    		ItemCrudRepository.class, ItemCrudResource.class, ItemCrudResourceImpl.class, ItemCrudClient.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    ItemPagingAndSortingClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    @Override
    protected ItemPagingAndSortingClient getClient() {
    	return client;
    }
    
    @Override
    protected TestJdbcUtil getTestUtil() {
    	return testUtil;
    }
    
	@Test
	public void findAllSort_asc() {
		//sort by id asc
		List<ItemEntity> l = getClient().findAllSort(false);
		assertNotNull(l);
		assertTrue(l.size() >= 10);
		boolean sorted =  IntStream.range(0, l.size()-1)
		        .allMatch(i -> l.get(i).getId() <= l.get(i+1).getId());
		assertTrue(sorted);
	}

	@Test
	public void findAllSort_desc() {
		//sort by id desc
		List<ItemEntity> l = getClient().findAllSort(true);
		assertNotNull(l);
		assertTrue(l.size() >= 10);
		assertTrue(l.get(0).getId() >= 10);
		boolean sorted =  IntStream.range(0, l.size()-1)
		        .allMatch(i -> l.get(i).getId() >= l.get(i+1).getId());
		assertTrue(sorted);
	}

	@Test
	public void findAll_pageable_OK() {
		Page<ItemEntity> p = getClient().findAllPageable(false, 1, 5);
		assertNotNull(p);
		assertEquals(5, p.getNumberOfElements());
		assertEquals(1, p.getNumber());
		assertTrue(p.getTotalElements() >= 10L);
		assertTrue(p.getTotalPages() >= 2);
	}
	
	@Test
	public void findAll_pageable_NotFound() {
		Page<ItemEntity> p = getClient().findAllPageable(false, 100, 5);
		assertNotNull(p);
		assertEquals(0, p.getNumberOfElements());
		assertTrue(p.getContent().isEmpty());
	}
	
	@Test
	public void findAll_pageable_sort_OK() {
		//sort by id desc
		Page<ItemEntity> p = getClient().findAllPageable(true, 1, 5);
		assertNotNull(p);
		assertEquals(5, p.getNumberOfElements());
		assertEquals(1, p.getNumber());
		assertTrue(p.getTotalElements() >= 10L);
		assertTrue(p.getTotalPages() >= 2);
		List<ItemEntity> content = p.getContent();
		boolean sorted =  IntStream.range(0, content.size()-1)
			        .allMatch(i -> content.get(i).getId() >= content.get(i+1).getId());
		assertTrue(sorted);
	}
	
}
