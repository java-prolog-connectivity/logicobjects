package org.logicobjects.adapter.objectadapters;

import org.jpc.term.Term;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;

public class IterableToTermAdapter extends ObjectToTermAdapter<Iterable> {

	@Override
	public Term adapt(Iterable objects, AdaptationContext adaptingContext) {
		return new IteratorToTermAdapter().adapt(objects.iterator(), adaptingContext);
	}
}
