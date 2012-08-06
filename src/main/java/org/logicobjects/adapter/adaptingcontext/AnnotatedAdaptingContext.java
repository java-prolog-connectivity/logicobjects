package org.logicobjects.adapter.adaptingcontext;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LObjectAdapter.LObjectAdapterUtil;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.annotation.LTermAdapter.LTermAdapterUtil;

public abstract class AnnotatedAdaptingContext extends AdaptingContext {
	protected abstract LObjectAdapter getTermToObjectAdapterAnnotation();
	protected abstract LTermAdapter getObjectToTermAdapterAnnotation();
	
	public TermToObjectAdapter getTermToObjectAdapter() {
		LObjectAdapter aLObjectAdapter = getTermToObjectAdapterAnnotation();
		if(aLObjectAdapter != null)
			return LObjectAdapterUtil.newAdapter(aLObjectAdapter);
		return null;
	}

	public ObjectToTermAdapter getObjectToTermAdapter() {
		LTermAdapter aLTermAdapter = getObjectToTermAdapterAnnotation();
		if(aLTermAdapter != null)
			return LTermAdapterUtil.newAdapter(aLTermAdapter);
		return null;
	}

}
