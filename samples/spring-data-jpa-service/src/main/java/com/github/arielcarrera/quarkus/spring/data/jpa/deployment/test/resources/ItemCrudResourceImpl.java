package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemCrudRepository;

@Service
public class ItemCrudResourceImpl implements ItemCrudResource {

	@Inject
	ItemCrudRepository repository;

	public ItemCrudResourceImpl() {
		super();
	}
	
	public ItemCrudRepository getRepository() {
		return repository;
	}
	
	@Override
	public List<ItemEntity> findAll() {
		Iterable<ItemEntity> findAll = repository.findAll();
		ArrayList<ItemEntity> list = new ArrayList<>();
		findAll.forEach(list::add);
		return list;
	}

	@Override
	public Optional<ItemEntity> findById(@NotNull Integer id) {
		return repository.findById(id);
	}

	@Override
	public List<ItemEntity> findAllById(List<Integer> ids) {
		Iterable<ItemEntity> findAllById = repository.findAllById(ids);
		ArrayList<ItemEntity> list = new ArrayList<>();
		findAllById.forEach(list::add);
		return list;
	}

	@Override
	public void existsById(@NotNull Integer id) {
		if (!repository.existsById(id)) throw new NotFoundException();
	}

	@Override
	public long count() {
		return repository.count();
	}

	@Override
	public ItemEntity save(ItemEntity entity) {
		try {
			return repository.save(entity);
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
	public ItemEntity update(@NotNull Integer id, @NotNull ItemEntity entity) {
		try {
			entity.setId(id);
			return repository.save(entity);
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
	public Iterable<ItemEntity> saveAll(@NotEmpty List<ItemEntity> entities) {
		try {
			Iterable<ItemEntity> saveAll = repository.saveAll(entities);
			ArrayList<ItemEntity> list = new ArrayList<>();
			saveAll.forEach(list::add);
			return list;
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
	public void deleteById(@NotNull Integer id) {
    	try {
    		repository.deleteById(id);
		} catch(DataAccessException e) {
			if(e.getCause() instanceof EmptyResultDataAccessException) {
				throw new NotFoundException("ITEM_NOT_FOUND");
			}
			throw new InternalServerErrorException();
		}
	}

	@Override
	public void delete(ItemEntity entity) {
		repository.delete(entity);
	}

	@Override
	public void deleteAll(List<ItemEntity> entities) {
		repository.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}

	// Query method
	@Override
	public List<ItemEntity> findByValue(Integer value) {
		return repository.findByValue(value);
	}

}
