package com.github.arielcarrera.quarkus.spring.data.jpa.runtime;

import javax.enterprise.util.AnnotationLiteral;
import javax.transaction.Transactional;

/**
 * Literal for {@link Transactional} annotation
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@SuppressWarnings("all")
public class TransactionalLiteral extends AnnotationLiteral<Transactional> implements Transactional {

    private TransactionalLiteral() {
    }

    public static final TransactionalLiteral INSTANCE = new TransactionalLiteral();

	@Override
	public TxType value() {
		return TxType.REQUIRED;
	}

	@Override
	public Class[] rollbackOn() {
		return new Class[] {RuntimeException.class};
	}

	@Override
	public Class[] dontRollbackOn() {
		return new Class[] {};
	}
}