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

public class ClassAdaptingContext extends AdaptingContext {

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
	public LObject getLogicObjectAnnotation() {
		return (LObject) getContext().getAnnotation(LObject.class);
	}
	
	public Object createLObjectFromAnnotation(Term term, Type type, LObject lObjectAnnotation) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		try {
			Object lObject = null;
			if(typeWrapper.isAssignableFrom(getContext())) {
				lObject = LogicObjectFactory.getDefault().create(getContext());
			} else {
				lObject = typeWrapper.asClass().newInstance();  //type wrapper should be below in the hierarchy of lObjectClass
			}
			LogicObject.setParams(lObject, term, lObjectAnnotation.params());
			
			return lObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String infereLogicObjectName() {
		return LogicUtil.javaClassNameToProlog(clazz.getSimpleName());
	}

}

