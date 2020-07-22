package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.UserTransaction;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.hibernate.LazyInitializationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Codifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Identifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.PageUtil;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.exceptions.DataAccessException;

public abstract class AbstractReadResourceImpl<T extends Identifiable<ID> & Codifiable<CODE>, ID, CODE> 
	implements AbstractReadResource<T, ID, CODE> {

	public abstract ReadRepository<T, ID> getRepository();
    
    public abstract UserTransaction getTx();

    public AbstractReadResourceImpl() {
    	super();
    }
    
    @Override
    public List<T> findAll(boolean sort) {
		return sort ? getRepository().findAll(Sort.by(Direction.DESC, "id"))
    				: getRepository().findAll();
    }
    
    @Override
    public Page<T> findAllPageable(boolean sort, int from, int size) {
        return PageUtil.of(sort ? getRepository().findAll(PageRequest.of(from, size, Sort.by(Direction.DESC, "id")))
        		: getRepository().findAll(PageRequest.of(from, size)));
    }
    
    @Override
	public Optional<T> findById(ID id, 
			boolean queryMethod) {
		return queryMethod ? Optional.ofNullable(getRepository().findOneById(id)) : getRepository().findById(id);
	}

    @Override
	public void existsById(ID id) {
    	if (!getRepository().existsById(id)) throw new NotFoundException();
	}
    
    @Override
    public List<T> findAllById(List<ID> ids) {
    	return getRepository().findAllById(ids);
    }
    

    @Override
	public void existsEntityManager() {
    	if (getRepository().entityManager() == null) throw new NotFoundException();
	}
    
    @Override
	public boolean contains(ID id, boolean globalTx,
			boolean queryMethod) {
    	try {
	    	if (globalTx) {
		    	getTx().begin();
				try {
					return emContains(id, queryMethod);
				} finally {
					getTx().commit();
				}
	    	}
    		return emContains(id, queryMethod);
    	} catch (NotFoundException nf) {
    		throw nf;
		} catch (Exception e) {
    		throw new InternalServerErrorException();
		}
	}

	private boolean emContains(ID id, boolean queryMethod) {
		Optional<T> opt = queryMethod ? Optional.ofNullable(getRepository().findOneById(id)) : getRepository().findById(id);
    	if (opt.isEmpty()) throw new NotFoundException(ITEM_NOT_FOUND);
		return getRepository().contains(opt.get());
	}

	@Override
	public long count() {
		return getRepository().count();
	}

	@Override
	public CODE findCodeByReference(ID id, boolean globalTx, boolean outTx) {
		try {
			if (globalTx) {
				T item;
				getTx().begin();
				try {
					item = getOne(id);
					if (!outTx) return item.getCode();
				} finally {
					getTx().commit();
				}
				return item.getCode();
			}
			return getOne(id).getCode();
		} catch(EntityNotFoundException e) {
			throw new NotFoundException(ITEM_NOT_FOUND);
		} catch(LazyInitializationException e) {
			throw new InternalServerErrorException(LazyInitializationException.class.getName());
		} catch(Exception e) {
			throw new InternalServerErrorException();
		}
	}

	private T getOne(ID id) {
		T p = getRepository().getOne(id);
		if (p == null) throw new NotFoundException(ITEM_NOT_FOUND);
		return p;
	}
	
	
    @Override
    public List<T> findAllByExample(boolean sort, T exampleData) {
    	Example<T> example = Example.of(exampleData,
    			ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		return sort ? getRepository().findAll(example, Sort.by(Direction.DESC, "id")) : getRepository().findAll(example);
    }
    
    @Override
    public Page<T> findAllByExample(boolean sort, int from, int size, T exampleData) {
    	Example<T> example = Example.of(exampleData,
    			ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
        return PageUtil.of(sort ? getRepository().findAll(example, PageRequest.of(from, size, Sort.by(Direction.DESC, "id")))
        		: getRepository().findAll(example, PageRequest.of(from, size)));
    }
    
    @Override
    public Optional<T> findOneByExample(T exampleData) {
    	Example<T> example = Example.of(exampleData,
    			ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
    	try {
    		return getRepository().findOne(example);
    	} catch (DataAccessException e) {
    		Throwable cause = e.getCause();
			if (cause instanceof NonUniqueResultException) {
				throw new WebApplicationException(Status.CONFLICT);
			} else {
				throw e;
			}
		}
    }

	@Override
	public long countByExample(T exampleData) {
		Example<T> example = Example.of(exampleData,
    			ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		return getRepository().count(example);
	}

	@Override
	public void existsByExample(T exampleData) {
		Example<T> example = Example.of(exampleData,
    			ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnorePaths("status"));
		if (!getRepository().exists(example)) throw new NotFoundException();
	}
}
