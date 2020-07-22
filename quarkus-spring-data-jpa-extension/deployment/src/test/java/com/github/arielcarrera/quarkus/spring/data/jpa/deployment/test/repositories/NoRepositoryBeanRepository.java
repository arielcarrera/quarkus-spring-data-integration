package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

@NoRepositoryBean
public interface NoRepositoryBeanRepository extends Repository<ItemEntity,Integer> {

	List<ItemEntity> findAll();
	
}
