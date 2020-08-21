package com.github.arielcarrera.quarkus.spring.data.jpa.runtime;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.lang.Nullable;

import com.oracle.svm.core.annotate.KeepOriginal;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@Substitute
@TargetClass(className = "org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor")
final class DefaultMethodInvokingMethodInterceptorSubstitution implements MethodInterceptor {

	@Substitute
    public DefaultMethodInvokingMethodInterceptorSubstitution() {
		
    }
	
	@KeepOriginal
	public static boolean hasDefaultMethods(Class<?> interfaceClass) {
		return false;
	}
	
	@Substitute
	@Nullable
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		Method method = invocation.getMethod();

		if (!method.isDefault()) {
			return invocation.proceed();
		}

		Object[] arguments = invocation.getArguments();
		Object proxy = ((ProxyMethodInvocation) invocation).getProxy();
		
		return method.invoke(proxy, arguments);
	}

}

@TargetClass(className = "org.hibernate.annotations.common.annotationfactory.AnnotationProxy")
final class AnnotationProxySubstitution {
	
	@Substitute
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}
}

@TargetClass(className = "org.hibernate.engine.jdbc.SerializableClobProxy")
final class SerializableClobProxySubstitution {
	
	@Substitute
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}
}

@TargetClass(className = "org.hibernate.engine.jdbc.SerializableBlobProxy")
final class SerializableBlobProxySubstitution {
	
	@Substitute
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}
}

@TargetClass(className = "java.lang.invoke.MethodHandles$Lookup")
final class MethodHandles$LookupSubstitution {
	
	@Substitute
	private ProtectionDomain protectionDomain(Class<?> clazz) {
		return null;
	}
}

@TargetClass(className = "org.jboss.resteasy.core.ContextParameterInjector$GenericDelegatingProxy")
final class ContextParameterInjector$GenericDelegatingProxySubstitution {
	
	@Substitute
	public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
		return null;
	}
}
