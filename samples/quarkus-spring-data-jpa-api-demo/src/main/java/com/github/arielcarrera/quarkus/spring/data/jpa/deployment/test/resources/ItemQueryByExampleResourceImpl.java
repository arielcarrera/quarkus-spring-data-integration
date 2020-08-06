package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import javax.inject.Inject;
//import javax.persistence.NonUniqueResultException;
//import javax.transaction.Transactional;
//import javax.ws.rs.NotFoundException;
//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.Response.Status;
//
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.ExampleMatcher;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.domain.Sort.Direction;
//import org.springframework.stereotype.Service;
//
//import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
//import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;
//import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemQueryByExampleRepository;
//import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.PageUtil;

//@Transactional
//public class ItemQueryByExampleResourceImpl implements ItemQueryByExampleResource {
//
//	@Inject
//	ItemQueryByExampleRepository repository;
//	
//
//	public ItemQueryByExampleResourceImpl() {
//		super();
//	}
//
//    @Override
//    public List<ItemEntity> findAllByExample(boolean sort, ItemEntity exampleData) {
//    	Example<ItemEntity> example = Example.of(exampleData,
//    			ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
//    	Iterable<ItemEntity> iterable = sort ? repository.findAll(example, Sort.by(Direction.DESC, "id")) : repository.findAll(example);
//    	ArrayList<ItemEntity> list = new ArrayList<>();
//    	iterable.forEach(list::add);
//		return list;
//    }
//    
//    @Override
//    public Page<ItemEntity> findAllByExample(boolean sort, int from, int size, ItemEntity exampleData) {
//    	Example<ItemEntity> example = Example.of(exampleData,
//    			ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
//        return PageUtil.of(sort ? repository.findAll(example, PageRequest.of(from, size, Sort.by(Direction.DESC, "id")))
//        		: repository.findAll(example, PageRequest.of(from, size)));
//    }
//    
//    @Override
//    public Optional<ItemEntity> findOneByExample(ItemEntity exampleData) {
//    	Example<ItemEntity> example = Example.of(exampleData,
//    			ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
//    	try {
//    		return repository.findOne(example);
//    	} catch (Throwable e) {
//    		Throwable cause = e.getCause();
//			if (cause instanceof NonUniqueResultException) {
//				throw new WebApplicationException(Status.CONFLICT);
//			} else {
//				throw e;
//			}
//		}
//    }
//
//	@Override
//	public long countByExample(ItemEntity exampleData) {
//		Example<ItemEntity> example = Example.of(exampleData,
//    			ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
//		return repository.count(example);
//	}
//	
//	@Override
//	public void existsByExample(ItemEntity exampleData) {
//		Example<ItemEntity> example = Example.of(exampleData,
//    			ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
//		if (!repository.exists(example)) throw new NotFoundException();
//	}
//    
//}
