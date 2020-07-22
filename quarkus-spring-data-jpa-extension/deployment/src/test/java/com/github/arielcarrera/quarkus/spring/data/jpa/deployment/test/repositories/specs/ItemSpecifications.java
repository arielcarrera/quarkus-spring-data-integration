package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;

public class ItemSpecifications {

	public static Specification<ItemEntity> isValueLessThan(Integer value) {
		return new Specification<ItemEntity>() {
			private static final long serialVersionUID = 1L;
			public Predicate toPredicate(Root<ItemEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder.lessThan(root.get("value"), value);
			}
		};
	}
}