package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadWriteDeleteRepository;

public interface ItemRWDeleteRepository extends ReadWriteDeleteRepository<ItemEntity, Integer> {

}
