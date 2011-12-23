package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Field;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;

public class IterableToTermAdapter extends ObjectToTermAdapter<Iterable> {

	@Override
	public Term adapt(Iterable objects, Field field) {
		return new IteratorToTermAdapter().adapt(objects.iterator(), field);
	}
}
