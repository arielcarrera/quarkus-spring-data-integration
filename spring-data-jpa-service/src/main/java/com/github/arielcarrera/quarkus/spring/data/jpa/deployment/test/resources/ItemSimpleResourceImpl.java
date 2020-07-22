package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemSimpleRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.PageUtil;

@Service
@Transactional
public class ItemSimpleResourceImpl implements ItemSimpleResource {

	@Inject
	ItemSimpleRepository repository;

	public ItemSimpleResourceImpl() {
		super();
	}
	
	@Override
    public List<ItemEntity> findAll() {
    	List<ItemEntity> findAll = repository.findAll();
        return findAll;
    }
    
    @Override
	public ItemEntity save(ItemEntity entity) {
		return repository.save(entity);
	}
	
	// query method
	@Override
	public List<ItemEntity> findByValue(Integer value) {
		List<ItemEntity> list = repository.findByValue(value);
		return list;
	}
	
	// query method - property expression
	@Override
	public List<ItemEntity> findByChildValue(Integer value) {
		List<ItemEntity> list = repository.findByChildValue(value);
		return list;
	}
	
	@Override
	public List<ItemEntity> findByChild_Value(Integer value) {
		List<ItemEntity> list = repository.findByChild_Value(value);
		return list;
	}

    // derived count query
	@Override
    public long countByValue(Integer value) {
    	return repository.countByValue(value);
    }
    
    // derived delete query
    @Override
    public long deleteByValue(Integer value) {
    	return repository.deleteByValue(value);
    }

    // page
	@Override
	public Page<ItemEntity> findAll(int from, int size) {
		return PageUtil.of(repository.findAll(PageRequest.of(from, size)));
	}

	// slice
	@Override
	public Page<ItemEntity> findByValue(Integer value, int from, int size) {
		return PageUtil.of(repository.findByValue(value, PageRequest.of(from, size)));
	}

	@Override
	public ItemEntity findFirstByOrderByValueDesc() {
		return repository.findFirstByOrderByValueDesc();
	}

	@Override
	public Page<ItemEntity> findTop3ByValueLessThan(Integer value, int from, int size) {
		return PageUtil.of(repository.findTop3ByValueLessThan(value, PageRequest.of(from, size)));
	}
	
	@Override
	public List<ItemEntity> findStreamableByCodeIn(List<Integer> codes) {
		Streamable<ItemEntity> streamable = repository.findStreamableByCodeIn(codes);
		return streamable.toList();
	}
	
	@Override
	public List<ItemEntity> findAsyncByUniqueValue(Integer uniqueValue) {
		Future<List<ItemEntity>> future = repository.findAsyncByUniqueValue(uniqueValue);
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new WebApplicationException("error during async call", Status.INTERNAL_SERVER_ERROR);
		}
	}
}
