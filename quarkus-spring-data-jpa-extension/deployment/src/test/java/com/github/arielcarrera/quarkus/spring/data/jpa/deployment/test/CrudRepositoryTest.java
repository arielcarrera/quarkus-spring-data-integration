package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemCrudRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemCrudResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemCrudResource.ItemCrudClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemCrudResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractRWDeleteResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractReadResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class CrudRepositoryTest extends AbstractItemCrudRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(ItemCrudRepository.class, ItemCrudResource.class, ItemCrudResourceImpl.class, ItemCrudClient.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    ItemCrudClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    @Override
    protected ItemCrudClient getClient() {
    	return client;
    }
    
    @Override
    protected TestJdbcUtil getTestUtil() {
    	return testUtil;
    }
    
    @Test
    public void findAll_OK() {
    	long count = testUtil.countAllItems();
    	List<ItemEntity> findAll = getClient().findAll();
    	assertNotNull(findAll);
    	assertEquals(count, findAll.size());
    }
    
	@Test
	public void findById_OK() {
		Optional<ItemEntity> op = getClient().findById(1);
		assertNotNull(op);
		assertTrue(op.isPresent() && op.get().getValue().equals(101));
	}

	@Test
	public void findById_NotFound() {
		Optional<ItemEntity> op = getClient().findById(-1);
		assertNotNull(op);
		assertFalse(op.isPresent());
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
	public void count_OK() {
		assertTrue(getClient().count() >= 10);
	}
	
    @Test
    public void save_OK() {
    	ItemEntity item =  getClient().save(new ItemEntity(11, 111));
    	assertNotNull(item);
    	assertEquals(11, item.getId());
    	assertEquals(111, item.getValue());
    	//check
    	item = testUtil.getItemById(11);
    	assertNotNull(item);
    	assertEquals(11, item.getId());
    	assertEquals(111, item.getValue());
    }
    
    @Test
    public void save_NotUnique() {
    	WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().save(new ItemEntity(13, 113, 1002)));
    	assertEquals(Status.CONFLICT.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
	public void update_OK() {
		//save
		testUtil.putItem(new ItemEntity(12, 912));
		ItemEntity e = testUtil.getItemById(12);
		assertNotNull(e);
		assertEquals(12, e.getId());
		assertEquals(912, e.getValue());
		//update
		ItemEntity result = getClient().save(new ItemEntity(12, 112));
		assertNotNull(result);
		assertEquals(12, result.getId());
		e = testUtil.getItemById(12);
		assertNotNull(e);
		assertEquals(12, e.getId());
		assertEquals(112, e.getValue());
	}

	@Test
	public void update_NotUnique() {
		//save
		testUtil.putItem(new ItemEntity(14, 114));
		ItemEntity e = testUtil.getItemById(14);
		assertNotNull(e);
		assertEquals(14, e.getId());
		assertEquals(114, e.getValue());
		//update
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().save(new ItemEntity(14, 114, 1002)));
		assertEquals(Status.CONFLICT.getStatusCode(), exception.getResponse().getStatus());
	}

	@Test
	public void saveAll_OK() {
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
		e = testUtil.getItemById(15);
		assertNotNull(e);
		assertEquals(15, e.getId());
		assertEquals(115, e.getValue());
		assertEquals(1015, e.getUniqueValue());
		e = testUtil.getItemById(16);
		assertNotNull(e);
		assertEquals(16, e.getId());
		assertEquals(116, e.getValue());
		assertEquals(1016, e.getUniqueValue());
	}

	@Test
	public void saveAll_update_OK() {
		//save
		testUtil.putItem(new ItemEntity(17, 917, 1017));
		testUtil.putItem(new ItemEntity(18, 918, 1018));
		ItemEntity e = testUtil.getItemById(17);
		assertNotNull(e);
		assertEquals(17, e.getId());
		assertEquals(917, e.getValue());
		assertEquals(1017, e.getUniqueValue());
		e = testUtil.getItemById(18);
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
		e = testUtil.getItemById(17);
		assertNotNull(e);
		assertEquals(17, e.getId());
		assertEquals(117, e.getValue());
		assertEquals(1017, e.getUniqueValue());
		e = testUtil.getItemById(18);
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
		assertFalse(testUtil.existsItemById(19));
		assertFalse(testUtil.existsItemById(20));
		assertFalse(testUtil.existsItemById(21));
	}
	
	
	@Test
	public void deleteById_OK() {
		testUtil.putItem(new ItemEntity(24, 124));
		ItemEntity e = testUtil.getItemById(24);
		assertNotNull(e);
		assertEquals(24, e.getId());
		assertEquals(124, e.getValue());
		//delete
		getClient().deleteById(24);
		//check
		assertFalse(testUtil.existsItemById(24));
	}

	@Test
	public void deleteById_NotFound() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> getClient().deleteById(-1));
		assertEquals(Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(AbstractRWDeleteResource.ITEM_NOT_FOUND, exception.getMessage());
	}

	@Test
	public void delete_OK() {
		ItemEntity e = testUtil.putItem(new ItemEntity(26, 126));
		e = testUtil.getItemById(26);
		assertNotNull(e);
		assertEquals(26, e.getId());
		assertEquals(126, e.getValue());
		//delete
		getClient().delete(new ItemEntity(26, 126));
		//check
		assertFalse(testUtil.existsItemById(26));
	}

	/**
	 * Note: This test method does an insert + update + delete  operation
	 */
	@Test
	public void delete_updated_OK() {
		ItemEntity e = testUtil.putItem(new ItemEntity(27, 927));
		e = testUtil.getItemById(27);
		assertNotNull(e);
		assertEquals(27, e.getId());
		assertEquals(927, e.getValue());
		//delete
		getClient().delete(new ItemEntity(27, 127));
		//check
		assertFalse(testUtil.existsItemById(27));
	}

	/**
	 * Note: This test method does an select + insert + delete  operation
	 */
	@Test
	public void delete_NotFound() {
		assertFalse(testUtil.existsItemById(28)); //check that entity with id=28 does not exists
		getClient().delete(new ItemEntity(28, 128));
		assertFalse(testUtil.existsItemById(28));
	}

	@Test
	public void deleteAll_list_OK() {
		//save
		List<ItemEntity> entities = Arrays.asList(new ItemEntity(29, 129, 1029),new ItemEntity(30, 130, 1030));
		testUtil.putAllItems(entities);
		assertTrue(testUtil.existsItemById(29));
		assertTrue(testUtil.existsItemById(30));
		//delete
		getClient().deleteAll(entities);
		//check
		assertFalse(testUtil.existsItemById(29));
		assertFalse(testUtil.existsItemById(30));
	}

	@Test
	public void deleteAll_list_update_OK() {
		//save
		List<ItemEntity> entities = Arrays.asList(new ItemEntity(31, 131, 1031), new ItemEntity(32, 932, 1032));
		testUtil.putAllItems(entities);
		assertTrue(testUtil.existsItemById(31));
		assertTrue(testUtil.existsItemById(32));
		//merge + delete
		getClient().deleteAll(Arrays.asList(new ItemEntity(31, 131, 1031), new ItemEntity(32, 132, 1032)));
		//check
		assertFalse(testUtil.existsItemById(31));
		assertFalse(testUtil.existsItemById(32));
	}

	@Test
	public void deleteAll_list_NotFound() {
		//save
		testUtil.putItem(new ItemEntity(33, 133, 1033));
		assertTrue(testUtil.existsItemById(33));
		assertFalse(testUtil.existsItemById(34));
		//delete
		List<ItemEntity> entities = Arrays.asList(new ItemEntity(33, 133, 1033), new ItemEntity(34, 134, 1034));
		getClient().deleteAll(entities);
		//check
		assertFalse(testUtil.existsItemById(33));
		assertFalse(testUtil.existsItemById(34));
	}

	@Test
	public void deleteAll_list_Empty() {
		long count = testUtil.countAllItems();
		getClient().deleteAll(Collections.emptyList());
		assertEquals(count, testUtil.countAllItems());
	}

	@Test
	public void deleteAll_OK() {
		List<ItemEntity> all = testUtil.getAllItems();
		//delete
		getClient().deleteAll();
		//check
		assertEquals(0, testUtil.countAllItems());
		//restore data
		testUtil.putAllItems(all);
		assertEquals(all.size(), testUtil.countAllItems());
	}

	
    @Test
    public void findByValue_OK() {
    	List<ItemEntity> list =  getClient().findByValue(105);
    	assertNotNull(list);
    	assertEquals(2, list.size());
    }
    
    @Test
    public void findByValue_empty() {
    	List<ItemEntity> list =  getClient().findByValue(-100);
    	assertNotNull(list);
    	assertEquals(0, list.size());
    }
    
}
