package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemRWDeleteResource.ItemRWDeleteClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractRWDeleteResource;

public abstract class AbstractItemRWDeleteRepositoryTest extends AbstractItemRWRepositoryTest {

	public AbstractItemRWDeleteRepositoryTest() {
		super();
	}

	@Override
	protected abstract ItemRWDeleteClient getClient();
	
	@Test
	public void deleteById_OK() {
		getTestUtil().putItem(new ItemEntity(24, 124));
		ItemEntity e = getTestUtil().getItemById(24);
		assertNotNull(e);
		assertEquals(24, e.getId());
		assertEquals(124, e.getValue());
		//delete
		getClient().deleteById(24);
		//check
		assertFalse(getTestUtil().existsItemById(24));
	}

	@Test
	public void deleteById_NotFound() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().deleteById(-1));
		assertEquals(Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(AbstractRWDeleteResource.ITEM_NOT_FOUND, exception.getMessage());
	}

	@Test
	public void delete_attached_entity_OK() {
		ItemEntity e = getTestUtil().putItem(new ItemEntity(25, 125));
		e = getTestUtil().getItemById(25);
		assertNotNull(e);
		assertEquals(25, e.getId());
		assertEquals(125, e.getValue());
		//delete
		getClient().delete(false, e);
		//check
		assertFalse(getTestUtil().existsItemById(25));
	}

	@Test
	public void delete_detached_entity_OK() {
		ItemEntity e = getTestUtil().putItem(new ItemEntity(26, 126));
		e = getTestUtil().getItemById(26);
		assertNotNull(e);
		assertEquals(26, e.getId());
		assertEquals(126, e.getValue());
		//delete
		getClient().delete(true, new ItemEntity(26, 126));
		//check
		assertFalse(getTestUtil().existsItemById(26));
	}

	/**
	 * Note: This test method does an insert + update + delete  operation
	 */
	@Test
	public void delete_detached_entity_updated_OK() {
		ItemEntity e = getTestUtil().putItem(new ItemEntity(27, 927));
		e = getTestUtil().getItemById(27);
		assertNotNull(e);
		assertEquals(27, e.getId());
		assertEquals(927, e.getValue());
		//delete
		getClient().delete(true, new ItemEntity(27, 127));
		//check
		assertFalse(getTestUtil().existsItemById(27));
	}

	/**
	 * Note: This test method does an select + insert + delete  operation
	 */
	@Test
	public void delete_detached_entity_NotFound() {
		assertFalse(getTestUtil().existsItemById(28)); //check that entity with id=28 does not exists
		getClient().delete(true, new ItemEntity(28, 128));
		assertFalse(getTestUtil().existsItemById(28));
	}

	@Test
	public void delete_entity_Null() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().delete(false, null));
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
	}

	@Test
	public void deleteAll_list_OK() {
		//save
		List<ItemEntity> entities = Arrays.asList(new ItemEntity(29, 129, 1029),new ItemEntity(30, 130, 1030));
		getTestUtil().putAllItems(entities);
		assertTrue(getTestUtil().existsItemById(29));
		assertTrue(getTestUtil().existsItemById(30));
		//delete
		getClient().deleteAll(entities);
		//check
		assertFalse(getTestUtil().existsItemById(29));
		assertFalse(getTestUtil().existsItemById(30));
	}

	@Test
	public void deleteAll_list_updated_OK() {
		//save
		List<ItemEntity> entities = Arrays.asList(new ItemEntity(31, 131, 1031), new ItemEntity(32, 932, 1032));
		getTestUtil().putAllItems(entities);
		assertTrue(getTestUtil().existsItemById(31));
		assertTrue(getTestUtil().existsItemById(32));
		//merge + delete
		getClient().deleteAll(Arrays.asList(new ItemEntity(31, 131, 1031), new ItemEntity(32, 132, 1032)));
		//check
		assertFalse(getTestUtil().existsItemById(31));
		assertFalse(getTestUtil().existsItemById(32));
	}

	@Test
	public void deleteAll_list_NotFound() {
		//save
		getTestUtil().putItem(new ItemEntity(33, 133, 1033));
		assertTrue(getTestUtil().existsItemById(33));
		assertFalse(getTestUtil().existsItemById(34));
		//delete
		List<ItemEntity> entities = Arrays.asList(new ItemEntity(33, 133, 1033), new ItemEntity(34, 134, 1034));
		getClient().deleteAll(entities);
		//check
		assertFalse(getTestUtil().existsItemById(33));
		assertFalse(getTestUtil().existsItemById(34));
	}

	@Test
	public void deleteAll_list_Null() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().deleteAll(null));
		assertEquals(Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus());
	}

	@Test
	public void deleteAll_list_Empty() {
		long count = getTestUtil().countAllItems();
		getClient().deleteAll(Collections.emptyList());
		assertEquals(count, getTestUtil().countAllItems());
	}

	@Test
	public void deleteAll_OK() {
		List<ItemEntity> all = getTestUtil().getAllItems();
		//delete
		getClient().deleteAll();
		//check
		assertEquals(0, getTestUtil().countAllItems());
		//restore data
		getTestUtil().putAllItems(all);
		assertEquals(all.size(), getTestUtil().countAllItems());
	}

	@Test
	public void deleteInBatch_OK() {
		//save
		List<ItemEntity> entities = Arrays.asList(new ItemEntity(35, 135, 1035), new ItemEntity(36, 136, 1036));
		getTestUtil().putAllItems(entities);
		assertTrue(getTestUtil().existsItemById(35));
		assertTrue(getTestUtil().existsItemById(36));
		//delete(batch)
		getClient().deleteInBatch(entities);
		//check
		assertFalse(getTestUtil().existsItemById(35));
		assertFalse(getTestUtil().existsItemById(36));
	}

	@Test
	public void deleteInBatch_NotFound() {
		//save
		getTestUtil().putItem(new ItemEntity(37, 137, 1037));
		assertTrue(getTestUtil().existsItemById(37));
		assertFalse(getTestUtil().existsItemById(38));
		//delete
		List<ItemEntity> entities = Arrays.asList(new ItemEntity(37, 137, 1037), new ItemEntity(38, 138, 1038));
		getClient().deleteInBatch(entities);
		//check(batch)
		assertFalse(getTestUtil().existsItemById(37));
		assertFalse(getTestUtil().existsItemById(38));
	}

	@Test
	public void deleteInBatch_Empty() {
		long count = getTestUtil().countAllItems();
		getClient().deleteInBatch(Collections.emptyList());
		assertEquals(count, getTestUtil().countAllItems());
	}

	@Test
	public void deleteAllInBatch_OK() {
		List<ItemEntity> all = getTestUtil().getAllItems();
		//delete
		getClient().deleteAllInBatch();
		//check
		assertEquals(0, getTestUtil().countAllItems());
		//restore data
		getTestUtil().putAllItems(all);
		assertEquals(all.size(), getTestUtil().countAllItems());
	}
}