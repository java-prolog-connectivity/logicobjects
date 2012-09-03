package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import jpl.Term;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.core.LogicObject;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

public class ClassAdaptationContext extends AnnotatedSingleElementAdaptationContext {

	private Class clazz;
	
	public ClassAdaptationContext(Class clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public AnnotatedElement getContext() {
		return clazz;
	}
	
	@Override
	public Class getContextClass() {
		return clazz;
	}
	





	
}

