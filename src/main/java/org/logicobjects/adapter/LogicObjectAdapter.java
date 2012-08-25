package org.logicobjects.adapter;


import jpl.Term;

import org.logicobjects.adapter.adaptingcontext.AdaptingContext;
import org.logicobjects.core.LogicObject;

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

	/**
	 * How a Java object is adapted to a logic object sometimes depends on the context.
	 * Though often the translation can be deduced only from the class of the object,
	 * this method helps a programmer to specify a specific names and properties despite the information present in the class
	 * @param object
	 * @param objectName
	 * @param propertyNames
	 * @return
	 */
	public LogicObject asLogicObject(Object object, String objectName, String[] propertyNames) {
		Term[] arguments = LogicObject.propertiesAsTerms(object, propertyNames);
		return new LogicObject(objectName, arguments);
	}
	
	
	
}
