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
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.OrderProjectionRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.OrderProjectionResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.OrderProjectionResource.OrderProjectionClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.OrderProjectionResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class ProjectionRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(OrderEntity.class, ErrorResponse.class, OrderDto.class, OrderProjectionRepository.OrderProjection.class)
                    .addClasses(OrderProjectionRepository.class, OrderProjectionResource.class, OrderProjectionResourceImpl.class, OrderProjectionClient.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    OrderProjectionClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    @Test
    public void findByNumber_ok() {
    	OrderEntity order =  testUtil.putOrder(new OrderEntity(1,1,"A1","test",100D));
    	assertNotNull(order);
    	assertEquals(1, order.getId());
    	OrderDto dto =  client.findByNumber(1);
    	assertNotNull(dto);
    	assertEquals(1, dto.getId());
    	assertEquals(1, dto.getNumber());
    	assertEquals(100D, dto.getTotal());
    }
    
    @Test
    public void findById_notFound() {
    	OrderDto order =  client.findByNumber(100);
    	assertNull(order);
    }
    
    
    @Test
    public void findDtoByNumber_ok() {
    	OrderEntity order =  testUtil.putOrder(new OrderEntity(2,2,"A2","test",200D));
    	assertNotNull(order);
    	assertEquals(2, order.getId());
    	OrderDto dto =  client.findByNumber(2);
    	assertNotNull(dto);
    	assertEquals(2, dto.getId());
    	assertEquals(2, dto.getNumber());
    	assertEquals(200D, dto.getTotal());
    }
    
    @Test
    public void findDtoById_notFound() {
    	OrderDto order =  client.findByNumber(100);
    	assertNull(order);
    }
}
