package org.logicobjects.adapter.objectadapters;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.ObjectToTermException;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;


public class AnyCollectionToTermAdapter extends ObjectToTermAdapter<Object> {

	@Override
	public Term adapt(Object object, AdaptationContext adaptingContext) {
		if(object instanceof Map) {
			return new MapToTermAdapter().adapt((Map)object, adaptingContext);
		}  else if(object instanceof Object[]) {
			return new ArrayToTermAdapter().adapt((Object[])object, adaptingContext);
		} else if(object instanceof Iterable) {
			return new IterableToTermAdapter().adapt((Iterable) object, adaptingContext);
		} else if(object instanceof Iterator) {
			return new IteratorToTermAdapter().adapt((Iterator) object, adaptingContext);
		} else if(object instanceof Enumeration) {
			return new EnumerationToTermAdapter().adapt((Enumeration) object, adaptingContext);
		} 
		throw new ObjectToTermException(object);
	}

}



