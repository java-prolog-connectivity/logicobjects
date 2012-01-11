package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.AccessibleObject;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;

public abstract class AccessibleObjectAdaptingContext extends AdaptingContext {

	//private AccessibleObject accessibleObject;
	/*
	public AccessibleObjectAdaptingContext(AccessibleObject accessibleObject) {
		this.accessibleObject = accessibleObject;
	}
	*/
	/*
	public AccessibleObject getContext() {
		return accessibleObject;
	}
	*/
	
	public abstract AccessibleObject getContext();
	
	@Override
	public LObjectAdapter getTermToObjectAdapterAnnotation() {
		return (LObjectAdapter) getContext().getAnnotation(LObjectAdapter.class);
	}

	@Override
	public LTermAdapter getObjectToTermAdapterAnnotation() {
		return (LTermAdapter) getContext().getAnnotation(LTermAdapter.class);
	}

	@Override
	public LObject getLogicObjectAnnotation() {
		return (LObject) getContext().getAnnotation(LObject.class);
	}

}