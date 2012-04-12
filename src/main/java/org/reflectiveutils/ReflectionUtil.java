package org.reflectiveutils;

import java.util.ArrayList;
import java.util.List;

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
}
