package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ErrorResponse {
	
	String message;

	public ErrorResponse() {
		super();
	}
	
	public ErrorResponse(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
