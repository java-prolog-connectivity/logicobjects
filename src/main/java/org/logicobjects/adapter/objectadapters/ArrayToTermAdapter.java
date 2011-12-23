package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Field;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.util.LogicUtil;

public class ArrayToTermAdapter extends ObjectToTermAdapter<Object[]> {


	@Override
	public Term adapt(Object[] objects, Field field) {
		return LogicUtil.termArrayToList(arrayAsTerms(objects, field));
	}
	
	public static Term[] arrayAsTerms(Object[] objects) {
		return arrayAsTerms(objects, null);
	}
	
	public static Term[] arrayAsTerms(Object[] objects, Field field) {
		Term[] terms = new Term[objects.length];
		for(int i=0; i<objects.length; i++) {
			terms[i] = new ObjectToTermAdapter().adapt(objects[i], field);
		}
		return terms;
	}
}
