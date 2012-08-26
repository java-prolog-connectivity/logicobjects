package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Type;

import jpl.Term;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.core.LogicObject;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

public class ClassAdaptingContext extends AnnotatedAdaptingContext {

	private Class clazz;

	public ClassAdaptingContext(Class clazz) {
		this.clazz = clazz;
	}
	
	@Override
	protected Class getContextClass() {
		return clazz;
	}
	
	@Override
	public LObjectAdapter getTermToObjectAdapterAnnotation() {
		return (LObjectAdapter) getContextClass().getAnnotation(LObjectAdapter.class);
	}
	
	@Override
	public LTermAdapter getObjectToTermAdapterAnnotation() {
		return (LTermAdapter) getContextClass().getAnnotation(LTermAdapter.class);
	}

	@Override
	public String infereLogicObjectName() {
		return LogicUtil.javaClassNameToProlog(clazz.getSimpleName());
	}

	@Override
	public LObjectGenericDescription getLogicObjectDescription() {
		LObject aLObject = (LObject) getContextClass().getAnnotation(LObject.class);
		if(aLObject != null)
			return LObjectGenericDescription.create(aLObject);
		return null;
	}
	
}

