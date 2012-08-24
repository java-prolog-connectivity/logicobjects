package org.logicobjects.adapter;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import jpl.Term;
import org.logicobjects.adapter.adaptingcontext.AdaptingContext;
import org.logicobjects.adapter.adaptingcontext.FieldAdaptingContext;
import org.logicobjects.core.LogicObject;
import org.reflectiveutils.ReflectionUtil;

public class LogicObjectAdapter extends Adapter<Object, LogicObject> {

	@Override
	public LogicObject adapt(Object source) {
		return adapt(source, null);
	}
	
	public LogicObject adapt(Object source, AdaptingContext adaptingContext) {
		return asLogicObject(source, adaptingContext);
	}
	/*
	public Term adapt(Object source, AdaptingContext adaptingContext) {
		
	}
	*/
	public LogicObject asLogicObject(Object object, AdaptingContext adaptingContext) {
		if(object instanceof LogicObject) {
			return (LogicObject)object;
		}	
		else {
			return new LogicObject(new ObjectToTermAdapter().adapt(object, adaptingContext));
		}
	}

	public LogicObject asLogicObject(Object object, String objectName, String[] propertyNames) {
		Term[] arguments = fieldsAsTerms(object, propertyNames);
		return new LogicObject(objectName, arguments);
	}
	
	private Term[] fieldsAsTerms(Object object, String[] propertyNames) {
		List<Term> arguments = new ArrayList<Term>();
		for(String propertyName : propertyNames) {
			try {
				Term term = fieldAsTerm(object, propertyName);
				arguments.add(term);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return arguments.toArray(new Term[] {});
	}
	
	public static Term fieldAsTerm(Object object, String propertyName) {
		Object propertyValue = ReflectionUtil.getFieldValue(object, propertyName);
		Field field = ReflectionUtil.getField(object, propertyName);
		return new ObjectToTermAdapter().adapt(propertyValue, new FieldAdaptingContext(field));
	}
	
}
