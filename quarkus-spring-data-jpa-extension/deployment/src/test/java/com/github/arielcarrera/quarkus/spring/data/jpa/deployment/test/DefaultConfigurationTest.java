package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemSimpleRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemSimpleResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemSimpleResource.ItemSimpleClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemSimpleResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DataJpaConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DataJpaRepositoryConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.DataExceptionPostProcessor;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.TransactionalPostProcessor;

import io.quarkus.test.QuarkusUnitTest;

public class DefaultConfigurationTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(DataJpaConfig.class, DataJpaRepositoryConfig.class, DataExceptionPostProcessor.class)
                    .addClasses(ItemSimpleRepository.class, ItemSimpleResource.class, ItemSimpleResourceImpl.class, ItemSimpleClient.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    ItemSimpleClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    @Inject
    DataJpaConfig dataConfig;
    
    
    @Test
    public void findAll_ok() {
    	long count = testUtil.countAllItems();
    	List<ItemEntity> findAll = client.findAll();
    	assertNotNull(findAll);
    	assertEquals(count, findAll.size());
    }
    
    @Test
    @DisplayName("query implementation postfix")
    public void queryPostfix() {
    	assertEquals("Impl", dataConfig.repository.implementationPostfix);
    }
    
    @Test
    @DisplayName("query lookup strategy")
    public void queryLookup() {
    	assertTrue(dataConfig.repository.queryLookupStrategy.isPresent());
    	assertEquals(Key.CREATE_IF_NOT_FOUND, dataConfig.repository.queryLookupStrategy.get());
    }
    
    @Test
    @DisplayName("repository base class")
    public void repositoryBaseClass() {
    	assertTrue(dataConfig.repository.baseClass.isEmpty());
    }
    
    @Test
    @DisplayName("query method evaluation context provider")
    public void queryMethodEvaluationContextProvider() {
    	assertTrue(dataConfig.repository.queryMethodEvaluationContextProvider.isEmpty());
    }
    
    @Test
    @DisplayName("repository post processors")
    public void repositoryPostProcessors() {
    	assertTrue(dataConfig.repository.postProcessors.isPresent());
    	List<Class<?>> list = dataConfig.repository.postProcessors.get();
    	assertTrue(!list.isEmpty());
    	assertEquals(DataExceptionPostProcessor.class, list.get(0));
    	assertEquals(TransactionalPostProcessor.class, list.get(1));
    }

    @Test
    @DisplayName("query creation listeners")
    public void creationListeners() {
    	assertTrue(dataConfig.repository.queryCreationListeners.isEmpty());
    }


    @Test
    @DisplayName("named queries")
    public void namedQueries() {
    	assertTrue(dataConfig.namedQueries.isEmpty());
    }
}
