package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Type;

import jpl.Term;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.core.LogicObject;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.AbstractTypeWrapper;

public class ClassAdaptingContext extends AnnotatedAdaptingContext {

	private Class clazz;

	public ClassAdaptingContext(Class clazz) {
		this.clazz = clazz;
	}
	
	public Class getContext() {
		return clazz;
	}
	
	@Override
	public LObjectAdapter getTermToObjectAdapterAnnotation() {
		return (LObjectAdapter) getContext().getAnnotation(LObjectAdapter.class);
	}
	
	@Override
	public LTermAdapter getObjectToTermAdapterAnnotation() {
		return (LTermAdapter) getContext().getAnnotation(LTermAdapter.class);
	}

	@Override
	public String infereLogicObjectName() {
		return LogicUtil.javaClassNameToProlog(clazz.getSimpleName());
	}

	@Override
	public LMethodInvokerDescription getMethodInvokerDescription() {
		LObject aLObject = (LObject) getContext().getAnnotation(LObject.class);
		if(aLObject != null)
			return LMethodInvokerDescription.create(aLObject);
		return null;
	}
	
	@Override
	protected Object adaptToObjectFromDescription(Term term, Type type, LMethodInvokerDescription lMethodInvokerDescription) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		try {
			Object lObject = null;
			if(typeWrapper.isAssignableFrom(getContext())) {
                lObject = LogicObjectFactory.getDefault().create(getContext());
            } else {
            	lObject = LogicObjectFactory.getDefault().create(typeWrapper.asClass());
            }
			LogicObject.setParams(lObject, term, lMethodInvokerDescription.params());
			return lObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}

