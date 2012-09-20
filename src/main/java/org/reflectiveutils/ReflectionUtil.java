package org.reflectiveutils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.logicobjects.lib.examples.metro.Line;
import org.reflectiveutils.visitor.FindFirstTypeVisitor;
import org.reflectiveutils.visitor.TypeVisitor;
import org.reflectiveutils.visitor.TypeVisitor.InterfaceMode;

public class ReflectionUtil {

	
	/**
	 * Answers if the two methods can handle the same message
	 * This is true if they have the same name and same number and type of parameters
	 * (the return type is not relevant)
	 * @param m1
	 * @param m2
	 * @return
	 */
	public static boolean handleSameMessage(Method m1, Method m2) {
		/*
		if(!m1.getReturnType().equals(m2.getReturnType()))
			return false;
		*/
		if(!m1.getName().equals(m2.getName()))
			return false;
		
		Class[] params1 = m1.getParameterTypes();
		Class[] params2 = m2.getParameterTypes();
		if(params1.length != params2.length)
			return false;
		
		for(int i = 0; i<params1.length; i++) {
			if(!params1[i].equals(params2[i]))
				return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param method
	 * @param methods
	 * @return true if the first parameter method handle the same message than one method in the second parameter list, false otherwise
	 */
	private static boolean isHandled(Method method, List<Method> methods) {
		for(Method m : methods) {
			if(handleSameMessage(method, m))
				return true;
		}
		return false;
	}
	
	public static List<Method> getAllAbstractMethods(Class clazz) {
		List<Method> publicAbstractMethods = new ArrayList<>();
		getAllPublicAbstractMethods(clazz, publicAbstractMethods);
		List<Method> nonPublicAbstractMethods = new ArrayList<>();
		getAllNonPublicAbstractMethods(clazz, nonPublicAbstractMethods);
		List<Method> allAbstractMethods = publicAbstractMethods;
		allAbstractMethods.addAll(nonPublicAbstractMethods);
		return allAbstractMethods;
	}
	
	private static void getAllPublicAbstractMethods(Class clazz, List<Method> abstractMethods) {
		if(clazz == null)
			return;
		for(Method method : clazz.getMethods()) //only answers public methods (both declared and inherited). It includes any method declared in the class interfaces (methods in interfaces must be public)
			if(isAbstract(method) && !isHandled(method, abstractMethods))
				abstractMethods.add(method);
		getAllNonPublicAbstractMethods(clazz.getSuperclass(), abstractMethods);
	}
	
	private static void getAllNonPublicAbstractMethods(Class clazz, List<Method> abstractMethods) {
		if(clazz == null)
			return;
		for(Method method : clazz.getDeclaredMethods()) //answers ALL the methods declared by the class. Methods in the class interfaces are ignored.
			if(!isPublic(method) && isAbstract(method) && !isHandled(method, abstractMethods))
				abstractMethods.add(method);
		getAllNonPublicAbstractMethods(clazz.getSuperclass(), abstractMethods);
	}
	
	
	public static void main(String[] args) {
		List<Method> methods = null;
		methods = Arrays.asList(Line.class.getDeclaredMethods());
		System.out.println(methods);
		methods = Arrays.asList(Line.class.getMethods());
		System.out.println(methods);
		methods = getAllAbstractMethods(Line.class);
		System.out.println(methods);
	}
	
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
	
	/**
	 * Returns a map with all the visible fields in a class:
	 * - all the fields declared in the class, 
	 * - the public and protected fields of the ancestor classes, and 
	 * - the "package" fields of superclasses located in the same package
	 * @param clazz
	 * @return
	 */
	public static Map<String, Field> visibleFields(Class clazz) {
		Map<String, Field> visibleFields = new HashMap<String, Field>();
		Field[] declaredFields = clazz.getDeclaredFields();
		for(Field declaredField : declaredFields) {
			visibleFields.put(declaredField.getName(), declaredField);
		}
		visibleSuperFields(clazz, visibleFields);
		return visibleFields;
	}
	
	public static void visibleSuperFields(final Class clazz, final Map<String, Field> visibleFields) {
		Class superClass = clazz.getSuperclass();
		if(superClass != null) {
			TypeVisitor typeVisitor = new TypeVisitor(InterfaceMode.EXCLUDE_INTERFACES) {
				@Override public boolean doVisit(Class clazzInHierarchy) {
					Field[] declaredFields = clazzInHierarchy.getDeclaredFields();
					for(Field declaredField : declaredFields) {
						if(!visibleFields.containsKey(declaredField.getName())) //check if the field is already there
							if(!Modifier.isPrivate(declaredField.getModifiers())) { //exclude private fields in super classes
								if(!isPackageAccessModifier(declaredField) || clazzInHierarchy.getPackage().equals(clazz.getPackage())) //exclude 'package' fields in classes declared in different packages
									visibleFields.put(declaredField.getName(), declaredField);
							}
					}
					return true;
				}
			};
			typeVisitor.visit(superClass);
		}
	}
	
	public static boolean isAbstract(Class clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}
	
	public static boolean isPublic(Method method) {
		return Modifier.isPublic(method.getModifiers());
	}
	
	public static boolean isAbstract(Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}
	
	public static boolean isPackageAccessModifier(Field field) {
		int modifiers = field.getModifiers();
		return !Modifier.isPrivate(modifiers) && !Modifier.isProtected(modifiers) && !Modifier.isPublic(modifiers);
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

	
	
	public static class A {
		protected String s = "x";
		private String p;
		String p1;
		public String p2;
		
		
		public String getS() {
			return s;
		}
	}
	
	public static abstract class B extends A {
		protected String s = "y";
		
		public String getS() {
			p1 = "";
			return s;
		}
		
		public abstract void setS(String s);
	}
	
	public static abstract class C extends B {
		
		
		public void setS1(String s){};
	}
	
	
	
	
	
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
