package org.logicobjects.adapter.adaptercontext;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;

public class ClassAdapterContext extends AdapterContext {

	private Class clazz;
	
	public ClassAdapterContext(Class clazz) {
		this.clazz = clazz;
	}
	
	public Class getAdaptedClass() {
		return clazz;
	}

	@Override
	public TermToObjectAdapter getTermToObjectAdapter() {
		return getTermToObjectAdapter((LObjectAdapter) getAdaptedClass().getAnnotation(LObjectAdapter.class));
	}

	@Override
	public ObjectToTermAdapter getObjectToTermAdapter() {
		return getObjectToTermAdapter((LTermAdapter) getAdaptedClass().getAnnotation(LTermAdapter.class));
	}

	@Override
	public LObject getLogicObjectAnnotation() {
		return (LObject) getAdaptedClass().getAnnotation(LObject.class);
	}

}

