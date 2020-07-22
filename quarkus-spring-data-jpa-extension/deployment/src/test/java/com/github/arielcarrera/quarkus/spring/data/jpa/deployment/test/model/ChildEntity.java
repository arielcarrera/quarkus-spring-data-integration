package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@Entity
public class ChildEntity implements Identifiable<Integer> {

	@Id
	private Integer id;

	private Integer value;

	public ChildEntity() {
		super();
	}

	public ChildEntity(Integer id, Integer value) {
		super();
		this.id = id;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
