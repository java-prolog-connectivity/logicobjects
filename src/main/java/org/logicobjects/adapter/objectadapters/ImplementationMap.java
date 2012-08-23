package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.SingleTypeWrapper;

/*
 * Default implementation classes for certain interfaces
 * These default implementation classes will be used when interfaces or abstract classes should be instantiated
 */
public class ImplementationMap {
	Map<Class, Class> dict;
	
	
	
	private static ImplementationMap implementationMap = new ImplementationMap();
	
	public static ImplementationMap getDefault() {
		return implementationMap;
	}
	
	public static boolean isCollectionObject(Object object) {
		return isCollectionClass(object.getClass());
	}
	
	public static boolean isCollectionClass(Class clazz) {
		return( Map.class.isAssignableFrom(clazz) || Object[].class.isAssignableFrom(clazz) || Iterable.class.isAssignableFrom(clazz) || Iterator.class.isAssignableFrom(clazz) || Enumeration.class.isAssignableFrom(clazz) );
	}
	
	private ImplementationMap() {
		dict = new LinkedHashMap<Class, Class>(); //preserving insertion order
		fillIn();
	}
	
	protected void fillIn() {
		dict.put(List.class, ArrayList.class);
		dict.put(AbstractMap.class, HashMap.class);
		dict.put(AbstractSet.class, HashSet.class);
	}
	
	private <Parent,Child extends Parent>void add(Class<Parent> parent, Class<Child> child) {
		/*// this seems to be unnecessary, even if the class is concrete, having a default implementation class descending from it could be useful
		if(!parent.isInterface() || !Modifier.isAbstract(parent.getModifiers()))
			throw new RuntimeException(parent.getName()+" is a concrete class, not an interface or abstract class");
		*/
		if( child.isInterface() || Modifier.isAbstract(child.getModifiers()) )
			throw new RuntimeException("Invalid implementation class: "+child.getName());
		dict.put(parent, child);
	}
	
	public Class implementationFor(Class anInterface) {
		for(Class clazz : dict.keySet()) {
			if(anInterface.isAssignableFrom(clazz))
				return dict.get(clazz);
		}
		return null;
	}
	
	
	public Object instantiateObject(Type type) {
		SingleTypeWrapper typeWrapper = (SingleTypeWrapper) AbstractTypeWrapper.wrap(type);
		Object object = null;
		try {
			object = typeWrapper.asClass().newInstance();
		} catch (InstantiationException e) { //it is an interface or abstract class
			try {
				Class defaultInstantiationClass = implementationFor(typeWrapper.asClass());
				if(defaultInstantiationClass != null)
					return defaultInstantiationClass.newInstance();
				else
					throw new RuntimeException(e);
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			} 
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return object;
	}
	
	
	public static void main(String[] args) {
		ImplementationMap d = new ImplementationMap();
	}
}
