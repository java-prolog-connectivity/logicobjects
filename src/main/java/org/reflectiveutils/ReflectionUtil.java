package org.reflectiveutils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.code.guava.beans.Properties;
import com.google.code.guava.beans.Property;

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
	
	public static Field getField(Object target, String propertyName) {
		return getField(target.getClass(), propertyName);
	}
	
	public static Field getField(Class clazz, String propertyName) {
		Field field = null;
		try {
			field = clazz.getField(propertyName); //this fails if the field is not public
		} catch (NoSuchFieldException e) {
			try {
				Property property = Properties.getPropertyByName(clazz, propertyName); //this fails if there is not a getter
				field = property.getField();
			} catch(IllegalStateException e2) { //Unknown property
				throw new RuntimeException(e2); 
			}
		}
		return field;
	}
	
	public static Object getFieldValue(Object target, String propertyName) {
		Object value = null;
		Field field = getField(target, propertyName);
		if(field != null) {
			field.setAccessible(true); //otherwise we will get an illegal access exception
			try {
				value = field.get(target);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return value;
	}
	
	public static void setFieldValue(Object target, String propertyName, Object value) {
		Field field = getField(target, propertyName);
		try {
			if(field != null) {
				field.setAccessible(true); //otherwise we will get an illegal access exception
				field.set(target, value);
			}
		} catch (Exception e) {
			Property property = Properties.getPropertyByName(target, propertyName);
			try {
				property.setValueWithSetter(target, value); //try to use the setter if any
			} catch(NullPointerException e2) { //setter no defined
				property.setFieldValue(target, value);
			}
		} 
		
	}
}
