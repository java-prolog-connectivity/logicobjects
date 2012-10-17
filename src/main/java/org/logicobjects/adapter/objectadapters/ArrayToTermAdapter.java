package org.logicobjects.adapter.objectadapters;

import java.util.Arrays;

import org.jpc.LogicUtil;
import org.jpc.term.Term;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;

public class ArrayToTermAdapter extends ObjectToTermAdapter<Object[]> {


	@Override
	public Term adapt(Object[] objects, AdaptationContext adaptingContext) {
		return LogicUtil.termsToList(new ObjectToTermAdapter().adaptObjects(Arrays.asList(objects), adaptingContext));
	}
	
}
