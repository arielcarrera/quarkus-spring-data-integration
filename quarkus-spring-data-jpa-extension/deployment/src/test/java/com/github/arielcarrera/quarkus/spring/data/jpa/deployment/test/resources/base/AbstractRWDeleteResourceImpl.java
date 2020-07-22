package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.UserTransaction;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.springframework.dao.EmptyResultDataAccessException;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Codifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Identifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadWriteDeleteRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.exceptions.DataAccessException;

public abstract class AbstractRWDeleteResourceImpl<T extends Identifiable<ID> & Codifiable<CODE>, ID, CODE> 
	extends AbstractRWResourceImpl<T, ID, CODE> implements AbstractRWDeleteResource<T, ID, CODE> {

	public abstract ReadWriteDeleteRepository<T, ID> getRepository();
    
    public abstract UserTransaction getTx();

    public AbstractRWDeleteResourceImpl() {
    	super();
    }
    
    @Override
    public void deleteById(ID id) {
    	try {
    		getRepository().deleteById(id);
		} catch(DataAccessException e) {
			if(e.getCause() instanceof EmptyResultDataAccessException) {
				throw new NotFoundException(AbstractRWDeleteResource.ITEM_NOT_FOUND);
			}
			throw new InternalServerErrorException();
		}
    }

    @Override
    public void delete(boolean detached, T entity) {
    	try {
    		if (detached) {
    			getRepository().delete(entity);
    		} else {
    			getRepository().delete(getRepository().getOne(entity.getId()));
    		}
		} catch(DataAccessException e) {
			if(e.getCause() instanceof EmptyResultDataAccessException) {
				throw new NotFoundException(AbstractRWDeleteResource.ITEM_NOT_FOUND);
			}
			throw new InternalServerErrorException();
		}
    }

    @Override
    public void deleteAll(List<T> entities) {
    	try {
    		getRepository().deleteAll(entities);
    	} catch(DataAccessException e) {
			if(e.getCause() instanceof IllegalArgumentException) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
		}
    }

    @Override
    public void deleteAll() {
    	getRepository().deleteAll();
    }

    @Override
    public void deleteInBatch(List<T> entities) {
    	List<T> collect = entities.stream().map(e -> getRepository().getOne(e.getId())).collect(Collectors.toList());
    	getRepository().deleteInBatch(collect);
    }

    @Override
    public void deleteAllInBatch() {
    	getRepository().deleteAllInBatch();
    }

}
