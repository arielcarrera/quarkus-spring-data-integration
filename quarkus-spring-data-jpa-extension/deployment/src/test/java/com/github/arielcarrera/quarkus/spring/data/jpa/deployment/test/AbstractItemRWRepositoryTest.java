package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemRWResource.ItemRWClient;

public abstract class AbstractItemRWRepositoryTest extends AbstractItemReadRepositoryTest {

	public AbstractItemRWRepositoryTest() {
		super();
	}

	@Override
	protected abstract ItemRWClient getClient();
	
	@Test
	public void save_new_OK() {
		ItemEntity result = getClient().save(false, new ItemEntity(11, 111));
		assertNotNull(result);
		assertEquals(11, result.getId());
		//check
		ItemEntity e = getTestUtil().getItemById(11);
		assertNotNull(e);
		assertEquals(11, e.getId());
		assertEquals(111, e.getValue());
	}

	@Test
	public void save_update_OK() {
		//save
		getTestUtil().putItem(new ItemEntity(12, 912));
		ItemEntity e = getTestUtil().getItemById(12);
		assertNotNull(e);
		assertEquals(12, e.getId());
		assertEquals(912, e.getValue());
		//update
		ItemEntity result = getClient().save(false, new ItemEntity(12, 112));
		assertNotNull(result);
		assertEquals(12, result.getId());
		e = getTestUtil().getItemById(12);
		assertNotNull(e);
		assertEquals(12, e.getId());
		assertEquals(112, e.getValue());
	}

	@Test
	public void save_new_NotUnique() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().save(false, new ItemEntity(13, 113, 1002)));
		assertEquals(Status.CONFLICT.getStatusCode(), exception.getResponse().getStatus());
	}

	@Test
	public void save_update_NotUnique() {
		//save
		getTestUtil().putItem(new ItemEntity(14, 114));
		ItemEntity e = getTestUtil().getItemById(14);
		assertNotNull(e);
		assertEquals(14, e.getId());
		assertEquals(114, e.getValue());
		//update
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().save(false, new ItemEntity(14, 114, 1002)));
		assertEquals(Status.CONFLICT.getStatusCode(), exception.getResponse().getStatus());
	}

	@Test
	public void saveAll_new_OK() {
		List<ItemEntity> entities = new ArrayList<>();
		entities.add(new ItemEntity(15, 115, 1015));
		entities.add(new ItemEntity(16, 116, 1016));
		Iterable<ItemEntity> result = getClient().saveAll(entities);
		assertNotNull(result);
		Iterator<ItemEntity> iterator = result.iterator();
		ItemEntity e = iterator.next();
		assertEquals(15, e.getId());
		assertEquals(115, e.getValue());
		assertEquals(1015, e.getUniqueValue());
		e = iterator.next();
		assertEquals(16, e.getId());
		assertEquals(116, e.getValue());
		assertEquals(1016, e.getUniqueValue());
		//check
		e = getTestUtil().getItemById(15);
		assertNotNull(e);
		assertEquals(15, e.getId());
		assertEquals(115, e.getValue());
		assertEquals(1015, e.getUniqueValue());
		e = getTestUtil().getItemById(16);
		assertNotNull(e);
		assertEquals(16, e.getId());
		assertEquals(116, e.getValue());
		assertEquals(1016, e.getUniqueValue());
	}

	@Test
	public void saveAll_update_OK() {
		//save
		getTestUtil().putItem(new ItemEntity(17, 917, 1017));
		getTestUtil().putItem(new ItemEntity(18, 918, 1018));
		ItemEntity e = getTestUtil().getItemById(17);
		assertNotNull(e);
		assertEquals(17, e.getId());
		assertEquals(917, e.getValue());
		assertEquals(1017, e.getUniqueValue());
		e = getTestUtil().getItemById(18);
		assertNotNull(e);
		assertEquals(18, e.getId());
		assertEquals(918, e.getValue());
		assertEquals(1018, e.getUniqueValue());
		//update
		List<ItemEntity> entities2 = new ArrayList<>();
		entities2.add(new ItemEntity(17, 117, 1017));
		entities2.add(new ItemEntity(18, 118, 1018));
		Iterable<ItemEntity> result = getClient().saveAll(entities2);
		assertNotNull(result);
		Iterator<ItemEntity> iterator = result.iterator();
		e = iterator.next();
		assertEquals(17, e.getId());
		assertEquals(117, e.getValue());
		assertEquals(1017, e.getUniqueValue());
		e = iterator.next();
		assertEquals(18, e.getId());
		assertEquals(118, e.getValue());
		assertEquals(1018, e.getUniqueValue());
		//check
		e = getTestUtil().getItemById(17);
		assertNotNull(e);
		assertEquals(17, e.getId());
		assertEquals(117, e.getValue());
		assertEquals(1017, e.getUniqueValue());
		e = getTestUtil().getItemById(18);
		assertNotNull(e);
		assertEquals(18, e.getId());
		assertEquals(118, e.getValue());
		assertEquals(1018, e.getUniqueValue());
	}

	@Test
	public void saveAll_NotUnique() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().saveAll(Arrays.asList(
				new ItemEntity(19, 119, 1002),
				new ItemEntity(20, 120, 1020),
				new ItemEntity(21, 121, 1021))));
		assertEquals(Status.CONFLICT.getStatusCode(), exception.getResponse().getStatus());
		//check
		assertFalse(getTestUtil().existsItemById(19));
		assertFalse(getTestUtil().existsItemById(20));
		assertFalse(getTestUtil().existsItemById(21));
	}

	@Test
	public void saveAndFlush_OK() {
		ItemEntity result = getClient().save(true, new ItemEntity(22, 122));
		assertNotNull(result);
		assertEquals(22, result.getId());
		//check
		ItemEntity e = getTestUtil().getItemById(22);
		assertNotNull(e);
		assertEquals(22, e.getId());
		assertEquals(122, e.getValue());
	}

	@Test
	public void flush_OK() throws Exception {
		ItemEntity result = getClient().flush(new ItemEntity(23, 123));
		assertNotNull(result);
		assertEquals(23, result.getId());
		//check
		ItemEntity e = getTestUtil().getItemById(23);
		assertNotNull(e);
		assertEquals(23, e.getId());
		assertEquals(123, e.getValue());
	}
	

}