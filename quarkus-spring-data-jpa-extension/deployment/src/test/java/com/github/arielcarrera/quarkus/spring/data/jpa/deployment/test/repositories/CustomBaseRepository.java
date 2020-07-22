package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class CustomBaseRepository<T, ID> extends SimpleJpaRepository<T, ID> {

	public static final String MSG = "CUSTOM BASE REPOSITORY!";

	private final EntityManager entityManager;

	public CustomBaseRepository(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}
	
	public CustomBaseRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager, EntityManager entityManagerCreation) {
		super(entityInformation, entityManager, entityManagerCreation);
		this.entityManager = entityManager;
	}

	public CustomBaseRepository(Class<T> domainClass, EntityManager em) {
		this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em, null);
	}
	
	public CustomBaseRepository(Class<T> domainClass, EntityManager em, EntityManager emCreation) {
		this(JpaEntityInformationSupport.getEntityInformation(domainClass, (emCreation != null ? emCreation : em)), em, emCreation);
	}

	@Transactional
	public <S extends T> S save(S entity) {
		if (entity == null) throw new IllegalArgumentException(MSG);
		
		entityManager.persist(entity);
		
		return entity;
	}

}
