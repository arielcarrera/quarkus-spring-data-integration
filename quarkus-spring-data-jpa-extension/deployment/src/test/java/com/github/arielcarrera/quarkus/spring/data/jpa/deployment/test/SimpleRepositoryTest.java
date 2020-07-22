package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ChildEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemSimpleRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemSimpleResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemSimpleResource.ItemSimpleClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemSimpleResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class SimpleRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(ItemSimpleRepository.class, ItemSimpleResource.class, ItemSimpleResourceImpl.class, ItemSimpleClient.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    ItemSimpleClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    @Test
    public void findAll_ok() {
    	long count = testUtil.countAllItems();
    	List<ItemEntity> findAll = client.findAll();
    	assertNotNull(findAll);
    	assertEquals(count, findAll.size());
    }
    
    @Test
    public void save_ok() {
    	ItemEntity item =  client.save(new ItemEntity(11, 111));
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
    public void findByValue_ok() {
    	List<ItemEntity> list =  client.findByValue(105);
    	assertNotNull(list);
    	assertEquals(2, list.size());
    }
    
    @Test
    public void findByValue_empty() {
    	List<ItemEntity> list =  client.findByValue(-100);
    	assertNotNull(list);
    	assertEquals(0, list.size());
    }
    
    @Test
    public void countByValue_ok() {
    	assertEquals(2, client.countByValue(105));
    }
    
    @Test
    public void deleteByValue_ok() {
    	ItemEntity item =  testUtil.putItem(new ItemEntity(12, 912));
    	ItemEntity item2 =  testUtil.putItem(new ItemEntity(13, 912));
    	assertNotNull(item);
    	assertNotNull(item2);
    	assertEquals(12, item.getId());
    	assertEquals(13, item2.getId());
    	long deleteCount = client.deleteByValue(912);
    	assertEquals(2, deleteCount);
    	assertFalse(testUtil.existsItemById(12));
    	assertFalse(testUtil.existsItemById(13));
    }

    @Test
    public void findByChildValue_ok() {
    	
    	testUtil.putItem(new ItemEntity(14, 114, 1014, ItemEntity.STATUS_ACTIVE, new ChildEntity(1,1000)));
    	assertTrue(testUtil.existsItemById(14));
    	//find
    	List<ItemEntity> list =  client.findByChildValue(1000);
    	assertNotNull(list);
    	assertEquals(1, list.size());
    	assertEquals(14, list.get(0).getId());
    	assertEquals(1, list.get(0).getChild().getId());
    }
    
    @Test
    public void findByChildValue_notFound() {
    	testUtil.putItem(new ItemEntity(15, 115, 1015, ItemEntity.STATUS_ACTIVE, new ChildEntity(2,2000)));
    	assertTrue(testUtil.existsItemById(15));
    	List<ItemEntity> list =  client.findByChildValue(101);
    	assertNotNull(list);
    	assertTrue(list.isEmpty());
    }
    
    @Test
    public void findByChild_Value_ok() {
    	testUtil.putItem(new ItemEntity(16, 116, 1016, ItemEntity.STATUS_ACTIVE, new ChildEntity(3,3000)));
    	assertTrue(testUtil.existsItemById(16));
    	//find
    	client.findByChild_Value(105);
    	List<ItemEntity> list =  client.findByChild_Value(3000);
    	assertNotNull(list);
    	assertEquals(1, list.size());
    	assertEquals(16, list.get(0).getId());
    	assertEquals(3, list.get(0).getChild().getId());
    }
    
    @Test
    public void findByChild_Value_notFound() {
    	testUtil.putItem(new ItemEntity(17, 117, 1017, ItemEntity.STATUS_ACTIVE, new ChildEntity(4,4000)));
    	List<ItemEntity> list =  client.findByChild_Value(101);
    	assertNotNull(list);
    	assertTrue(list.isEmpty());
    }
    
    @Test
    public void findAll_page_ok() {
    	long count = testUtil.countAllItems();
    	Page<ItemEntity> page = client.findAll(2,2);
    	assertNotNull(page);
    	assertEquals(2, page.getNumberOfElements());
    	assertEquals(2, page.getNumber());
    	assertEquals(count, page.getTotalElements());
    	assertNotNull(page.getContent());
    	assertEquals(2, page.getContent().size());
    	assertTrue(page.getTotalPages() > 2);
    }
    
    @Test
    public void findByValue_slice_ok() {
    	Page<ItemEntity> slice = client.findByValue(105, 0, 1);
    	assertNotNull(slice);
    	assertEquals(1, slice.getNumberOfElements());
    	assertEquals(0, slice.getNumber());
    	assertNull(slice.getTotalElements());
    	assertNull(slice.getTotalPages());
    	assertNotNull(slice.getContent());
    	assertEquals(1, slice.getContent().size());
    }
    
    @Test
    public void findFirstByOrderByValueDesc_ok() {
    	testUtil.putItem(new ItemEntity(18, 10000000));
    	ItemEntity item = client.findFirstByOrderByValueDesc();
    	assertNotNull(item);
    	assertEquals(18, item.getId());
    	assertEquals(10000000, item.getValue());
    }
    
    @Test
    public void findTop3ByValueLessThan_ok() {
    	Page<ItemEntity> slice = client.findTop3ByValueLessThan(105, 0, 3);
    	assertNotNull(slice);
    	assertEquals(3, slice.getNumberOfElements());
    	assertEquals(0, slice.getNumber());
    	assertNull(slice.getTotalElements());
    	assertNull(slice.getTotalPages());
    	assertNotNull(slice.getContent());
    	assertEquals(3, slice.getContent().size());
    	assertTrue(slice.getContent().stream().allMatch(i -> i.getValue() < 105));
    }
    
    @Test
    public void findStreamableByCodeIn_ok() {
    	List<ItemEntity> list = client.findStreamableByCodeIn(Arrays.asList(101,102,103));
    	assertNotNull(list);
    	assertEquals(3, list.size());
    	assertTrue(list.stream().allMatch(i -> i.getCode() > 100 && i.getCode() < 104));
    }
    
    @Test
    public void findAsyncByUniqueValue_ok() {
    	testSelectByUniqueValue(1005, client::findAsyncByUniqueValue);
    }
    
    private void testSelectByUniqueValue(Integer value, Function<Integer, List<ItemEntity>> query) {
    	List<ItemEntity> list = query.apply(value);
    	assertTrue(!list.isEmpty());
    	assertEquals(5, list.get(0).getId());
    }
}
