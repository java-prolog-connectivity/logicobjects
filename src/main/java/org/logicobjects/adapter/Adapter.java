package org.logicobjects.adapter;

import java.lang.reflect.Type;
import java.util.Collection;

import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

/*
 * An abstract adapter with convenient methods for finding out their generic types at runtime
 * 
 */
public abstract class Adapter<From, To> {
	public abstract To adapt(From source);
	
	public Collection<To> adapt(Collection<From> source, Collection<To> destiny) {
		for(From o : source) {
			destiny.add(adapt(o));
		}
		return destiny;
	}
	
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
