package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import jpl.Term;
import org.logicobjects.adapter.ObjectToTermException;

import org.logicobjects.adapter.ObjectToTermAdapter;


public class AnyCollectionToTermAdapter extends ObjectToTermAdapter<Object> {

	@Override
	public Term adapt(Object object, Field field) {
		if(object instanceof Map) {
			return new MapToTermAdapter().adapt((Map)object);
		}  else if(object instanceof Object[]) {
			return new ArrayToTermAdapter().adapt((Object[])object, field);
		} else if(object instanceof Iterable) {
			return new IterableToTermAdapter().adapt((Iterable) object, field);
		} else if(object instanceof Iterator) {
			return new IteratorToTermAdapter().adapt((Iterator) object, field);
		} else if(object instanceof Enumeration) {
			return new EnumerationToTermAdapter().adapt((Enumeration) object, field);
		} 
		throw new ObjectToTermException(object); 
	}

}



