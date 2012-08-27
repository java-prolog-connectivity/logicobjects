package org.logicobjects.adapter.objectadapters;

import java.util.Collections;
import java.util.Iterator;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;

import com.google.common.collect.Iterators;

public class IteratorToTermAdapter extends ObjectToTermAdapter<Iterator> {

	@Override
	public Term adapt(Iterator objects, AdaptationContext adaptingContext) {
		return new ArrayToTermAdapter().adapt(Collections.list(Iterators.asEnumeration(objects)).toArray(), adaptingContext);
	}
}
