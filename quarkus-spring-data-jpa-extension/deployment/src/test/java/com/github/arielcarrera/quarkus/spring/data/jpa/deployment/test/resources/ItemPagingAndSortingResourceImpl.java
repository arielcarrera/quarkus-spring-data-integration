package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.Min;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemPagingAndSortingRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils.PageUtil;

public class ItemPagingAndSortingResourceImpl extends ItemCrudResourceImpl implements ItemPagingAndSortingResource {

	@Inject
	ItemPagingAndSortingRepository repository;

	public ItemPagingAndSortingResourceImpl() {
		super();
	}
	
	@Override
	public ItemPagingAndSortingRepository getRepository() {
		return repository;
	}

	@Override
	public Page<ItemEntity> findAllPageable(boolean sort, @Min(0) int from, @Min(1) int size) {
		try {
			return PageUtil.of(sort ? getRepository().findAll(PageRequest.of(from, size, Sort.by(Direction.DESC, "id")))
					: getRepository().findAll(PageRequest.of(from, size)));
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<ItemEntity> findAllSort(boolean desc) {
		Iterable<ItemEntity> iterable = desc ? getRepository().findAll(Sort.by(Direction.DESC, "id"))
				: getRepository().findAll(Sort.by(Direction.ASC, "id"));
		ArrayList<ItemEntity> list = new ArrayList<>();
		iterable.forEach(list::add);
		return list;
	}
    
}
