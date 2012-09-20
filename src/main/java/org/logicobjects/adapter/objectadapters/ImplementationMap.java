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

import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.SingleTypeWrapper;

/*
 * Default implementation classes for certain interfaces
 * These default implementation classes will be used when interfaces or abstract classes should be instantiated
 */
public class ImplementationMap {
	private Map<Class, Class> dict;
	private Map<Class, Class> cache;
	
	
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
		cache = new HashMap<>();
		dict = new LinkedHashMap<>(); //preserving insertion order
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
	
	
	public Object instantiateObject(final Type type) {
		SingleTypeWrapper typeWrapper = (SingleTypeWrapper) AbstractTypeWrapper.wrap(type);
		Class targetClass = typeWrapper.asClass();
		Class instantiationClass = null;
		Object object = null;
		
		if(cache.containsKey(targetClass))
			instantiationClass = cache.get(targetClass);
		else {
			if(!ReflectionUtil.isAbstract(targetClass))
				instantiationClass = targetClass;
			
			if(instantiationClass == null) {
				instantiationClass = implementationFor(targetClass);
			}
			
			cache.put(targetClass, instantiationClass); //will do this even if the instantiation class is null, to avoid searching in vain in a future request
			
			if(instantiationClass == null) {
				throw new RuntimeException(new InstantiationException() {
					@Override public String getMessage() {
						return "Impossible to instantiate type " + type;
					}
				});
			}
		}

		if(instantiationClass != null) {
			try {
				object = instantiationClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return object;
	}
	
	
	public static void main(String[] args) {
		ImplementationMap d = new ImplementationMap();
	}
}
