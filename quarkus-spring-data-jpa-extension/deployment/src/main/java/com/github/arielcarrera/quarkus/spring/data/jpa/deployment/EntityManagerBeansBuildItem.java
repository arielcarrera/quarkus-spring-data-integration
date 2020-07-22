package com.github.arielcarrera.quarkus.spring.data.jpa.deployment;

import java.util.Set;

import javax.persistence.EntityManager;

import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.builder.item.SimpleBuildItem;

/**
 * Build item with discovered {@link EntityManager} beans data
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public final class EntityManagerBeansBuildItem extends SimpleBuildItem {

	private final Set<BeanInfo> entityManagerBeans;

    public EntityManagerBeansBuildItem(Set<BeanInfo> entityManagerBeans) {
        this.entityManagerBeans = entityManagerBeans;
    }

    public Set<BeanInfo> getEntityManagerBeans() {
        return entityManagerBeans;
    }

}