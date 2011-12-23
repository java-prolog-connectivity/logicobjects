package org.logicobjects.adapter.adaptercontext;

import java.lang.reflect.AccessibleObject;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;

public class AccessibleObjectAdapterContext extends AdapterContext {

	private AccessibleObject accessibleObject;
	
	public AccessibleObject getAdaptedAccessibleObject() {
		return accessibleObject;
	}
	
	@Override
	public TermToObjectAdapter getTermToObjectAdapter() {
		return getTermToObjectAdapter((LObjectAdapter) getAdaptedAccessibleObject().getAnnotation(LObjectAdapter.class));
	}

	@Override
	public ObjectToTermAdapter getObjectToTermAdapter() {
		return getObjectToTermAdapter((LTermAdapter) getAdaptedAccessibleObject().getAnnotation(LTermAdapter.class));
	}

	@Override
	public LObject getLogicObjectAnnotation() {
		return (LObject) getAdaptedAccessibleObject().getAnnotation(LObject.class);
	}

}
