package com.github.arielcarrera.quarkus.spring.data.jpa.deployment;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.data.repository.cdi.RepositoryCreation;

/**
 * EntityManager producer
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ApplicationScoped
public class EntityManagerProducer {

//    @Inject @RepositoryCreation //@Alternative
//    private EntityManagerFactory emfCreation;
//
//    @Produces @RepositoryCreation
//    @Dependent //@Alternative
//    public EntityManager produceEntityManagerCreation() {
//        return emfCreation.createEntityManager();
//    }
//
//    public void closeCreation(@Disposes @RepositoryCreation EntityManager em) {
//        em.close();
//    }
}
