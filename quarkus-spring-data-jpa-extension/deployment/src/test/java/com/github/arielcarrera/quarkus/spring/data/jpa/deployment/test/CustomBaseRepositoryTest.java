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
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.custom.CustomBaseRepositoryConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.CustomBaseRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemCustomBaseRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemCustomBaseResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemCustomBaseResource.ItemCustomBaseClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemCustomBaseResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class CustomBaseRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(ItemCustomBaseRepository.class, ItemCustomBaseResource.class, ItemCustomBaseResourceImpl.class, ItemCustomBaseClient.class)
                    .addClasses(CustomBaseRepository.class, CustomBaseRepositoryConfig.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    ItemCustomBaseClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    
    @Test
    public void save_custom_ok() {
    	WebApplicationException exception = assertThrows(WebApplicationException.class, () -> client.save(null));
    	assertEquals(Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus());
    	assertEquals(CustomBaseRepository.MSG, exception.getMessage());
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
    
}
