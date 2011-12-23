package org.reflectiveutils;

import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

	public static boolean includesInterfaceInHierarchy(Class clazz, Class interfaze) {
		//Object.class will never answer true to the first condition, so the call to getSuperclass() in the second is safe
		return (interfaze.isAssignableFrom(clazz) && !interfaze.isAssignableFrom(clazz.getSuperclass()));
	}
	
	
	public static Class[] includedInterfaces(Class clazz) {
		List<Class> includedInterfaces = new ArrayList<Class>();
		for(Class interfaze : clazz.getInterfaces()) {
			if(includesInterfaceInHierarchy(clazz, interfaze))
				includedInterfaces.add(interfaze);
		}
		return includedInterfaces.toArray(new Class[] {});
	}
}
