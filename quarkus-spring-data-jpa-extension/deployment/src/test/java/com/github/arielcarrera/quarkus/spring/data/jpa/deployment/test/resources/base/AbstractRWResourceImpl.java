package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources.base;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.transaction.UserTransaction;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.hibernate.exception.ConstraintViolationException;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Codifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Identifiable;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadWriteRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.exceptions.DataAccessException;

public abstract class AbstractRWResourceImpl<T extends Identifiable<ID> & Codifiable<CODE>, ID, CODE> 
	extends AbstractReadResourceImpl<T, ID, CODE> implements AbstractRWResource<T, ID, CODE> {

	public abstract ReadWriteRepository<T, ID> getRepository();
    
    public abstract UserTransaction getTx();

    public AbstractRWResourceImpl() {
    	super();
    }
    
	@Override
	public T save(boolean flush, T entity) {
		try {
			return flush ? getRepository().saveAndFlush(entity) : getRepository().save(entity);
		} catch (DataAccessException e) {
			if (e.getCause() != null && e.getCause() instanceof PersistenceException) {
				if (e.getCause().getCause() != null && e.getCause().getCause() instanceof ConstraintViolationException) {
					throw new WebApplicationException(Response.Status.CONFLICT);
				}
			}
			throw new InternalServerErrorException();
		}
	}
	
	@Override
	public T update(ID id, T entity) {
		try {
			return getRepository().save(entity);
		} catch (DataAccessException e) {
			if (e.getCause() != null && e.getCause() instanceof PersistenceException) {
				if (e.getCause().getCause() != null && e.getCause().getCause() instanceof ConstraintViolationException) {
					throw new WebApplicationException(Response.Status.CONFLICT);
				}
			}
			throw new InternalServerErrorException();
		}
	}
	
	@Override
	public Iterable<T> saveAll(List<T> entities) {
		try {
			return getRepository().saveAll(entities);
		} catch (DataAccessException e) {
			if (e.getCause() != null && e.getCause() instanceof PersistenceException) {
				if (e.getCause().getCause() != null && e.getCause().getCause() instanceof ConstraintViolationException) {
					throw new WebApplicationException(Response.Status.CONFLICT);
				}
			}
			throw new InternalServerErrorException();
		}
	}

	@Override
	public T flush(T entity) {
		try {
			getTx().begin();
			entity = getRepository().save(entity);
			getRepository().flush();
			getTx().commit();
		} catch (Exception e) {
			throw new InternalServerErrorException();
		}
		return entity;
	}

}
