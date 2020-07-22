package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadWriteDeleteRepository;

public interface ItemTransactionalRepository extends ReadWriteDeleteRepository<ItemEntity, Integer>{

	@Transactional
	default void deleteTxDefault(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.MANDATORY)
	default void deleteTxMandatory(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.NEVER)
	default void deleteTxNever(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.NOT_SUPPORTED)
	default void deleteTxNotSupported(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.REQUIRED)
	default void deleteTxRequired(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.REQUIRES_NEW)
	default void deleteTxRequiresNew(int id) {
		deleteById(id);
	}
	
	@Transactional(value=TxType.SUPPORTS)
	default void deleteTxSupports(int id) {
		deleteById(id);
	}
	
	default void deleteNoAnnotated(int id) {
		deleteById(id);
	}
}
