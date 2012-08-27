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
import org.logicobjects.adapter.objectadapters.ArrayToTermAdapter;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.ArrayTypeWrapper;


public class LogicObject implements ITermObject {

	public static final String LOGTALK_OPERATOR = "::";
	
	public static Compound logtalkMessage(Term object, String messageName, Term[] messageArguments) {
		return new Compound(LOGTALK_OPERATOR, new Term[] {object, LogicUtil.asTerm(messageName, messageArguments)});
	}
	
	private String name;
	private Term[] termArguments;

	public LogicObject(Term term) {
		this(term.name(), term.args());
	}
	
	public LogicObject(String name) {
		this(name, new Term[]{});
	}
	
	public LogicObject(String name, Term[] termArguments) {
		setName(name);
		setTermArguments(termArguments);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return arity() > 0;
	}
	
	public int arity() {
		return getTermArguments().length;
	}
	
	public Query asQuery(String methodName, Object[] messageArgs) {
		Compound compound = logtalkMessage(asTerm(), methodName, ArrayToTermAdapter.objectsAsTerms(messageArgs));
		Query query = new Query(compound);
		return query;
	}

	@Override
	public String toString() {
		return asTerm().toString();
	}
	
	
	
	
	
	
	
	
	
	public static void setPropertiesArray(Object lObject, String argsArray, Term term) {
		Field field = ReflectionUtil.getField(lObject, argsArray);
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(field.getGenericType());
		if(!(typeWrapper instanceof ArrayTypeWrapper))
			throw new RuntimeException("The property " + argsArray + " is not an array instance variable in object " + lObject);
		Term[] termArguments = term.args();
		ReflectionUtil.setFieldValue(lObject, argsArray, new TermToObjectAdapter().adaptField(termArguments, field));
	}
	
	public static void setProperties(Object lObject, String[] properties, Term term) {
		for(int i=0; i<properties.length; i++) {
			String propertyName = properties[i];
			Field field = ReflectionUtil.getField(lObject, propertyName);
			Object fieldValue = new TermToObjectAdapter().adaptField(term.arg(i+1), field);
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
		return new ObjectToTermAdapter().adaptField(propertyValue, field);
	}


	
}

