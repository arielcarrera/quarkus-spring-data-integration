package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemReadRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments.ReadFragment;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemReadResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemReadResource.ItemReadClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemReadResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractReadResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractReadResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class ReadRepositoryTest extends AbstractItemReadRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addPackage(ReadFragment.class.getPackage())
                    .addClasses(AbstractReadResource.class, AbstractReadResourceImpl.class)
                    .addClasses(ReadRepository.class, ItemReadRepository.class,
                    		ItemReadResource.class, ItemReadResourceImpl.class, ItemReadClient.class)
    		).withConfigurationResource("application.properties");

    @RestClient
    ItemReadClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    @Override
    protected ItemReadClient getClient() {
    	return client;
    }
    
    @Override
    protected TestJdbcUtil getTestUtil() {
    	return testUtil;
    }
    
}
