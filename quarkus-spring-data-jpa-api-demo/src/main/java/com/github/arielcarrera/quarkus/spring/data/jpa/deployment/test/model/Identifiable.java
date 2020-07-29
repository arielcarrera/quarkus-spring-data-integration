package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model;

public interface Identifiable<ID> {

	public ID getId();

	public void setId(ID id);
	
}
