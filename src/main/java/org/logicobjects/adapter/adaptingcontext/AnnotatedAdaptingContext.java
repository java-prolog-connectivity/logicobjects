package org.logicobjects.adapter.adaptingcontext;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;

public abstract class AnnotatedAdaptingContext extends AdaptingContext {
	protected abstract LObjectAdapter getTermToObjectAdapterAnnotation();
	protected abstract LTermAdapter getObjectToTermAdapterAnnotation();
	
	public TermToObjectAdapter getTermToObjectAdapter() {
		LObjectAdapter aLObjectAdapter = getTermToObjectAdapterAnnotation();
		if(aLObjectAdapter != null)
			return TermToObjectAdapter.create(aLObjectAdapter);
		return null;
	}

	public ObjectToTermAdapter getObjectToTermAdapter() {
		LTermAdapter aLTermAdapter = getObjectToTermAdapterAnnotation();
		if(aLTermAdapter != null)
			return ObjectToTermAdapter.create(aLTermAdapter);
		return null;
	}

}
