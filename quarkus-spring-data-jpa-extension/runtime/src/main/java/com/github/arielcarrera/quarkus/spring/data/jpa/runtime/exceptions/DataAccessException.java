package com.github.arielcarrera.quarkus.spring.data.jpa.runtime.exceptions;

/**
 * Data Access Exception
 * 
 * Exception that encapsulates persistence exceptions
 * that occur during the execution of a query
 * 
 * @author Ariel Carrera
 *
 */
public class DataAccessException extends RuntimeException {
	private static final long serialVersionUID = 1106710614844023088L;

	public DataAccessException() {
		super();
	}
	
	public DataAccessException(final String message) {
		super(message);
	}

	public DataAccessException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DataAccessException(final Throwable cause) {
		super(cause);
	}
	
}