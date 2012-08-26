package org.logicobjects.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.FieldAdaptingContext;
import org.logicobjects.adapter.objectadapters.ArrayToTermAdapter;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.ReflectionUtil;


public class LogicObject implements ITermObject {

	public static final String LOGTALK_OPERATOR = "::";
	
	public static Compound logtalkMessage(Term object, String messageName, Term[] messageArguments) {
		return new Compound(LOGTALK_OPERATOR, new Term[] {object, LogicUtil.asTerm(messageName, messageArguments)});
	}
	
	
	private String name;
	private Object[] objectProperties;
	private Term[] termArguments;
	//private Term asTerm;
	
	public LogicObject(Term term) {
		this(term.name(), term.args());
	}
	
	public LogicObject(String name) {
		this(name, new Object[]{});
	}
	
	public LogicObject(String name, Object[] arguments) {
		setName(name);
		if(arguments == null)
			arguments = new Object[]{};
		setObjectProperties(arguments);
	}

	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getObjectProperties() {
		return objectProperties;
	}

	public void setObjectProperties(Object[] objectProperties) {
		setTermArguments(ArrayToTermAdapter.arrayAsTerms(objectProperties));		
		this.objectProperties = objectProperties;
	}

	private Term[] getTermArguments() {
		return termArguments;
	}

	private void setTermArguments(Term[] termArguments) {
		this.termArguments = termArguments;
	}

	@Override
	public Term asTerm() {
		if( isParametrizedObject() )
			return new Compound(getName(), getTermArguments());
		else
			return new Atom(getName());
	}
	
	
	public boolean isParametrizedObject() {
		return getTermArguments().length > 0;
	}
	
	public Query invokeMethod(String methodName, Object[] messageArgs) {
		Compound compound = logtalkMessage(asTerm(), methodName, ArrayToTermAdapter.arrayAsTerms(messageArgs));
		Query query = new Query(compound);
		return query;
	}

	@Override
	public String toString() {
		return asTerm().toString();
	}
	
	
	public static void setProperties(Object lObject, String[] properties, Term term) {
		for(int i=0; i<properties.length; i++) {
			String propertyName = properties[i];
			Field field = ReflectionUtil.getField(lObject, propertyName);
			Object fieldValue = new TermToObjectAdapter().adapt(term.arg(i+1), field.getGenericType(), new FieldAdaptingContext(field));
			ReflectionUtil.setFieldValue(lObject, propertyName, fieldValue);
		}
	}

	public static Term[] propertiesAsTerms(Object object, String[] propertyNames) {
		List<Term> arguments = new ArrayList<Term>();
		for(String propertyName : propertyNames) {
			try {
				Term term = propertyAsTerm(object, propertyName);
				arguments.add(term);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return arguments.toArray(new Term[] {});
	}
	
	public static Term propertyAsTerm(Object object, String propertyName) {
		Object propertyValue = ReflectionUtil.getFieldValue(object, propertyName);
		Field field = ReflectionUtil.getField(object, propertyName);
		return new ObjectToTermAdapter().adapt(propertyValue, new FieldAdaptingContext(field));
	}
	
}

