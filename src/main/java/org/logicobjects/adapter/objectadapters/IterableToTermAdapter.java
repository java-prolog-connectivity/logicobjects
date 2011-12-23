package org.logicobjects.adapter.objectadapters;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptingContext;

public class IterableToTermAdapter extends ObjectToTermAdapter<Iterable> {

	@Override
	public Term adapt(Iterable objects, AdaptingContext adaptingContext) {
		return new IteratorToTermAdapter().adapt(objects.iterator(), adaptingContext);
	}
}
