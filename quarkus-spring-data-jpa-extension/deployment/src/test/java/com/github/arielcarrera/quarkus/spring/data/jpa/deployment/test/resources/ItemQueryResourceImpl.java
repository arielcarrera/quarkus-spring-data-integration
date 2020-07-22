package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemQueryRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.PageUtil;

@Transactional
public class ItemQueryResourceImpl implements ItemQueryResource {

	@Inject
	ItemQueryRepository repository;

	public ItemQueryResourceImpl() {
		super();
	}

	@Override
	public List<ItemEntity> queryUniqueValue(Integer uniqueValue) {
		return repository.queryUniqueValue(uniqueValue);
	}
	
	@Override
	public Page<ItemEntity> queryUniqueValue(Integer uniqueValue, int from, int size) {
		 return PageUtil.of(repository.queryUniqueValue(uniqueValue, PageRequest.of(from, size)));
	}
	
	@Override
	public List<ItemEntity> namedQueryUniqueValue(Integer uniqueValue) {
		return repository.namedQueryUniqueValue(uniqueValue);
	}
	
	@Override
	public Page<ItemEntity> namedQueryUniqueValue(Integer uniqueValue, int from, int size) {
		 return PageUtil.of(repository.namedQueryUniqueValue(uniqueValue, PageRequest.of(from, size)));
	}
	
	@Override
	public List<ItemEntity> nativeQueryUniqueValue(Integer uniqueValue) {
		return repository.nativeQueryUniqueValue(uniqueValue);
	}
	
	@Override
	public Page<ItemEntity> nativeQueryUniqueValue(Integer uniqueValue, int from, int size) {
		 return PageUtil.of(repository.nativeQueryUniqueValue(uniqueValue, PageRequest.of(from, size)));
	}

	@Override
	public List<ItemEntity> queryUniqueValueNamedParameter(Integer uniqueValue) {
		return repository.queryUniqueValueNamedParameter(uniqueValue);
	}
	
	@Override
	public List<ItemEntity> queryUniqueValueSpelExpressions(Integer uniqueValue) {
		return repository.queryUniqueValueSpelExpressions(uniqueValue);
	}

	@Override
	public int updateValue(Integer value, Integer id) {
		return repository.updateValue(value, id);
	}

	@Override
	public void deleteInBulkByValue(Integer value) {
		repository.deleteInBulkByValue(value);
	}
}
