package org.logicobjects.adapter.objectadapters;

import java.util.Collections;
import java.util.Enumeration;

import org.jpc.term.Term;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;

public class EnumerationToTermAdapter extends ObjectToTermAdapter<Enumeration> {

	@Override
	public Term adapt(Enumeration objects, AdaptationContext adaptingContext) {
		return new ArrayToTermAdapter().adapt(Collections.list(objects).toArray(), adaptingContext);
	}
}
