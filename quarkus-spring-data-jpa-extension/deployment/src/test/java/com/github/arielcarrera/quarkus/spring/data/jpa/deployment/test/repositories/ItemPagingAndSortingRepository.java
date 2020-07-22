package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

public interface ItemPagingAndSortingRepository extends ItemCrudRepository, PagingAndSortingRepository<ItemEntity,Integer> {

}
