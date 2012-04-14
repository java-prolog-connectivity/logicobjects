package org.logicobjects.adapter;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jpl.Term;

import org.logicobjects.adapter.adaptingcontext.FieldAdaptingContext;
import org.logicobjects.core.LogicObject;
import org.reflectiveutils.ReflectionUtil;

import com.google.code.guava.beans.Properties;

public class LogicObjectAdapter extends Adapter<Object, LogicObject> {

	@Override
	public LogicObject adapt(Object source) {
		return asLogicObject(source);
	}
	

	
	public LogicObject asLogicObject(Object object) {
		if(object instanceof LogicObject) {
			return (LogicObject)object;
		}	
		else {
			return new LogicObject(new ObjectToTermAdapter().adapt(object));
		}
	}
	
	/*
	public LogicObject asLogicObject(Object object, LObject logicObjectAnnotation) {
		String objectName = logicObjectAnnotation.name();
		String[] propertyNames = logicObjectAnnotation.params();
		Term[] parameters = getParameters(object, propertyNames);
		return new LogicObject(objectName, parameters);
	}
	*/
	
	public LogicObject asLogicObject(Object object, String objectName, String[] propertyNames) {
		Term[] parameters = fieldsAsTerms(object, propertyNames);
		return new LogicObject(objectName, parameters);
	}
	
	private Term[] fieldsAsTerms(Object object, String[] propertyNames) {
		List<Term> parameters = new ArrayList<Term>();
		for(String propertyName : propertyNames) {
			try {
				Term term = fieldAsTerm(object, propertyName);
				parameters.add(term);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return parameters.toArray(new Term[] {});
	}
	
	public static Term fieldAsTerm(Object object, String propertyName) {
		Object propertyValue = ReflectionUtil.getFieldValue(object, propertyName);
		Field field = ReflectionUtil.getField(object, propertyName);
		return new ObjectToTermAdapter().adapt(propertyValue, new FieldAdaptingContext(field));
	}
	
}
