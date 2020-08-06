package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ErrorResponse;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.OrderDto;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.OrderEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.OrderPersistableRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.OrderPersistableResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.OrderPersistableResource.OrderPersistableClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.OrderPersistableResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class PersistableRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(OrderEntity.class, ErrorResponse.class, OrderDto.class)
                    .addClasses(OrderPersistableRepository.class, OrderPersistableResource.class, OrderPersistableResourceImpl.class, OrderPersistableClient.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    OrderPersistableClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    
    @Test
    public void save_ok() {
    	OrderDto order =  client.save(new OrderDto(1,1,"A1"));
    	assertNotNull(order);
    	assertEquals(1, order.getId());
    	assertEquals(1, order.getOrderNumber());
    	assertEquals(200D, order.getTotal());
    }
    
    @Test
    public void findById_ok() {
    	OrderDto order =  client.save(new OrderDto(2,2,"A2"));
    	assertNotNull(order);
    	assertEquals(2, order.getId());
    	order =  client.findById(2);
    	assertNotNull(order);
    	assertEquals(2, order.getId());
    	assertEquals(2, order.getOrderNumber());
    	assertEquals(200D, order.getTotal());
    }
    
    @Test
    public void findById_notFound() {
    	OrderDto order =  client.findById(100);
    	assertNull(order);
    }
    
    
}
