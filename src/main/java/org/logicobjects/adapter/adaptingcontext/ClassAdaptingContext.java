package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Type;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.core.LogtalkObjectFactory;
import org.reflectiveutils.AbstractTypeWrapper;

public class ClassAdaptingContext extends AdaptingContext {

	private Class clazz;

	public ClassAdaptingContext(Class clazz) {
		this.clazz = clazz;
	}
	
	public Class getContextClass() {
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
	public LObject getLogicObjectAnnotation() {
		return (LObject) getContextClass().getAnnotation(LObject.class);
	}
	
	public Object createLObjectFromAnnotation(Term term, Type type, LObject lObjectAnnotation) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		try {
			Object lObject = null;
			if(typeWrapper.isAssignableFrom(getContextClass())) {
				lObject = LogtalkObjectFactory.getDefault().create(getContextClass());
			} else {
				lObject = typeWrapper.asClass().newInstance();  //type wrapper should be below in the hierarchy of lObjectClass
			}
			setParams(lObject, term, lObjectAnnotation.params());
			
			return lObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

