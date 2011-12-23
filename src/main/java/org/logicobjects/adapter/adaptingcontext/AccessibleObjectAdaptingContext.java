package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.AccessibleObject;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;

public class AccessibleObjectAdaptingContext extends AdaptingContext {

	private AccessibleObject accessibleObject;
	
	public AccessibleObjectAdaptingContext(AccessibleObject accessibleObject) {
		this.accessibleObject = accessibleObject;
	}
	
	public AccessibleObject getContextAccessibleObject() {
		return accessibleObject;
	}
	
	@Override
	public LObjectAdapter getTermToObjectAdapterAnnotation() {
		return (LObjectAdapter) getContextAccessibleObject().getAnnotation(LObjectAdapter.class);
	}

	@Override
	public LTermAdapter getObjectToTermAdapterAnnotation() {
		return (LTermAdapter) getContextAccessibleObject().getAnnotation(LTermAdapter.class);
	}

	@Override
	public LObject getLogicObjectAnnotation() {
		return (LObject) getContextAccessibleObject().getAnnotation(LObject.class);
	}

}
