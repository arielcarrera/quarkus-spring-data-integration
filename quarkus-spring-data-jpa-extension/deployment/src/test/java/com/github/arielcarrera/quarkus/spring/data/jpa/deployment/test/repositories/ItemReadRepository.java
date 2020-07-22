package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.base.ReadRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments.CustomFragment;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.fragments.CustomFragment2;

public interface ItemReadRepository extends ReadRepository<ItemEntity, Integer>, CustomFragment, CustomFragment2 {

}
