package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemNoRepositoryBeanRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.NoRepositoryBeanRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.NoRepositoryBeanResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.NoRepositoryBeanResource.NoRepositoryBeanClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.NoRepositoryBeanResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class NoRepositoryBeanTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(NoRepositoryBeanRepository.class, ItemNoRepositoryBeanRepository.class, 
                    		NoRepositoryBeanResource.class, NoRepositoryBeanResourceImpl.class, NoRepositoryBeanClient.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    NoRepositoryBeanClient client;
    
    @Test
    public void checkNoRepositoryBeanInjection_false() {
    	assertFalse(client.checkNoRepositoryBeanInjection());
    }
    
    @Test
    public void checkNoRepositoryBeanUsageInjection_ok() {
    	assertTrue(client.checkItemNoRepositoryBeanInjection());
    }
    
}
