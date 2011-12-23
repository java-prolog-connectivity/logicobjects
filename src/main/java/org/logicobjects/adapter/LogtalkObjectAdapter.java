package org.logicobjects.adapter;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jpl.Term;

import org.logicobjects.annotation.LObject;
import org.logicobjects.core.LogtalkObject;

import com.google.code.guava.beans.Properties;

public class LogtalkObjectAdapter extends Adapter<Object, LogtalkObject> {

	@Override
	public LogtalkObject adapt(Object source) {
		return asLogtalkObject(source);
	}
	

	
	public LogtalkObject asLogtalkObject(Object object) {
		if(object instanceof LogtalkObject) {
			return (LogtalkObject)object;
		}	
		else {
			return new LogtalkObject(new ObjectToTermAdapter().adapt(object));
		}
	}
	
	
	public LogtalkObject asLogtalkObject(Object object, LObject logicObjectAnnotation) {
		String objectName = logicObjectAnnotation.name();
		String[] propertyNames = logicObjectAnnotation.params();
		Term[] parameters = getParameters(object, propertyNames);
		return new LogtalkObject(objectName, parameters);
	}
	
	private Term[] getParameters(Object object, String[] propertyNames) {
		List<Term> parameters = new ArrayList<Term>();
		for(String propertyName : propertyNames) {
			try {
				Term term = null;
				Object propertyValue = Properties.getBeanPropertyByName(propertyName, object).getValue();
				Field field = Properties.getPropertyByName(object, propertyName).getField();
				term = new ObjectToTermAdapter().adapt(propertyValue, field);
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
