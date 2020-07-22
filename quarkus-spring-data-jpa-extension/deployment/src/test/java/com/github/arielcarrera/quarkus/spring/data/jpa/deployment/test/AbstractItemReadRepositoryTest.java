package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemReadResource.ItemReadClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractReadResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;

public abstract class AbstractItemReadRepositoryTest{

	public AbstractItemReadRepositoryTest() {
		super();
	}

	protected abstract ItemReadClient getClient();
	
	protected abstract TestJdbcUtil getTestUtil();
	
	@Test
	public void findAll_OK() {
		List<ItemEntity> l = getClient().findAll(false);
		assertNotNull(l);
		assertTrue(l.size() >= 10);
	}

	@Test
	public void findAll_sort_OK() {
		//sort by id desc
		List<ItemEntity> l = getClient().findAll(true);
		assertNotNull(l);
		assertTrue(l.size() >= 10);
		assertTrue(l.get(0).getId() >= 10);
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
		Page<ItemEntity> p = getClient().findAllPageable(true, 100, 5);
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
	
	@Test
	public void findById_OK() {
		Optional<ItemEntity> op = getClient().findById(1, false);
		assertNotNull(op);
		assertTrue(op.isPresent() && op.get().getValue().equals(101));
	}

	@Test
	public void findById_NotFound() {
		Optional<ItemEntity> op = getClient().findById(-1, false);
		assertNotNull(op);
		assertFalse(op.isPresent());
	}

	@Test
	public void findOneById_OK() {
		Optional<ItemEntity> op = getClient().findById(1, true);
		assertNotNull(op);
		assertTrue(op.isPresent() && op.get().getValue().equals(101));
	}

	@Test
	public void findOneById_NotFound() {
		Optional<ItemEntity> op = getClient().findById(-1, true);
		assertNotNull(op);
		assertFalse(op.isPresent());
	}

	@Test
	public void existsById_OK() {
		getClient().existsById(1);
	}

	@Test
	public void existsById_NotFound() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().existsById(-1));
		assertEquals(Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(AbstractReadResourceImpl.DEFAULT_ERROR_MESSAGE, exception.getMessage());
	}
	
	@Test
	public void findAllByID_OK() {
		List<ItemEntity> l = getClient()
				.findAllById(Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)));
		assertNotNull(l);
		assertEquals(3, l.size());
	}

	@Test
	public void findAllByID_Partially() {
		List<ItemEntity> l = getClient()
				.findAllById(Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(-1)));
		assertNotNull(l);
		assertEquals(2, l.size());
	}

	@Test
	public void findAllByID_NotFound() {
		List<ItemEntity> l = getClient()
				.findAllById(Arrays.asList(Integer.valueOf(-1), Integer.valueOf(-2), Integer.valueOf(-3)));
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	
	public void entityManager_OK() {
		getClient().existsEntityManager();
	}

	@Test
	public void entityManager_contains_outsideTx_simpleJpaImpl() {
		assertFalse(getClient().contains(1, false, false));
	}
	
	@Test
	public void entityManager_contains_outsideTx_queryMethod() {
		assertFalse(getClient().contains(1, false, true));
	}

	@Test
	public void entityManager_contains_insideTx_simpleJpaImpl() {
		assertTrue(getClient().contains(1, true, false));
	}

	@Test
	public void entityManager_contains_insideGlobalTx_queryMethod() {
		assertTrue(getClient().contains(1, true, true));
	}
	
	@Test
	public void entityManager_contains_notFound() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			getClient().contains(-1, true, false);
		});
		assertEquals(Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(AbstractReadResourceImpl.ITEM_NOT_FOUND, exception.getMessage());
	}

	@Test
	public void count_OK() {
		assertTrue(getClient().count() >= 10);
	}

	@Test
	public void getOne_outTx() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			getClient().findCodeByReference(1, false, false);
		});
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(LazyInitializationException.class.getName(), exception.getMessage());
	}

	@Test
	public void getOne_inTx() {
		Integer code = getClient().findCodeByReference(1, true, false);
		assertNotNull(code);
		assertEquals(101, code);
	}

	@Test
	public void getOne_initOutTx() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			getClient().findCodeByReference(1, false, true);
		});
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
	}

	@Test
	public void getOne_NotFound() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
				getClient().findCodeByReference(-1, true, false);
		});
		assertEquals(Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(AbstractReadResourceImpl.ITEM_NOT_FOUND, exception.getMessage());
	}

	@Test
	public void findAll_example_OK() {
		List<ItemEntity> l = getClient().findAllByExample(false, new ItemEntity(null, 105));
		assertNotNull(l);
		assertEquals(2, l.size());
		assertEquals(105, l.get(0).getValue());
		assertEquals(105, l.get(1).getValue());
	}

	@Test
	public void findAll_example_NotFound() {
		List<ItemEntity> l = getClient().findAllByExample(false, new ItemEntity(null, 0));
		assertNotNull(l);
		assertTrue(l.isEmpty());
	}

	@Test
	public void findAll_example_sort_OK() {
		//sort by id desc
		List<ItemEntity> l = getClient().findAllByExample(true, new ItemEntity(null, 105));
		assertNotNull(l);
		assertEquals(2, l.size());
		assertEquals(105, l.get(0).getValue());
		assertEquals(105, l.get(1).getValue());
		assertTrue(l.get(0).getId() >= l.get(1).getId());
	}

	@Test
	public void findAll_example_pageable_OK() {
		//sort by id desc
		Page<ItemEntity> p = getClient().findAllByExample(true, 0, 1, new ItemEntity(null, 105));
		assertNotNull(p);
		assertEquals(1, p.getNumberOfElements());
		assertEquals(0, p.getNumber());
		assertEquals(2L, p.getTotalElements());
		assertEquals(2, p.getTotalPages());
		assertEquals(10, p.getContent().get(0).getId());
	}

	@Test
	public void findOne_example_OK() {
		Optional<ItemEntity> op = getClient().findOneByExample(new ItemEntity(null, 101));
		assertNotNull(op);
		assertTrue(op.isPresent());
		assertEquals(1, op.get().getId());
	}

	@Test
	public void findOne_example_NotFound() {
		Optional<ItemEntity> op = getClient().findOneByExample(new ItemEntity(null, -1));
		assertNotNull(op);
		assertFalse(op.isPresent());
	}

	@Test
	public void findOne_example_ErrorMultipleResults() throws Throwable {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			getClient().findOneByExample(new ItemEntity(null, 105, null));
		});
		assertEquals(Status.CONFLICT.getStatusCode(), exception.getResponse().getStatus());
	}

	@Test
	public void count_example_OK() {
		assertEquals(2, getClient().countByExample(new ItemEntity(null, 105)));
	}

	@Test
	public void exists_example_OK() {
		getClient().existsByExample(new ItemEntity(1, 101));
	}
	
	@Test
	public void exists_example_notFound() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().existsByExample(new ItemEntity(null, 23123)));
		assertEquals(Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
	}
	
}