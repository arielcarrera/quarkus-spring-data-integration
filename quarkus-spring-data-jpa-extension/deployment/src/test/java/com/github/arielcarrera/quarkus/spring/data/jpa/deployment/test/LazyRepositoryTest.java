package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.LazyEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemLazyRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemLazyResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemLazyResource.ItemLazyClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemLazyResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class LazyRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(ItemLazyRepository.class, ItemLazyResource.class, ItemLazyResourceImpl.class, ItemLazyClient.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    ItemLazyClient client;
    
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
    public void findByLazyValue_ok() {
    	
    	testUtil.putItem(new ItemEntity(11, 111, 1011, ItemEntity.STATUS_ACTIVE, new LazyEntity(1,1000)));
    	assertTrue(testUtil.existsItemById(11));
    	//find
    	List<ItemEntity> list =  client.findByLazyValue(1000);
    	assertNotNull(list);
    	assertEquals(1, list.size());
    	assertEquals(11, list.get(0).getId());
    	assertEquals(1, list.get(0).getLazy().getId());
    }
    
    @Test
    public void findByLazyValue_notFound() {
    	testUtil.putItem(new ItemEntity(12, 112, 1012, ItemEntity.STATUS_ACTIVE, new LazyEntity(2,2000)));
    	assertTrue(testUtil.existsItemById(12));
    	List<ItemEntity> list =  client.findByLazyValue(101);
    	assertNotNull(list);
    	assertTrue(list.isEmpty());
    }
    
    @Test
    public void findByLazy_Value_ok() {
    	testUtil.putItem(new ItemEntity(13, 113, 1013, ItemEntity.STATUS_ACTIVE, new LazyEntity(3,3000)));
    	assertTrue(testUtil.existsItemById(13));
    	//find
    	client.findByLazy_Value(105);
    	List<ItemEntity> list =  client.findByLazy_Value(3000);
    	assertNotNull(list);
    	assertEquals(1, list.size());
    	assertEquals(13, list.get(0).getId());
    	assertEquals(3, list.get(0).getLazy().getId());
    }
    
    @Test
    public void findByLazy_Value_notFound() {
    	testUtil.putItem(new ItemEntity(14, 114, 1014, ItemEntity.STATUS_ACTIVE, new LazyEntity(4,4000)));
    	List<ItemEntity> list =  client.findByLazy_Value(101);
    	assertNotNull(list);
    	assertTrue(list.isEmpty());
    }
}
