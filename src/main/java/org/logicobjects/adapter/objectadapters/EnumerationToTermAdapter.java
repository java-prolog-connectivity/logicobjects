package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Enumeration;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;

public class EnumerationToTermAdapter extends ObjectToTermAdapter<Enumeration> {

	@Override
	public Term adapt(Enumeration objects, Field field) {
		return new ArrayToTermAdapter().adapt(Collections.list(objects).toArray(), field);
	}
}
