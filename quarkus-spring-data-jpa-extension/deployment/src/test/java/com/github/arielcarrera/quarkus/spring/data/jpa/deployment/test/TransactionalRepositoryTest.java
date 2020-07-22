package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.ClientExceptionFilter;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemRWDeleteRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemRWRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemReadRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemTransactionalRepository;
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
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemTransactionalResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemTransactionalResource.ItemPair;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemTransactionalResource.ItemTransactionalClient;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.ItemTransactionalResourceImpl;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base.AbstractReadResource;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.TestJdbcUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.test.QuarkusUnitTest;

/**
 * Tests for Transactional Operations
 * 
 * @author Ariel Carrera
 *
 */
public class TransactionalRepositoryTest {

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
					.addClasses(ItemTransactionalRepository.class, ItemTransactionalResource.class, 
							ItemTransactionalResourceImpl.class, ItemTransactionalClient.class)
			).withConfigurationResource("application.properties");

	@Inject
	TestJdbcUtil jdbcUtil;

	@Inject
	@Any
	ItemTransactionalClient client;

	public TransactionalRepositoryTest() {
		super();
	}

	// TESTS - REPO WITH TX ANNOTATIONS IN INTERFACE: 
	@Test
	public void deleteTxDefault() {
		addTestItem(37);
		client.deleteTxDefault(37);
		assertDeleted(37);
	}
	
	@Test
	public void deleteTxDefaultWhitin() {
		addTestItem(38);
		client.deleteTxDefaultWhitin(38);
		assertDeleted(38);
	}
	
	@Test
	public void deleteTxMandatory() {
		addTestItem(39);
		assertThrows(WebApplicationException.class, () -> client.deleteTxMandatory(39));
		assertNotDeleted(39);
	}
	
	@Test
	public void deleteTxMandatoryWhitin() {
		addTestItem(40);
		client.deleteTxMandatoryWhitin(40);
		assertDeleted(40);
	}
	
	@Test
	public void deleteTxNever() {
		addTestItem(41);
		client.deleteTxNever(41);
		assertDeleted(41);
	}
	
	@Test
	public void deleteTxNeverWithin() {
		addTestItem(42);
		assertThrows(WebApplicationException.class, () -> client.deleteTxNeverWithin(42));
		assertNotDeleted(42);
	}
	
	@Test
	public void deleteTxNotSupported() {
		addTestItem(43);
		client.deleteTxNotSupported(43);
		assertDeleted(43);
	}
	
	@Test
	public void deleteTxNotSupportedWhitin() {
		addTestItem(44);
		client.deleteTxNotSupportedWhitin(44);
		assertDeleted(44);
	}
	
	@Test
	public void deleteTxRequired() {
		addTestItem(45);
		client.deleteTxRequired(45);
		assertDeleted(45);
	}
	
	@Test
	public void deleteTxRequiredWhitin() {
		addTestItem(46);
		client.deleteTxRequiredWhitin(46);
		assertDeleted(46);
	}
	
	@Test
	public void deleteTxRequiresNew() {
		addTestItem(47);
		client.deleteTxRequiresNew(47);
		assertDeleted(47);
	}
	
	@Test
	public void deleteTxRequiresNewWhitin() {
		addTestItem(48);
		client.deleteTxRequiresNewWhitin(48);
		assertDeleted(48);
	}
	
	@Test
	public void deleteTxSupports() {
		addTestItem(49);
		client.deleteTxSupports(49);
		assertDeleted(49);
	}
	
	@Test
	public void deleteTxSupportsWhitin() {
		addTestItem(50);
		client.deleteTxSupportsWhitin(50);
		assertDeleted(50);
	}
	
	@Test
	public void deleteNoAnnotated() {
		addTestItem(51);
		client.deleteNoAnnotated(51);
		assertDeleted(51);
	}
	
	@Test
	public void deleteNoAnnotatedWhitin() {
		addTestItem(52);
		client.deleteNoAnnotatedWhitin(52);
		assertDeleted(52);
	}
	
	// OTHER TESTS : SERVICE LAYER + DEFAULT REPO INTEGRATION (REPO WITH TX TYPE = REQUIRED)
	// TX TYPE = DEFAULT
	@Test
	public void txDefault_ok() {
		client.txDefault(new ItemEntity(11, 11, 11, ItemEntity.STATUS_ACTIVE));
		assertTrue(jdbcUtil.existsItemById(11));
	}

	@Test
	public void txDefault_rollback() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.txDefaultRollback(new ItemEntity(12, 12, 12, ItemEntity.STATUS_ACTIVE));
		});
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(ItemTransactionalResource.ROLLBACK, exception.getMessage());
		assertFalse(jdbcUtil.existsItemById(12));
	}

	@Test
	public void txMandatory_required() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () ->
			client.txMandatory(new ItemEntity(34, 34)));
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
	}

	// TX TYPE = NEVER
	@Test
	public void txNever_txWithin() {
		client.txNeverTxWithin(new ItemEntity(13, 13, 13, ItemEntity.STATUS_ACTIVE));
		assertTrue(jdbcUtil.existsItemById(13));
	}

	@Test
	public void txNever_noTx() throws SystemException {
		assertTrue(client.txNever());
	}

	@Test
	public void txNever_activeTx() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.txNeverActiveTx();
		});
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(ItemTransactionalResource.TX_EXISTS, exception.getMessage());
	}

	// TX TYPE = NOT SUPPORTED
	@Test
	public void txNotSupported_noTx() throws SystemException {
		assertTrue(client.txNotSupported());
	}

	@Test
	public void txNotSupported_activeTx() {
		assertTrue(client.txNotSupportedActiveTx());
	}

	@Test
	public void txNotSupported_txWithin() {
		client.txNotSupportedTxWithin(new ItemEntity(14, 14, 14, ItemEntity.STATUS_ACTIVE));
		assertTrue(jdbcUtil.existsItemById(14));
	}

	// TX TYPE = REQUIRED
	@Test
	public void txRequired_noTx() {
		client.txRequired(new ItemEntity(15, 15, 15, ItemEntity.STATUS_ACTIVE));
		assertTrue(jdbcUtil.existsItemById(15));
	}

	@Test
	public void txRequired_noTxRollback() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.txRequiredRollback(new ItemEntity(16, 16, 16, ItemEntity.STATUS_ACTIVE));
		});
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(ItemTransactionalResource.ROLLBACK, exception.getMessage());
		assertFalse(jdbcUtil.existsItemById(16));
	}

	@Test
	public void txRequired_txWithin() {
		client.txRequiredTxWithin(new ItemEntity(17, 17, 17, ItemEntity.STATUS_ACTIVE));
		assertTrue(jdbcUtil.existsItemById(17));
	}

	@Test
	public void txRequired_rollbackOut() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.txRequiredRollbackIn(new ItemPair(new ItemEntity(18, 18, 18, ItemEntity.STATUS_ACTIVE),
					new ItemEntity(19, 19, 19, ItemEntity.STATUS_ACTIVE)));
		});
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(ItemTransactionalResource.ROLLBACK, exception.getMessage());
		assertFalse(jdbcUtil.existsItemById(18));
		assertFalse(jdbcUtil.existsItemById(19));
	}

	@Test
	public void txRequired_rollbackIn() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.txRequiredRollbackOut(new ItemPair(new ItemEntity(20, 20, 20, ItemEntity.STATUS_ACTIVE),
					new ItemEntity(21, 21, 21, ItemEntity.STATUS_ACTIVE)));
		});
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(ItemTransactionalResource.ROLLBACK, exception.getMessage());
		assertTrue(jdbcUtil.existsItemById(20));
		assertFalse(jdbcUtil.existsItemById(21));
	}

	// TX TYPE = REQUIRES NEW
	@Test
	public void txRequiresNew_noTx() {
		client.txRequiresNew(new ItemEntity(22, 22, 22, ItemEntity.STATUS_ACTIVE));
		assertTrue(jdbcUtil.existsItemById(22));
	}

	@Test
	public void txRequiresNew_noTxRollback() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.txRequiredRollback(new ItemEntity(23, 23, 23, ItemEntity.STATUS_ACTIVE));
		});
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(ItemTransactionalResource.ROLLBACK, exception.getMessage());
		assertFalse(jdbcUtil.existsItemById(23));
	}

	@Test
	public void txRequiresNew_txWithinRollbackOut() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.txRequiresNewRollbackOut(new ItemPair(new ItemEntity(24, 24, 24, ItemEntity.STATUS_ACTIVE),
					new ItemEntity(25, 25, 25, ItemEntity.STATUS_ACTIVE)));
		});
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(ItemTransactionalResource.ROLLBACK, exception.getMessage());
		assertFalse(jdbcUtil.existsItemById(24));
		assertTrue(jdbcUtil.existsItemById(25));
	}

	@Test
	public void txRequiresNew_txWithinRollbackIn() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.txRequiresNewRollbackIn(new ItemPair(new ItemEntity(26, 26, 26, ItemEntity.STATUS_ACTIVE),
					new ItemEntity(27, 27, 27, ItemEntity.STATUS_ACTIVE)));
		});
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(ItemTransactionalResource.ROLLBACK, exception.getMessage());
		assertFalse(jdbcUtil.existsItemById(26));
		assertFalse(jdbcUtil.existsItemById(27));
	}

	@Test
	public void txRequiresNew_txWithinRollbackOut_Inverse() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.txRequiresNewRollbackOutInverse(new ItemPair(new ItemEntity(28, 28, 28, ItemEntity.STATUS_ACTIVE),
					new ItemEntity(29, 29, 29, ItemEntity.STATUS_ACTIVE)));
		});
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(ItemTransactionalResource.ROLLBACK, exception.getMessage());
		assertTrue(jdbcUtil.existsItemById(28));
		assertFalse(jdbcUtil.existsItemById(29));
	}

	@Test
	public void txRequiresNew_txWithinRollbackIn_Inverse() {
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
			client.txRequiresNewRollbackInInverse(new ItemPair(new ItemEntity(30, 30, 30, ItemEntity.STATUS_ACTIVE),
					new ItemEntity(31, 31, 31, ItemEntity.STATUS_ACTIVE)));
		});
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponse().getStatus());
		assertEquals(ItemTransactionalResource.ROLLBACK, exception.getMessage());
		assertFalse(jdbcUtil.existsItemById(30));
		assertFalse(jdbcUtil.existsItemById(31));
	}

	// TX TYPE = SUPPORTS
	@Test
	public void txSupports_noTx_Save() throws SystemException {
		client.txSupports(new ItemEntity(32, 32, 32, ItemEntity.STATUS_ACTIVE));
		assertTrue(jdbcUtil.existsItemById(32));
	}

	@Test
	public void txSupports_txWithin() {
		client.txSupportsActiveTx(new ItemEntity(33, 33, 33, ItemEntity.STATUS_ACTIVE));
		assertTrue(jdbcUtil.existsItemById(33));
	}

	// TX TYPE = DEFAULT (OTHER CASES)
	@Test
	public void noTx_txWithin() {
		client.noActiveTx(new ItemEntity(36, 36, 36, ItemEntity.STATUS_ACTIVE));
		assertTrue(jdbcUtil.existsItemById(36));
	}

	@Test
	public void samePersistenceContext() {
		client.checkPersistenceContext(1);
	}
	
	
	
	private void addTestItem(int num) {
		jdbcUtil.putItem(new ItemEntity(num, num + 100));
		ItemEntity e = jdbcUtil.getItemById(num);
		assertNotNull(e);
		assertEquals(num, e.getId());
		assertEquals(num + 100, e.getValue());
	}
	
	private void assertDeleted(int num) {
		assertFalse(jdbcUtil.existsItemById(num));
	}
	
	private void assertNotDeleted(int num) {
		assertTrue(jdbcUtil.existsItemById(num));
	}
}
