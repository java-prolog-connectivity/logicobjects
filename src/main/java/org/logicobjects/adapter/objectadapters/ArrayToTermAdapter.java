package org.logicobjects.adapter.objectadapters;

import java.util.Arrays;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.logicobjects.term.Term;
import org.logicobjects.util.LogicUtil;

public class ArrayToTermAdapter extends ObjectToTermAdapter<Object[]> {


	@Override
	public Term adapt(Object[] objects, AdaptationContext adaptingContext) {
		return LogicUtil.termsToList(new ObjectToTermAdapter().adaptObjects(Arrays.asList(objects), adaptingContext));
	}
	
}
