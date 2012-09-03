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
import org.logicobjects.adapter.adaptingcontext.BeanPropertyAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.FieldAdaptationContext;
import org.logicobjects.adapter.objectadapters.ArrayToTermAdapter;
import org.logicobjects.adapter.objectadapters.TermToArrayAdapter;
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
	private Object[] arguments;
	
	public LogicObject(String name) {
		this(name, new Object[]{});
	}
	
	public LogicObject(String name, Object[] arguments) {
		setName(name);
		setArguments(arguments);
	}
	
	public LogicObject(String name, int arity) {
		this(name, LogicUtil.variables(arity));
	}

	public LogicObject(Term term) {
		this(term.name(), TermToArrayAdapter.termsAsObjects(term.args()));
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	private Object[] getArguments() {
		return arguments;
	}

	private void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	@Override
	public Term asTerm() {
		if( isParametrizedObject() )
			return new Compound(getName(), ArrayToTermAdapter.objectsAsTerms(getArguments()));
		else
			return new Atom(getName());
	}
	
	
	public boolean isParametrizedObject() {
		return arity()>0;
	}
	
	public int arity() {
		return getArguments().length;
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
	
	
	
	
	
	
	
	public static void setProperty(Object lObject, String propertyName, Term term) {
		BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(lObject.getClass(), propertyName);
		Object value = new TermToObjectAdapter().adapt(term, adaptationContext.getPropertyType(), adaptationContext);
		ReflectionUtil.setProperty(lObject, propertyName, value, adaptationContext.getGuidingClass());
	}
	
	public static void setPropertiesArray(Object lObject, String argsList, Term term) {
		BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(lObject.getClass(), argsList);
		Field field = adaptationContext.getPropertyField();
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(field.getGenericType());
		if(!(typeWrapper instanceof ArrayTypeWrapper))
			throw new RuntimeException("The property " + argsList + " is not an array instance variable in object " + lObject);
		Term[] termArguments = term.args();
		Object adaptedArgs = new TermToObjectAdapter().adaptTerms(termArguments, adaptationContext.getPropertyType(), adaptationContext);
		ReflectionUtil.setProperty(lObject, argsList, adaptedArgs, adaptationContext.getGuidingClass());
	}
	
	public static void setPropertiesFromTermArgs(Object lObject, String[] properties, Term term) {
		for(int i=0; i<properties.length; i++) {
			String propertyName = properties[i];
			setProperty(lObject, propertyName, term.arg(i+1));
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
	
	public static Term propertyAsTerm(Object lObject, String propertyName) {
		BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(lObject.getClass(), propertyName);
		Object propertyValue = ReflectionUtil.getProperty(lObject, propertyName, adaptationContext.getGuidingClass());
		//Field field = ReflectionUtil.getProperty(lObject, propertyName);
		return new ObjectToTermAdapter().adapt(propertyValue, adaptationContext);
	}



}

