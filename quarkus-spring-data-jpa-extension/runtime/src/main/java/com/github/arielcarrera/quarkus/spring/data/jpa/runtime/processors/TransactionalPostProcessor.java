package com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transactional;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.TransactionalLiteral;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcInvocationContext;
import io.quarkus.arc.InjectableInterceptor;
import io.quarkus.narayana.jta.runtime.interceptor.TransactionalInterceptorBase;

/**
 * 
 * {@link RepositoryProxyPostProcessor} that sets up interceptors to do
 * transactional management
 * 
 * @author Ariel Carrera
 *
 */
public class TransactionalPostProcessor implements RepositoryProxyPostProcessor {

	@Override
	public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
		factory.addAdvice(new TransactionalInterceptorProcessor());
	}

	/**
	 * Transaction handling interceptor
	 * 
	 * @author Ariel Carrera <carreraariel@gmail.com>
	 */
	static class TransactionalInterceptorProcessor implements MethodInterceptor, Serializable {

		private static final long serialVersionUID = -3519640247926903556L;

		private static final ConcurrentHashMap<String, TransactionalInterceptorMetadata> cache = 
				new ConcurrentHashMap<>();

		static class TransactionalInterceptorMetadata {
			InjectableInterceptor<? extends TransactionalInterceptorBase> interceptorBean;
			Transactional transactionalAnnotation;
			
			public TransactionalInterceptorMetadata(
					InjectableInterceptor<? extends TransactionalInterceptorBase> interceptorBean,
					Transactional transactionalAnnotation) {
				super();
				this.interceptorBean = interceptorBean;
				this.transactionalAnnotation = transactionalAnnotation;
			}
			
			public InjectableInterceptor<? extends TransactionalInterceptorBase> getInterceptorBean() {
				return interceptorBean;
			}
			public Transactional getTransactionalAnnotation() {
				return transactionalAnnotation;
			}
			
		}
		/**
		 * Create a new TransactionInterceptor.
		 * <p>
		 * Transaction manager and transaction attributes still need to be set.
		 * 
		 * @see #setTransactionManager
		 * @see #setTransactionAttributes(java.util.Properties)
		 * @see #setTransactionAttributeSource(TransactionAttributeSource)
		 */
		public TransactionalInterceptorProcessor() {
		}

		
		@SuppressWarnings("unchecked")
		@Override
		@Nullable
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String methodStr = invocation.getMethod().toString();
			//Cache of Interceptor Beans by Method/TxAnnotation
			TransactionalInterceptorMetadata metadata = cache.get(methodStr);
			if (metadata == null) { // method string never processed
				Transactional txAnnotation = invocation.getMethod().getAnnotation(Transactional.class);
				// If there is no method annotation it will search at Class/Interface level
				if (txAnnotation == null) {
					txAnnotation = invocation.getMethod().getDeclaringClass().getAnnotation(Transactional.class);
					// applying default TransactionalLiteral.INSTANCE annotation
					if (txAnnotation == null) {
						txAnnotation = TransactionalLiteral.INSTANCE;
					}
				}
				//resolve interceptor bean
				List<Interceptor<?>> resolveInterceptors = Arc.container().beanManager()
						.resolveInterceptors(InterceptionType.AROUND_INVOKE, txAnnotation);
				for (Interceptor<?> interceptor : resolveInterceptors) {
					if (interceptor instanceof InjectableInterceptor
							&& TransactionalInterceptorBase.class.isAssignableFrom(interceptor.getBeanClass())) {
						metadata = new TransactionalInterceptorMetadata(
								(InjectableInterceptor<? extends TransactionalInterceptorBase>) interceptor, txAnnotation);
						cache.put(methodStr, metadata);
					}
				}
				if (metadata == null) throw new RuntimeException("Transactional interceptor not found");
			}

			//create a new interceptor instance
			TransactionalInterceptorBase interceptorInstance = metadata.getInterceptorBean()
					.get(Arc.container().beanManager().createCreationalContext(null));
			return interceptorInstance.intercept(createMethodInvocation(invocation, metadata.getTransactionalAnnotation()));
		}


		private InvocationContext createMethodInvocation(MethodInvocation invocation, Transactional txAnnotation) {
			return new InvocationContext() {

				@Override
				public void setParameters(Object[] params) {
				}

				@Override
				public Object proceed() throws Exception {
					try {
						return invocation.proceed();
					} catch (Exception e) {
						throw (Exception) e;
					} catch (Throwable t) {
						throw new InvocationTargetException(t);
					}
				}

				@Override
				public Object getTimer() {
					return null;
				}

				@Override
				public Object getTarget() {
					return (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
				}

				@Override
				public Object[] getParameters() {
					return invocation.getArguments();
				}

				@Override
				public Method getMethod() {
					return invocation.getMethod();
				}

				@Override
				public Map<String, Object> getContextData() {
					Set<Annotation> annotationSet = new HashSet<>();
					annotationSet.add(txAnnotation);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put(ArcInvocationContext.KEY_INTERCEPTOR_BINDINGS, annotationSet);
					return map;
				}

				@Override
				public Constructor<?> getConstructor() {
					return null;
				}
			};
		}

	}
}
