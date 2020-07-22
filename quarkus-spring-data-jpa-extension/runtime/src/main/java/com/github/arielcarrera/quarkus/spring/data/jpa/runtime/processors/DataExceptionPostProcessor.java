package com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.RollbackException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.lang.Nullable;

import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.exceptions.DataAccessException;

import io.quarkus.arc.Unremovable;

/**
 * 
 * {@link RepositoryProxyPostProcessor} that sets up interceptors 
 * to do exception mapping
 * 
 * @author Ariel Carrera
 */
@Unremovable
@ApplicationScoped
public class DataExceptionPostProcessor implements RepositoryProxyPostProcessor {

	@Override
	public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
		factory.addAdvice(new DataAccessExceptionMapperInterceptor());
	}

	/**
	 * Interceptor that catches exceptions and creates the {@link DataAccessException}
	 * 
	 * @author Ariel Carrera <carreraariel@gmail.com>
	 *
	 */
	static class DataAccessExceptionMapperInterceptor implements MethodInterceptor, Serializable {

		private static final long serialVersionUID = -3519640247926903558L;

		public DataAccessExceptionMapperInterceptor() {}

		@Override
		@Nullable
		public Object invoke(MethodInvocation invocation) throws Throwable {
			try {
				return invocation.proceed();
			} catch (RollbackException | javax.persistence.RollbackException e) {
				throw new DataAccessException(e.getCause());
			} catch (Exception e) {
				throw new DataAccessException(e);
			}
		}

	}
}
