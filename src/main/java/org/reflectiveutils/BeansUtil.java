package org.reflectiveutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * In the spirit of Apache bean utils. 
 * Most methods here are not there (probably it needs some cleaning ...)
 * 
 * @author scastro
 *
 */
public class BeansUtil {


	private static final String GETTER_PREFIX_NON_BOOLEAN = "get";
	private static final String GETTER_PREFIX_BOOLEAN = "is";
	private static final String SETTER_PREFIX_NON_BOOLEAN = "set";
	
	public static boolean looksLikeGetter(Method method) {
		String name = method.getName();
		return ( (name.startsWith(GETTER_PREFIX_NON_BOOLEAN) || name.startsWith(GETTER_PREFIX_BOOLEAN)) && method.getParameterTypes().length == 0 );
	}
	
	public static boolean looksLikeSetter(Method method) {
		String name = method.getName();
		return (name.startsWith(SETTER_PREFIX_NON_BOOLEAN) && method.getParameterTypes().length == 1 );
	}
	
	public static boolean looksLikeBeanMethod(Method method) {
		return looksLikeGetter(method) || looksLikeSetter(method);
	}

	public static String getterName(String propertyName, Class clazz) {
		if(clazz.equals(Boolean.class) || clazz.equals(boolean.class))
			return booleanGetterName(propertyName);
		else
			return nonBooleanGetterName(propertyName);
	}
	
	public static String nonBooleanGetterName(String propertyName) {
		return GETTER_PREFIX_NON_BOOLEAN + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
	}
	
	public static String booleanGetterName(String propertyName) {
		return GETTER_PREFIX_BOOLEAN + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
	}
	
	public static String setterName(String propertyName) {
		return SETTER_PREFIX_NON_BOOLEAN + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
	}
	
	/**
	 * Given a property name, answers the first getter in a class hierarchy
	 * @param clazz
	 * @param propertyName
	 * @return
	 */
	public static Method getterInHierarchy(Class clazz, String propertyName) {
		if(clazz == null)
			return null;
		Method method = declaredGetter(clazz, propertyName);
		if(method != null)
			return method;
		else
			return getterInHierarchy(clazz.getSuperclass(), propertyName);
	}
	
	public static Method getterInHierarchy(Class clazz, String propertyName, Class getterClass) {
		if(clazz == null)
			return null;
		Method method = declaredGetter(clazz, propertyName, getterClass);
		if(method != null)
			return method;
		else
			return getterInHierarchy(clazz.getSuperclass(), propertyName, getterClass);
	}
	
	/**
	 * Given a property name, answers the first setter in a class hierarchy
	 * Note that multiple setters could exist in a class for the same property name
	 * this method will answer the first setter found
	 * @param clazz
	 * @param propertyName
	 * @return
	 */
	public static Method setterInHierarchy(Class clazz, String propertyName) {
		if(clazz == null)
			return null;
		Method method = declaredSetter(clazz, propertyName);
		if(method != null)
			return method;
		else
			return setterInHierarchy(clazz.getSuperclass(), propertyName);
	}
	
	public static Method setterInHierarchy(Class clazz, String propertyName, Class setterClass) {
		if(clazz == null)
			return null;
		Method method = declaredSetter(clazz, propertyName, setterClass);
		if(method != null)
			return method;
		else
			return setterInHierarchy(clazz.getSuperclass(), propertyName, setterClass);
	}
	
	public static Method declaredGetter(Class clazz, String propertyName) {
		Method getter = null;
		try {
			getter = clazz.getDeclaredMethod(nonBooleanGetterName(propertyName));
		} catch (NoSuchMethodException e) {
			try {
				getter = clazz.getDeclaredMethod(booleanGetterName(propertyName));
				if(! (getter.getReturnType().equals(Boolean.class) || getter.getReturnType().equals(boolean.class)) )
					getter = null;
			} catch (NoSuchMethodException e1) {
			}
		}
		return getter;
	}
	
	public static Method declaredGetter(Class clazz, String propertyName, Class getterClass) {
		Method getter = null;
		try {
			getter = clazz.getDeclaredMethod(getterName(propertyName, getterClass));
		} catch (NoSuchMethodException e) {
		}
		return getter;
	}
	
	public static Method declaredSetter(Class clazz, String propertyName) {
		Method setter = null;
		String setterName = setterName(propertyName);
		for(Method declaredMethod : clazz.getDeclaredMethods()) {
			if(declaredMethod.getName().equals(setterName) && declaredMethod.getParameterTypes().length == 1) {
				setter = declaredMethod;
				break;
			}
		}
		return setter;
	}
	
	public static Method declaredSetter(Class clazz, String propertyName, Class setterClass) {
		Method setter = null;
		try {
			setter = clazz.getDeclaredMethod(setterName(propertyName), setterClass);
		} catch (NoSuchMethodException e) {
		}
		return setter;
	}
	
	public static Method publicGetter(Class clazz, String propertyName) {
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(clazz, propertyName);
		if(propertyDescriptor != null)
			return propertyDescriptor.getReadMethod();
		else
			return null;
	}
	
	public static Method publicSetter(Class clazz, String propertyName) {
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
	
}
