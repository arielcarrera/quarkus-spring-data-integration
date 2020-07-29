package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model;

public interface Codifiable<T> {

	public T getCode();

	public void setCode(T code);
	
}
