package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.InvalidTransactionException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.transaction.UserTransaction;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemRWDeleteRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemTransactionalRepository;

@ApplicationScoped
public class ItemTransactionalResourceImpl implements ItemTransactionalResource {

	//Traditional repository sample
	@Inject
	ItemRWDeleteRepository repository;
	
	//Repo with annotations in the interface
	@Inject
	ItemTransactionalRepository transactionalRepository;
	
	//Service reference for nested transactions
	@Inject
	ItemTransactionalResource self;

	@Inject
	UserTransaction tx;

	public ItemTransactionalResourceImpl() {
		super();
	}

	@Override
	@Transactional
	public void txDefault(ItemEntity e) {
		repository.save(e);
	}

	@Override
	@Transactional
	public void txDefaultRollback(ItemEntity e) {
		repository.save(e);
		throw new WebApplicationException(ROLLBACK, Response.Status.INTERNAL_SERVER_ERROR);
	}

	@Override
	@Transactional(value = TxType.MANDATORY)
	public void txMandatory(ItemEntity e) {
		repository.save(e);
	}

	@Override
	@Transactional(value = TxType.NEVER)
	public boolean txNever() {
		try {
			if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
				throw new WebApplicationException(Response.Status.CONFLICT);
			}
		} catch (SystemException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return true;
	}
	
	@Override
	@Transactional(value = TxType.NEVER)
	public void txNeverTxWithin(ItemEntity e) {
		repository.save(e);
	}
	
	@Override
	@Transactional(value = TxType.NEVER)
	public boolean txNeverActiveTx() {
		boolean rollback = false;
		try {
			tx.begin();
			boolean txNever = self.txNever();
			tx.commit();
			return txNever;
		} catch (WebApplicationException e) {
			rollback = true;
			throw e;
		} catch (Exception e) {
			rollback = true;
			if (e.getCause() instanceof InvalidTransactionException){
				throw new WebApplicationException(TX_EXISTS,Response.Status.INTERNAL_SERVER_ERROR);
			} 
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			doRollback(rollback);
		}
	}

	@Override
	@Transactional(value = TxType.NOT_SUPPORTED)
	public boolean txNotSupported() {
		try {
			if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
				throw new WebApplicationException(Response.Status.CONFLICT);
			}
		} catch (SystemException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return true;
	}
	
	@Override
	public boolean txNotSupportedActiveTx() {
		boolean rollback = false;
		try {
			tx.begin();
			boolean txNotSupported = self.txNotSupported();
			tx.commit();
			return txNotSupported;
		} catch (WebApplicationException e) {
			rollback = true;
			throw e;
		} catch (Exception e) {
			rollback = true;
			if (e.getCause() instanceof InvalidTransactionException){
				throw new WebApplicationException(TX_EXISTS,Response.Status.INTERNAL_SERVER_ERROR);
			} else {
				throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			}
		} finally {
			doRollback(rollback);
		}
	}

	@Override
	@Transactional(value = TxType.NOT_SUPPORTED)
	public void txNotSupportedTxWithin(ItemEntity e) {
		repository.save(e);
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void txRequired(ItemEntity e) {
		repository.save(e);
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void txRequiredRollback(ItemEntity e) {
		repository.save(e);
		throw new WebApplicationException(ROLLBACK, Response.Status.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	public void txRequiredTxWithin(ItemEntity item) {
		boolean rollback = false;
		try {
			tx.begin();
			self.txRequired(item);
			tx.commit();
		} catch (Exception e) {
			rollback = true;
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			doRollback(rollback);
		}
	}
	
	@Override
	public void txRequiredRollbackIn(ItemPair pair) {
		boolean rollback = false;
		try {
			tx.begin();
			self.txRequired(pair.getItem1());
			self.txRequiredRollback(pair.getItem2());
			tx.commit();
		} catch (Exception e) {
			rollback = true;
			throw new WebApplicationException(ROLLBACK, Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			doRollback(rollback);
		}
	}
	
	@Override
	public void txRequiredRollbackOut(ItemPair pair) {
		self.txRequired(pair.getItem1());
		self.txRequiredRollback(pair.getItem2());
	}
	

	@Override
	@Transactional(value = TxType.REQUIRES_NEW)
	public void txRequiresNew(ItemEntity e) {
		ItemEntity e2 = repository.save(e);
		e2.getStatus();
	}

	@Override
	@Transactional(value = TxType.REQUIRES_NEW)
	public void txRequiresNewRollback(ItemEntity e) {
		repository.save(e);
		throw new WebApplicationException(ROLLBACK, Response.Status.INTERNAL_SERVER_ERROR);
	}

	@Override
	public void txRequiresNewRollbackIn(ItemPair pair) {
		boolean rollback = false;
		try {
			tx.begin();
			repository.save(pair.getItem1());
			self.txRequiresNewRollback(pair.getItem2());
			tx.commit();
		} catch (Exception e) {
			rollback = true;
			throw new WebApplicationException(ROLLBACK, Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			doRollback(rollback);
		}
	}
	
	@Override
	public void txRequiresNewRollbackOut(ItemPair pair) {
		try {
			tx.begin();
			repository.save(pair.getItem1());
			self.txRequiresNew(pair.getItem2());
			throw new WebApplicationException(ROLLBACK, Response.Status.INTERNAL_SERVER_ERROR);
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			doRollback(true);
		}
	}
	
	@Override
	public void txRequiresNewRollbackInInverse(ItemPair pair) {
		boolean rollback = false;
		try {
			tx.begin();
			Exception ex = null;
			try {
				self.txRequiresNewRollback(pair.getItem1());
			}catch (Exception e) {
				ex = e;
			}
			repository.save(pair.getItem2());
			if (ex != null) throw ex;
			tx.commit();
		} catch (Exception e) {
			rollback = true;
			throw new WebApplicationException(ROLLBACK, Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			doRollback(rollback);
		}
	}
	
	@Override
	public void txRequiresNewRollbackOutInverse(ItemPair pair) {
		try {
			tx.begin();
			self.txRequiresNew(pair.getItem1());
			repository.save(pair.getItem2());
			throw new WebApplicationException(ROLLBACK, Response.Status.INTERNAL_SERVER_ERROR);
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			doRollback(true);
		}
	}
	
	@Override
	@Transactional(value = TxType.SUPPORTS)
	public void txSupports(ItemEntity e) {
		repository.save(e);
	}

	@Override
	public void txSupportsActiveTx(ItemEntity entity) {
		boolean rollback = false;
		try {
			tx.begin();
			self.txSupports(entity);
			tx.commit();
		} catch (Exception e) {
			rollback = true;
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			doRollback(rollback);
		}
	}
	
	
	@Override
	public void noActiveTx(ItemEntity e) {
		repository.save(e);
	}

	@Override
	@Transactional
	public void checkPersistenceContext(Integer id) {
		if (!repository.contains(repository.findById(id).get())){
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	private void doRollback(boolean flag) {
		if (!flag) return;
		try {
			tx.rollback();
		} catch (Exception ignored) {
		}
	}
	
	//Tx Annotations in the Repository:
	@Override
	public void deleteTxDefault(int id) {
		transactionalRepository.deleteTxDefault(id);
	}
	
	@Override
	@Transactional
	public void deleteTxDefaultWhitin(int id) {
		transactionalRepository.deleteTxDefault(id);
	}
	
	@Override
	public void deleteTxMandatory(int id) {
		transactionalRepository.deleteTxMandatory(id);
	}
	
	@Override
	@Transactional
	public void deleteTxMandatoryWhitin(int id) {
		transactionalRepository.deleteTxMandatory(id);
	}
	
	@Override
	public void deleteTxNever(int id) {
		transactionalRepository.deleteTxNever(id);
	}
	
	@Override
	@Transactional
	public void deleteTxNeverWithin(int id) {
		transactionalRepository.deleteTxNever(id);
	}
	
	@Override
	public void deleteTxNotSupported(int id) {
		transactionalRepository.deleteTxNotSupported(id);
	}
	
	@Override
	@Transactional
	public void deleteTxNotSupportedWhitin(int id) {
		transactionalRepository.deleteTxNotSupported(id);
	}
	
	@Override
	public void deleteTxRequired(int id) {
		transactionalRepository.deleteTxRequired(id);
	}
	
	@Override
	@Transactional
	public void deleteTxRequiredWhitin(int id) {
		transactionalRepository.deleteTxRequired(id);
	}
	
	@Override
	public void deleteTxRequiresNew(int id) {
		transactionalRepository.deleteTxRequiresNew(id);
	}
	
	@Override
	@Transactional
	public void deleteTxRequiresNewWhitin(int id) {
		transactionalRepository.deleteTxRequiresNew(id);
	}
	
	@Override
	public void deleteTxSupports(int id) {
		transactionalRepository.deleteTxSupports(id);
	}
	
	@Override
	@Transactional
	public void deleteTxSupportsWhitin(int id) {
		transactionalRepository.deleteTxSupports(id);
	}
	
	@Override
	public void deleteNoAnnotated(int id) {
		transactionalRepository.deleteNoAnnotated(id);
	}
	
	@Override
	@Transactional
	public void deleteNoAnnotatedWhitin(int id) {
		transactionalRepository.deleteNoAnnotated(id);
	}
}
