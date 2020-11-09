package com.github.arielcarrera.quarkus.spring.data.jpa.deployment;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import io.quarkus.arc.DefaultBean;

/**
 * EntityManager producer
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
public class EntityManagerProducer {

    @Inject
    private EntityManagerFactory emfCreation;

    @Produces
    @Dependent @DefaultBean
    public EntityManager produceEntityManagerCreation() {
        return emfCreation.createEntityManager();
    }

    public void closeCreation(@Disposes EntityManager em) {
        em.close();
    }
}
