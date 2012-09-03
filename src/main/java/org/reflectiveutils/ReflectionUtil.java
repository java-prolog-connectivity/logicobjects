package org.reflectiveutils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.logicobjects.annotation.LDelegationObject;
import org.reflectiveutils.visitor.FindFirstTypeVisitor;
import org.reflectiveutils.visitor.TypeVisitor.InterfaceMode;

public class ReflectionUtil {

	/**
	 * 
	 * @param clazz
	 * @param interfaze
	 * @return a boolean indicating if a class adds an interface to its class hierarchy
	 */
	public static boolean includesInterfaceInHierarchy(Class clazz, Class interfaze) {
		//Object.class will never answer true to the first condition, so the call to getSuperclass() in the second is safe
		return (interfaze.isAssignableFrom(clazz) && !interfaze.isAssignableFrom(clazz.getSuperclass()));
	}
	
	/**
	 * 
	 * @param clazz
	 * @return an array with all the interfaces included by {@code clazz}
	 */
	public static Class[] includedInterfaces(Class clazz) {
		List<Class> includedInterfaces = new ArrayList<Class>();
		for(Class interfaze : clazz.getInterfaces()) {
			if(includesInterfaceInHierarchy(clazz, interfaze))
				includedInterfaces.add(interfaze);
		}
		return includedInterfaces.toArray(new Class[] {});
	}
	

	/**
	 * @param ancestor
	 * @param descendant
	 * @return All the classes between {@code ancestor} and {@code descendant} ({@code ancestor} and {@code descendant} are also included)
	 * @throws NotAncestorException in case {@code ancestor} is not an ancestor of {@code descendant}
	 */
	public static Class[] getClassesInHierarchy(Class ancestor, Class descendant) {
		List<Class> hierarchy = new ArrayList<Class>();
		
		Class currentDescendant = descendant;
		while(true) {
			hierarchy.add(0, currentDescendant);
			if(currentDescendant.equals(ancestor)) { //done, we reach the ancestor in the hierarchy
				return hierarchy.toArray(new Class[] {});
			} else if(currentDescendant.equals(Object.class)) {
					throw new NotAncestorException(ancestor, descendant);
			} else {
				currentDescendant = currentDescendant.getSuperclass();
			}
		}
	}
	
	/*
	public static Field getField(Object target, String propertyName) {
		return getFieldInHierarchy(target.getClass(), propertyName);
	}
	
	private static Field getFieldInHierarchy(Class clazz, String propertyName) {
		Field field = null;
		try {
			field = clazz.getField(propertyName); //does not work for non public fields
		} catch(NoSuchFieldException e1) { //Unknown property
			try {
				field = getFieldInHierarchyAux(clazz, propertyName);
			} catch(Exception e2) {
				throw new RuntimeException(e1);
			}
		}
		return field;
	}
	
	private static Field getFieldInHierarchyAux(Class clazz, String propertyName) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(propertyName); //all the fields declared by the current class
		} catch(NoSuchFieldException e2) { //Unknown property
			if(clazz.equals(Object.class))
				throw new RuntimeException(e2);
			else
				field = getFieldInHierarchyAux(clazz.getSuperclass(), propertyName);
		}
		return field;
	}
*/
	
	public static Object getPropertyWithGetter(Object target, String propertyName) {
		try {
			return PropertyUtils.getProperty(target, propertyName);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void setPropertyWithSetter(Object target, String propertyName, Object value) {
		try {
			PropertyUtils.setProperty(target, propertyName, value);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Field getAccessibleField(String fieldName, Class definingClass) {
		Field field;
		try {
			field = definingClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
		field.setAccessible(true); //this is necessary to make accessible non-public fields, otherwise an illegal access exception will be thrown
		return field;
	}
	
	public static Object getFieldWithReflection(Object target, String propertyName, Class definingClass) {
		try {
			Field field = getAccessibleField(propertyName, definingClass);
			return field.get(target);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		} 
	}
	
	public static void setFieldWithReflection(Object target, String propertyName, Class definingClass, Object value) {
		try {
			Field field = getAccessibleField(propertyName, definingClass);
			field.set(target, value);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		} 
	}
	
	public static Object getProperty(Object target, String propertyName, Class definingClass) {
		Object value = null;
		try {
			value = getPropertyWithGetter(target, propertyName); //try with an accessor if possible
		} catch(Exception e) { //getter no defined
			if(definingClass == null)
				throw new RuntimeException(e);
			try {
				value = getFieldWithReflection(target, propertyName, definingClass); //try obtaining directly the field
			} catch(Exception e2) {
				throw new RuntimeException(e2);
			}
		}
		return value;
	}
	
	public static void setProperty(Object target, String propertyName, Object value, Class definingClass) {
		try {
			setPropertyWithSetter(target, propertyName, value);
		} catch (Exception e) { //setter no defined
			if(definingClass == null)
				throw new RuntimeException(e);
			try {
				setFieldWithReflection(target, propertyName, definingClass, value); //try obtaining directly the field
			} catch(Exception e2) { 
				throw new RuntimeException(e2);
			}
		} 
	}


	public static Method getter(Class clazz, String propertyName) {
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(clazz, propertyName);
		if(propertyDescriptor != null)
			return propertyDescriptor.getReadMethod();
		else
			return null;
	}
	
	public static Method setter(Class clazz, String propertyName) {
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(clazz, propertyName);
		if(propertyDescriptor != null)
			return propertyDescriptor.getWriteMethod();
		else
			return null;
	}
	
	
	private static PropertyDescriptor getPropertyDescriptor(Class clazz, String propertyName) {
		for(PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(clazz)) {
			if(propertyDescriptor.getName().equals(propertyName))
				return propertyDescriptor;
		}
		return null;
	}
	
	public Type getPropertyType(Object target, String propertyName) {
		Type type = null;
		try {
			PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(target, propertyName);
			Method getter = propertyDescriptor.getReadMethod();
			if(getter != null)
				type = getter.getGenericReturnType();
			else {
				Method setter = propertyDescriptor.getWriteMethod();
				if(setter != null)
					type = setter.getGenericParameterTypes()[0];
				else
					throw new RuntimeException("Unknown property type: " + propertyName + " in object: " + target);
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		return type;
	}
	
	public static <A extends Annotation> A getAnnotationParameter(Method method, int position, Class<A> annotationClass) {
		for(Annotation anAnnotation : method.getParameterAnnotations()[position]) {
			if(anAnnotation.annotationType().equals(annotationClass))
				return (A) anAnnotation;
		}
		return null;
	}

	
	/*
	public static class A {
		protected String s = "x";
		
		public String getS() {
			return s;
		}
	}
	
	public static class B extends A {
		protected String s = "y";
		
		public String getS() {
			return s;
		}
	}
	
	public static void main(String[] args) {
		A a = new B();
		System.out.println(a.s);
		System.out.println(((B)a).s);
		Method getter = getter(A.class, "s");
		try {
			System.out.println(getter.invoke(a));
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	*/
	
	
	public static Class findFirstNonSyntheticClass(Class candidateClass) {
		FindFirstTypeVisitor finderVisitor = new FindFirstTypeVisitor(InterfaceMode.INCLUDE_INTERFACES) {
			@Override
			public boolean match(Class clazz) {
				return !clazz.isSynthetic();
			}
		};
		finderVisitor.visit(candidateClass);
		return finderVisitor.getFoundType();
	}
}
