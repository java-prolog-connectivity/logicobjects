package org.logicobjects.adapter.objectadapters;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptingContext;
import org.logicobjects.util.LogicUtil;

public class ArrayToTermAdapter extends ObjectToTermAdapter<Object[]> {


	@Override
	public Term adapt(Object[] objects, AdaptingContext adaptingContext) {
		return LogicUtil.termArrayToList(arrayAsTerms(objects, adaptingContext));
	}
	
	public static Term[] arrayAsTerms(Object[] objects) {
		return arrayAsTerms(objects, null);
	}
	
	public static Term[] arrayAsTerms(Object[] objects, AdaptingContext adaptingContext) {
		Term[] terms = new Term[objects.length];
		for(int i=0; i<objects.length; i++) {
			terms[i] = new ObjectToTermAdapter().adapt(objects[i], adaptingContext);
		}
		return terms;
	}
}
