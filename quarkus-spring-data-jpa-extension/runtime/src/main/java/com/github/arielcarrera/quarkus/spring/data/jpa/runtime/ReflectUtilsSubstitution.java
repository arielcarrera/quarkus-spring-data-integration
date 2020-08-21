package com.github.arielcarrera.quarkus.spring.data.jpa.runtime;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassInfo;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;

import com.oracle.svm.core.annotate.KeepOriginal;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(ReflectUtils.class)
@Substitute
final class ReflectUtilsSubstitution {

	@Substitute
	private ReflectUtilsSubstitution() {
	}

	@Substitute
	private static final Map primitives = new HashMap(8);
	
	static {
		primitives.put("byte", Byte.TYPE);
		primitives.put("char", Character.TYPE);
		primitives.put("double", Double.TYPE);
		primitives.put("float", Float.TYPE);
		primitives.put("int", Integer.TYPE);
		primitives.put("long", Long.TYPE);
		primitives.put("short", Short.TYPE);
		primitives.put("boolean", Boolean.TYPE);
	}
	
	@Substitute
	private static final Map transforms = new HashMap(8);

	static {
		transforms.put("byte", "B");
		transforms.put("char", "C");
		transforms.put("double", "D");
		transforms.put("float", "F");
		transforms.put("int", "I");
		transforms.put("long", "J");
		transforms.put("short", "S");
		transforms.put("boolean", "Z");
	}
	
	@Substitute
	private static final String[] CGLIB_PACKAGES = {
			"java.lang",
	};

	@Substitute
	private static final ClassLoader defaultLoader = ReflectUtils.class.getClassLoader();

	@Substitute
	private static final List<Method> OBJECT_METHODS = new ArrayList<Method>();
	
	static {
		Method[] methods = Object.class.getDeclaredMethods();
		for (Method method : methods) {
			if ("finalize".equals(method.getName())
					|| (method.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) > 0) {
				continue;
			}
			OBJECT_METHODS.add(method);
		}
	}
	
	// SPRING PATCH BEGIN
	@Substitute
	private static final Method privateLookupInMethod;

	@Substitute
	private static final Method lookupDefineClassMethod;

	@Substitute
	private static final Method classLoaderDefineClassMethod;

	@Substitute
	private static final ProtectionDomain PROTECTION_DOMAIN;

	@Substitute
	private static final Throwable THROWABLE;

	static {
		Method privateLookupIn;
		Method lookupDefineClass;
		Method classLoaderDefineClass;
		ProtectionDomain protectionDomain;
		Throwable throwable = null;
		try {
			privateLookupIn = (Method) MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
			lookupDefineClass = (Method) MethodHandles.Lookup.class.getMethod("defineClass", byte[].class);
			classLoaderDefineClass = (Method) ClassLoader.class.getDeclaredMethod("defineClass",
							String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
			protectionDomain = getProtectionDomain(ReflectUtils.class);
		} catch (Throwable t) {
			privateLookupIn = null;
			lookupDefineClass = null;
			classLoaderDefineClass = null;
			protectionDomain = null;
			throwable = t;
		}
		privateLookupInMethod = privateLookupIn;
		lookupDefineClassMethod = lookupDefineClass;
		classLoaderDefineClassMethod = classLoaderDefineClass;
		PROTECTION_DOMAIN = protectionDomain;
		THROWABLE = throwable;
	}

	@KeepOriginal
	public static List addAllMethods(final Class type, final List list) {
		return null;
	}
	
	@KeepOriginal
	public static Class defineClass(String className, byte[] b, ClassLoader loader) throws Exception {
		return null;
	}

	@KeepOriginal
	public static Class defineClass(String className, byte[] b, ClassLoader loader,
			ProtectionDomain protectionDomain) throws Exception {
		return null;
	}
	
	@KeepOriginal
	@SuppressWarnings("deprecation")  // on JDK 9
	public static Class defineClass(String className, byte[] b, ClassLoader loader,
			ProtectionDomain protectionDomain, Class<?> contextClass) throws Exception {

//		Class c = null;
//
//		// Preferred option: JDK 9+ Lookup.defineClass API if ClassLoader matches
//		if (contextClass != null && contextClass.getClassLoader() == loader &&
//				privateLookupInMethod != null && lookupDefineClassMethod != null) {
//			try {
//				MethodHandles.Lookup lookup = (MethodHandles.Lookup)
//						privateLookupInMethod.invoke(null, contextClass, MethodHandles.lookup());
//				c = (Class) lookupDefineClassMethod.invoke(lookup, b);
//			}
//			catch (InvocationTargetException ex) {
//				Throwable target = ex.getTargetException();
//				if (target.getClass() != LinkageError.class && target.getClass() != IllegalArgumentException.class) {
//					throw new CodeGenerationException(target);
//				}
//				// in case of plain LinkageError (class already defined)
//				// or IllegalArgumentException (class in different package):
//				// fall through to traditional ClassLoader.defineClass below
//			}
//			catch (Throwable ex) {
//				throw new CodeGenerationException(ex);
//			}
//		}
//
//		// Classic option: protected ClassLoader.defineClass method
//		if (c == null && classLoaderDefineClassMethod != null) {
//			if (protectionDomain == null) {
//				protectionDomain = PROTECTION_DOMAIN;
//			}
//			Object[] args = new Object[]{className, b, 0, b.length, protectionDomain};
//			try {
//				if (!classLoaderDefineClassMethod.isAccessible()) {
//					classLoaderDefineClassMethod.setAccessible(true);
//				}
//				c = (Class) classLoaderDefineClassMethod.invoke(loader, args);
//			}
//			catch (InvocationTargetException ex) {
//				throw new CodeGenerationException(ex.getTargetException());
//			}
//			catch (Throwable ex) {
//				// Fall through if setAccessible fails with InaccessibleObjectException on JDK 9+
//				// (on the module path and/or with a JVM bootstrapped with --illegal-access=deny)
//				if (!ex.getClass().getName().endsWith("InaccessibleObjectException")) {
//					throw new CodeGenerationException(ex);
//				}
//			}
//		}
//
//		// Fallback option: JDK 9+ Lookup.defineClass API even if ClassLoader does not match
//		if (c == null && contextClass != null && contextClass.getClassLoader() != loader &&
//				privateLookupInMethod != null && lookupDefineClassMethod != null) {
//			try {
//				MethodHandles.Lookup lookup = (MethodHandles.Lookup)
//						privateLookupInMethod.invoke(null, contextClass, MethodHandles.lookup());
//				c = (Class) lookupDefineClassMethod.invoke(lookup, b);
//			}
//			catch (InvocationTargetException ex) {
//				throw new CodeGenerationException(ex.getTargetException());
//			}
//			catch (Throwable ex) {
//				throw new CodeGenerationException(ex);
//			}
//		}
//
//		// No defineClass variant available at all?
//		if (c == null) {
//			throw new CodeGenerationException(THROWABLE);
//		}
//
//		// Force static initializers to run.
//		Class.forName(className, true, loader);
//		return c;
		return null;
	}
	
	@KeepOriginal
	public static Method findNewInstance(Class iface) {
		return null;
	}
	
	@KeepOriginal
	public static int findPackageProtected(Class[] classes) {
		return 0;
	}
	
	@KeepOriginal
	public static ClassInfo getClassInfo(final Class clazz) {
		return null;
	}
	
	@KeepOriginal
	public static Constructor getConstructor(Class type, Class[] parameterTypes) {
		return null;
	}
	
	@KeepOriginal
	public static Type[] getExceptionTypes(Member member) {
		return null;
	}
	
	@KeepOriginal
	public static MethodInfo getMethodInfo(Member member) {
		return null;
	}
	
	@KeepOriginal
	public static MethodInfo getMethodInfo(final Member member, final int modifiers) {
		return null;
	}
	
	@KeepOriginal
	public static String[] getNames(Class[] classes) {
		return null;
	}
	
//	@Substitute
	@KeepOriginal
	public static ProtectionDomain getProtectionDomain(final Class source) {
		return null;
	}

	@KeepOriginal
	public static Signature getSignature(Member member) {
		return null;
	}

	@KeepOriginal
	public static Object newInstance(Class type) {
		return null;
	}

	@KeepOriginal
	public static Object newInstance(Class type, Class[] parameterTypes, Object[] args) {
		return null;
	}

    @KeepOriginal
	public static Object newInstance(final Constructor cstruct, final Object[] args) {
    	return null;
	}
	
    @KeepOriginal
	public static Method findInterfaceMethod(Class iface) {
		return null;
	}
	
	@KeepOriginal
	private static Class getClass(String className, ClassLoader loader) throws ClassNotFoundException {
		return null;
	}

	@KeepOriginal
	private static Class getClass(String className, ClassLoader loader, String[] packages) throws ClassNotFoundException {
		return null;
	}

}

@TargetClass(className = "org.springframework.beans.BeanUtils$KotlinDelegate")
final class BeanUtils$KotlinDelegateSubstitution {
	
	@Substitute
	public static <T> Constructor<T> findPrimaryConstructor(Class<T> clazz){
		return null;
	}
	
	@Substitute
	public static <T> T instantiateClass(Constructor<T> ctor, Object... args)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {
		return null;
	}
}

