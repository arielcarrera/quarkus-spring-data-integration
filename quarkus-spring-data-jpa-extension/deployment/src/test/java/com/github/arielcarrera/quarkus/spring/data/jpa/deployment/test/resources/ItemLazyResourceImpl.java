package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemLazyRepository;

@Transactional
public class ItemLazyResourceImpl implements ItemLazyResource {

	@Inject
	ItemLazyRepository repository;

	public ItemLazyResourceImpl() {
		super();
	}
	
    public List<ItemEntity> findAll() {
    	List<ItemEntity> findAll = repository.findAll();
    	fireLoadLazyReference(findAll);
        return findAll;
    }
	
	// query method - property expression
	public List<ItemEntity> findByLazyValue(Integer value) {
		List<ItemEntity> list = repository.findByLazyValue(value);
		fireLoadLazyReference(list);
		return list;
	}
	
	public List<ItemEntity> findByLazy_Value(Integer value) {
		List<ItemEntity> list = repository.findByLazy_Value(value);
		fireLoadLazyReference(list);
		return list;
	}
	
    //due to issue with lazy loading and jackson: https://github.com/quarkusio/quarkus/issues/4644
	private void fireLoadLazyReference(List<ItemEntity> findAll) {
		findAll.stream().filter(i -> i.getLazy() != null).forEach(i -> i.getLazy().getValue());
	}
}
