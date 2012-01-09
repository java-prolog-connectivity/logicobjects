package org.logicobjects.adapter;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jpl.Term;

import org.logicobjects.adapter.adaptingcontext.FieldAdaptingContext;
import org.logicobjects.core.LogicObject;

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
		Term[] parameters = getParameters(object, propertyNames);
		return new LogicObject(objectName, parameters);
	}
	
	private Term[] getParameters(Object object, String[] propertyNames) {
		List<Term> parameters = new ArrayList<Term>();
		for(String propertyName : propertyNames) {
			try {
				Term term = null;
				Object propertyValue = Properties.getBeanPropertyByName(propertyName, object).getValue();
				Field field = Properties.getPropertyByName(object, propertyName).getField();
				term = new ObjectToTermAdapter().adapt(propertyValue, new FieldAdaptingContext(field));
				parameters.add(term);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return parameters.toArray(new Term[] {});
	}
	/*
	public static LogtalkObject userObject() {
		return new LogtalkObject(new ObjectToTermAdapter().adapt("user"));
	}
	*/
	/*
	static class X {private int i = 10;  int getI(){return i;} }
	
	public static void main(String[] args) {
		X x = new X();
		Property property = Properties.getPropertyByName(x, "i");
		//BeanProperty property = Properties.getBeanPropertyByName("i", x);
		//System.out.println(property.getValue());
		System.out.println(property.getFieldValue(x));
		//property.setFieldValue(x, 5);
		//System.out.println(x.getI());
		//Field field = Properties.getPropertyByName(x, "i").getField();
		
	}*/
}
