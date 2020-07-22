package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

@Vetoed
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemTransactionalResource {

	@RegisterRestClient(configKey="item-api")
	@Path("/items")
	public interface ItemTransactionalClient extends ItemTransactionalResource {
	}

	String TX_EXISTS = "TX EXISTS";
	String ROLLBACK = "ROLLBACK";

	public static class ItemPair {
		ItemEntity item1;
		ItemEntity item2;
		
		public ItemPair() {
			super();
		}
		
		public ItemPair(ItemEntity item1, ItemEntity item2) {
			super();
			this.item1 = item1;
			this.item2 = item2;
		}
		
		public ItemEntity getItem1() {
			return item1;
		}
		
		public ItemEntity getItem2() {
			return item2;
		}
	}

	// TX TYPE = DEFAULT
	@POST
	@Path("/tx/default")
	void txDefault(@NotNull ItemEntity e);

	@POST
	@Path("/tx/default/rollback")
	void txDefaultRollback(@NotNull ItemEntity e);

	// TX TYPE = MANDATORY
	@POST
	@Path("/tx/mandatory")
	void txMandatory(@NotNull ItemEntity e);

	// TX TYPE = NEVER
	@POST
	@Path("/tx/never")
	boolean txNever();

	@POST
	@Path("/tx/never/active")
	boolean txNeverActiveTx();

	@POST
	@Path("/tx/never/new")
	void txNeverTxWithin(@NotNull ItemEntity e);

	// TX TYPE = NOT SUPPORTED
	@POST
	@Path("/tx/not-supported")
	boolean txNotSupported();

	@POST
	@Path("/tx/not-supported/active")
	boolean txNotSupportedActiveTx();

	@POST
	@Path("/tx/not-supported/new")
	void txNotSupportedTxWithin(@NotNull ItemEntity e);

	// TX TYPE = REQUIRED
	@POST
	@Path("/tx/required")
	void txRequired(@NotNull ItemEntity e);

	@POST
	@Path("/tx/required/active")
	void txRequiredTxWithin(@NotNull ItemEntity e);

	@POST
	@Path("/tx/required/rollback")
	void txRequiredRollback(@NotNull ItemEntity e);

	@POST
	@Path("/tx/required/rollback/in")
	void txRequiredRollbackIn(@NotNull ItemPair pair);

	@POST
	@Path("/tx/required/rollback/out")
	void txRequiredRollbackOut(@NotNull ItemPair pair);

	// TX TYPE = REQUIRES NEW
	@POST
	@Path("/tx/requires-new")
	void txRequiresNew(@NotNull ItemEntity e);

	@POST
	@Path("/tx/requires-new/rollback")
	void txRequiresNewRollback(@NotNull ItemEntity e);

	@POST
	@Path("/tx/requires-new/rollback/in")
	void txRequiresNewRollbackIn(@NotNull ItemPair pair);

	@POST
	@Path("/tx/requires-new/rollback/out")
	void txRequiresNewRollbackOut(@NotNull ItemPair pair);

	@POST
	@Path("/tx/requires-new/rollback/in/inverse")
	void txRequiresNewRollbackInInverse(@NotNull ItemPair pair);

	@POST
	@Path("/tx/requires-new/rollback/out/inverse")
	void txRequiresNewRollbackOutInverse(@NotNull ItemPair pair);

	// TX TYPE = SUPPORTS
	@POST
	@Path("/tx/supports")
	void txSupports(@NotNull ItemEntity e);

	@POST
	@Path("/tx/supports/active")
	void txSupportsActiveTx(@NotNull ItemEntity entity);

	// TX TYPE = DEFAULT (OTHER CASES)
	@HEAD
	@Path("/tx/default/contains/{id}")
	void checkPersistenceContext(@PathParam("id") @NotNull @Min(0) Integer id);
	
	@POST
	@Path("/tx/default/not-annotated")
	void noActiveTx(@NotNull ItemEntity e);

	
	// REPO WITH ANNOTATIONS IN INTERFACE:
	@POST
	@Path("/repo/no-annotated/active")
	void deleteNoAnnotatedWhitin(@QueryParam("id") int id);

	@POST
	@Path("/repo/no-annotated")
	void deleteNoAnnotated(@QueryParam("id") int id);

	@POST
	@Path("/repo/supports/active")
	void deleteTxSupportsWhitin(@QueryParam("id") int id);

	@POST
	@Path("/repo/supports")
	void deleteTxSupports(@QueryParam("id") int id);

	@POST
	@Path("/repo/requires-new/active")
	void deleteTxRequiresNewWhitin(@QueryParam("id") int id);

	@POST
	@Path("/repo/requires-new")
	void deleteTxRequiresNew(@QueryParam("id") int id);

	@POST
	@Path("/repo/required/active")
	void deleteTxRequiredWhitin(@QueryParam("id") int id);

	@POST
	@Path("/repo/required")
	void deleteTxRequired(@QueryParam("id") int id);

	@POST
	@Path("/repo/not-supported/active")
	void deleteTxNotSupportedWhitin(@QueryParam("id") int id);

	@POST
	@Path("/repo/not-supported")
	void deleteTxNotSupported(@QueryParam("id") int id);

	@POST
	@Path("/repo/never/active")
	void deleteTxNeverWithin(@QueryParam("id") int id);

	@POST
	@Path("/repo/never")
	void deleteTxNever(@QueryParam("id") int id);

	@POST
	@Path("/repo/mandatory/active")
	void deleteTxMandatoryWhitin(@QueryParam("id") int id);

	@POST
	@Path("/repo/mandatory")
	void deleteTxMandatory(@QueryParam("id") int id);

	@POST
	@Path("/repo/default/active")
	void deleteTxDefaultWhitin(@QueryParam("id") int id);

	@POST
	@Path("/repo/default")
	void deleteTxDefault(@QueryParam("id") int id);

}
