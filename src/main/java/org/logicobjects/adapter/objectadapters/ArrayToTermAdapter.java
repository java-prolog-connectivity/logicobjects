package org.logicobjects.adapter.objectadapters;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.logicobjects.util.LogicUtil;

public class ArrayToTermAdapter extends ObjectToTermAdapter<Object[]> {


	@Override
	public Term adapt(Object[] objects, AdaptationContext adaptingContext) {
		return LogicUtil.termArrayToList(objectsAsTerms(objects, adaptingContext));
	}
	
	public static Term[] objectsAsTerms(Object[] objects) {
		return objectsAsTerms(objects, null);
	}
	
	public static Term[] objectsAsTerms(Object[] objects, AdaptationContext adaptingContext) {
		Term[] terms = new Term[objects.length];
		for(int i=0; i<objects.length; i++) {
			terms[i] = new ObjectToTermAdapter().adapt(objects[i], adaptingContext);
		}
		return terms;
	}
}
