package com.github.arielcarrera.quarkus.spring.data.jpa.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.KeepOriginal;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

public class SerializableTypeWrapperSubstitution {
	
	
}
//@TargetClass(className = "org.springframework.core.SerializableTypeWrapper")
//final class SerializableTypeWrapperSubstitution {
//
//	@Alias
//	@KeepOriginal
//	public static <T extends java.lang.reflect.Type> T unwrap(T type) {
//		return null;
//	}
//	
//	@Alias
//	@KeepOriginal
//	public static java.lang.reflect.Type forTypeProvider(SerializableTypeWrapper$TypeProviderSubstitution provider) {
//		return null;
//	}
//}
//
//
//@TargetClass(className = "org.springframework.core.SerializableTypeWrapper$TypeProvider")
//interface SerializableTypeWrapper$TypeProviderSubstitution {
//
//	@Alias
//	java.lang.reflect.Type getType();
//}

@TargetClass(className = "org.springframework.core.SerializableTypeWrapper$TypeProxyInvocationHandler")
final class SerializableTypeWrapper$TypeProxyInvocationHandlerSubstitution {
	
//	@Alias
//	private SerializableTypeWrapper$TypeProviderSubstitution provider;
	
	@Substitute
	@Nullable
	public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
//		if (method.getName().equals("equals") && args != null) {
//			Object other = args[0];
//			// Unwrap proxies for speed
//			if (other instanceof java.lang.reflect.Type) {
//				other = SerializableTypeWrapperSubstitution.unwrap((java.lang.reflect.Type) other);
//			}
//			return ObjectUtils.nullSafeEquals(this.provider.getType(), other);
//		}
//		else if (method.getName().equals("hashCode")) {
//			return ObjectUtils.nullSafeHashCode(this.provider.getType());
//		}
//		else if (method.getName().equals("getTypeProvider")) {
//			return this.provider;
//		}
//	
//		if (java.lang.reflect.Type.class == method.getReturnType() && args == null) {
//			return SerializableTypeWrapperSubstitution.forTypeProvider(new SerializableTypeWrapper$MethodInvokeTypeProviderSubstitution(this.provider, method, -1));
//		}
//		else if (java.lang.reflect.Type[].class == method.getReturnType() && args == null) {
//			java.lang.reflect.Type[] result = new java.lang.reflect.Type[((java.lang.reflect.Type[]) method.invoke(this.provider.getType())).length];
//			for (int i = 0; i < result.length; i++) {
//				result[i] = SerializableTypeWrapperSubstitution.forTypeProvider(new SerializableTypeWrapper$MethodInvokeTypeProviderSubstitution(this.provider, method, i));
//			}
//			return result;
//		}
//	
//		try {
//			return method.invoke(this.provider.getType(), args);
//		}
//		catch (InvocationTargetException ex) {
//			throw ex.getTargetException();
//		}
		return null;
	}
}

//@TargetClass(className = "org.springframework.core.SerializableTypeWrapper$MethodInvokeTypeProvider")
//final class SerializableTypeWrapper$MethodInvokeTypeProviderSubstitution implements SerializableTypeWrapper$TypeProviderSubstitution {
//	
//	@Alias
//	public SerializableTypeWrapper$MethodInvokeTypeProviderSubstitution(SerializableTypeWrapper$TypeProviderSubstitution provider, Method method, int index) {
//		
//	}
//
//	@Alias
//	@KeepOriginal
//	@Override
//	public java.lang.reflect.Type getType() {
//		return null;
//	}
//}
