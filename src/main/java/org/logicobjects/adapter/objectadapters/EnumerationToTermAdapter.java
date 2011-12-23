package org.logicobjects.adapter.objectadapters;

import java.util.Collections;
import java.util.Enumeration;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptingContext;

public class EnumerationToTermAdapter extends ObjectToTermAdapter<Enumeration> {

	@Override
	public Term adapt(Enumeration objects, AdaptingContext adaptingContext) {
		return new ArrayToTermAdapter().adapt(Collections.list(objects).toArray(), adaptingContext);
	}
}
