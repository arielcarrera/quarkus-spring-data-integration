package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemRWDeleteRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemRWRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemReadRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadWriteDeleteRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadWriteRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments.ReadFragment;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemRWDeleteResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemRWDeleteResource.ItemRWDeleteClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemRWDeleteResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemRWResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemRWResource.ItemRWClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemRWResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemReadResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemReadResource.ItemReadClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemReadResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractReadResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class RWDeleteRepositoryTest extends AbstractItemRWDeleteRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addPackage(ReadFragment.class.getPackage())
                    .addPackage(AbstractReadResource.class.getPackage())
                    .addClasses(ReadRepository.class, ReadWriteDeleteRepository.class, ReadWriteRepository.class)
                    .addClasses(ItemReadRepository.class,ItemReadResource.class, ItemReadResourceImpl.class, ItemReadClient.class)
                    .addClasses(ItemRWRepository.class, ItemRWResource.class, ItemRWResourceImpl.class, ItemRWClient.class)
                    .addClasses(ItemRWDeleteRepository.class, ItemRWDeleteResource.class, ItemRWDeleteResourceImpl.class, ItemRWDeleteClient.class)
    		).withConfigurationResource("application.properties");

    @RestClient
    ItemRWDeleteClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    @Override
    protected ItemRWDeleteClient getClient() {
    	return client;
    }
    
    @Override
    protected TestJdbcUtil getTestUtil() {
    	return testUtil;
    }
    
}
