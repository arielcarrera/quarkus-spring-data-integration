package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemQueryRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemQueryResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemQueryResource.ItemQueryClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemQueryResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

public class QueryRepositoryTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("import.sql")
                    .addPackage(ClientExceptionFilter.class.getPackage())
                    .addPackage(ItemEntity.class.getPackage())
                    .addPackage(DefaultCdiRepositoryConfig.class.getPackage())
                    .addPackage(TestJdbcUtil.class.getPackage())
                    .addClasses(ItemQueryRepository.class, ItemQueryResource.class, ItemQueryResourceImpl.class, ItemQueryClient.class)
    		).withConfigurationResource("application.properties");
    
    @RestClient
    ItemQueryClient client;
    
    @Inject
    TestJdbcUtil testUtil;
    
    @FunctionalInterface
    interface TriFunction<A,B,C,R> {
        
    	R apply(A a, B b, C c);
        
		default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
			Objects.requireNonNull(after);
			return (A a, B b, C c) -> after.apply(apply(a, b, c));
		}
    }
    
    //hql query
    @Test
    public void queryUniqueValue_ok() {
    	testSelectByUniqueValue(1005, client::queryUniqueValue);
    }
    
    @Test
    public void queryUniqueValue_page_ok() {
    	testSelectByUniqueValue(1005, 0, 10, client::queryUniqueValue);
    }
    
    //named query
    @Test
    public void namedQueryUniqueValue_ok() {
    	testSelectByUniqueValue(1005, client::namedQueryUniqueValue);
    }
    
    @Test
    public void namedQueryUniqueValue_page_ok() {
    	testSelectByUniqueValue(1005, 0, 10, client::namedQueryUniqueValue);
    }
    
    //native query
    @Test
    public void nativeQueryUniqueValue_ok() {
    	testSelectByUniqueValue(1005, client::nativeQueryUniqueValue);
    }
    
    @Test
    public void nativeQueryUniqueValue_page_ok() {
    	testSelectByUniqueValue(1005, 0, 10, client::nativeQueryUniqueValue);
    }
    
    //named parameter
    @Test
    public void queryUniqueValue_namedParameter_ok() {
    	testSelectByUniqueValue(1005, client::queryUniqueValueNamedParameter);
    }
    
    //spel expressions
    @Test
    public void queryUniqueValue_spelExpression_ok() {
    	testSelectByUniqueValue(1005, client::queryUniqueValueSpelExpressions);
    }
    
    
    @Test
    public void modifyingQuery_ok() {
    	testUtil.putItem(new ItemEntity(19, 119, 1019));
    	assertTrue(testUtil.existsItemById(19));
    	//update
    	int rows = client.updateValue(19000, 19);
    	assertEquals(1, rows);
    	//check
    	ItemEntity item = testUtil.getItemById(19);
    	assertEquals(19, item.getId());
    	assertEquals(19000, item.getValue());
    }
    
    @Test
    public void derivedDeleteQuery_ok() {
    	testUtil.putItem(new ItemEntity(20, 555, 1020));
    	testUtil.putItem(new ItemEntity(21, 555, 1021));
    	assertTrue(testUtil.existsItemById(20));
    	assertTrue(testUtil.existsItemById(21));
    	//delete
    	client.deleteInBulkByValue(555);
    	//check
    	assertFalse(testUtil.existsItemById(20));
    	assertFalse(testUtil.existsItemById(21));
    }

    private void testSelectByUniqueValue(Integer value, Function<Integer, List<ItemEntity>> query) {
    	List<ItemEntity> list = query.apply(value);
    	assertTrue(!list.isEmpty());
    	assertEquals(5, list.get(0).getId());
    }
    
    private void testSelectByUniqueValue(Integer value, int from, int size, TriFunction<Integer, Integer, Integer, Page<ItemEntity>> query) {
    	Page<ItemEntity> page = query.apply(value, from, size);
    	assertTrue(!page.getContent().isEmpty());
    	assertEquals(1, page.getNumberOfElements());
    	assertEquals(1, page.getTotalElements());
    	assertEquals(from, page.getNumber());
    	assertEquals(size, page.getSize());
    	assertEquals(5, page.getContent().get(0).getId());
    }
}
