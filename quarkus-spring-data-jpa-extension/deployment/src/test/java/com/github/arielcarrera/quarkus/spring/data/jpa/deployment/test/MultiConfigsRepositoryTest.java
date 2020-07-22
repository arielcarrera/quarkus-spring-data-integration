package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.custom.CustomConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.custom.CustomRepositoryConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.CustomBaseRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemLazyRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemMultipleConfig1Repository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemMultipleConfig2Repository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemLazyResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemLazyResource.ItemLazyClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemLazyResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemMultipleConfigResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemMultipleConfigResource.ItemMultipleConfigClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemMultipleConfigResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class MultiConfigsRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(ItemLazyRepository.class, ItemLazyResource.class, ItemLazyResourceImpl.class, ItemLazyClient.class)
                    .addClasses(ItemMultipleConfig1Repository.class, ItemMultipleConfig2Repository.class,
                    		ItemMultipleConfigResource.class, ItemMultipleConfigResourceImpl.class, ItemMultipleConfigClient.class,
	                		CustomRepositoryConfig.class, CustomBaseRepository.class, CustomConfig.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    ItemMultipleConfigClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    
    @Test
    public void check_save_repo1_default() {
    	WebApplicationException exception = assertThrows(WebApplicationException.class, () -> client.saveRepoConfig1(null));
    	assertEquals(Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus());
    	assertEquals("Target object must not be null", exception.getMessage());
    }
    
    @Test
    public void check_save_repo2_custom() {
    	WebApplicationException exception = assertThrows(WebApplicationException.class, () -> client.saveRepoConfig2(null));
    	assertEquals(Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus());
    	assertEquals(CustomBaseRepository.MSG, exception.getMessage());
    }
    
    @Test
    public void save_repo1_ok() {
    	ItemEntity item =  client.saveRepoConfig1(new ItemEntity(11, 111));
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
    public void save_repo2_ok() {
    	ItemEntity item =  client.saveRepoConfig2(new ItemEntity(12, 112));
    	assertNotNull(item);
    	assertEquals(12, item.getId());
    	assertEquals(112, item.getValue());
    	//check
    	item = testUtil.getItemById(12);
    	assertNotNull(item);
    	assertEquals(12, item.getId());
    	assertEquals(112, item.getValue());
    }
    
    
}
