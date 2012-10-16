package org.logicobjects.adapter;

import java.lang.reflect.Type;
import java.util.Collection;

import org.reflectiveutils.GenericsUtil;

/**
 * An abstract adapter
 * 
 */
public abstract class Adapter<From, To> {
	public abstract To adapt(From source);
	
	public void adapt(Collection<From> source, Collection<To> destiny) {
		for(From o : source) {
			destiny.add(adapt(o));
		}
	}
	
	
	//convenient methods for finding out the generic types at runtime
	
	/**
	 * Answers an array of the type parameter of this class given a descendant class
	 * @param descendantAdapter
	 * @return
	 */
	public static <A extends Adapter> Type[] types(Class <A> descendantAdapter) {
		return new GenericsUtil().findAncestorTypeParameters(Adapter.class, descendantAdapter);
	}
	
	public static <A extends Adapter> Type fromType(Class <A> descendantAdapter) {
		return new GenericsUtil().findAncestorTypeParameters(Adapter.class, descendantAdapter)[0];
	}
	
	public static <A extends Adapter> Type toType(Class <A> descendantAdapter) {
		return new GenericsUtil().findAncestorTypeParameters(Adapter.class, descendantAdapter)[1];
	}
	

}
