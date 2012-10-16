package org.logicobjects.core;

import static org.logicobjects.LogicObjects.LOGTALK_OPERATOR;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.BeanPropertyAdaptationContext;
import org.logicobjects.adapter.objectadapters.ArrayToTermAdapter;
import org.logicobjects.adapter.objectadapters.TermToArrayAdapter;
import org.logicobjects.term.Atom;
import org.logicobjects.term.Compound;
import org.logicobjects.term.Term;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.BeansUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.ArrayTypeWrapper;


public class LogicObject implements ITermObject {

	public static Compound logtalkMessage(Term object, String messageName, List<Term> messageArguments) {
		return new Compound(LOGTALK_OPERATOR, Arrays.asList(object, new Compound(messageName, messageArguments)) );
	}
	
	private String name;
	private List arguments;
	
	public LogicObject(String name) {
		this(name, Collections.emptyList());
	}
	
	public LogicObject(String name, List arguments) {
		setName(name);
		setArguments(arguments);
	}
	
	public LogicObject(String name, int arity) {
		this(name, LogicUtil.anonymousVariables(arity));
	}

	public LogicObject(Term term) {
		this(((Compound)term).name(), new TermToObjectAdapter().adaptTerms(term.args()));
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	private List getArguments() {
		return arguments;
	}

	private void setArguments(List arguments) {
		this.arguments = arguments;
	}

	@Override
	public Term asTerm() {
		if( isParametrizedObject() )
			return new Compound(getName(), new ObjectToTermAdapter().adaptObjects(getArguments()));
		else
			return new Atom(getName());
	}
	
	
	public boolean isParametrizedObject() {
		return arity()>0;
	}
	
	public int arity() {
		return getArguments().size();
	}
	
	public Term asGoal(String methodName, List messageArgs) {
		Compound compound = logtalkMessage(asTerm(), methodName, messageArgs);
		return compound;
	}

	@Override
	public String toString() {
		return asTerm().toString();
	}
	
	
	
	
	
	
	
	public static void setProperty(Object lObject, String propertyName, Term term) {
		BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(lObject.getClass(), propertyName);
		Object value = new TermToObjectAdapter().adapt(term, adaptationContext.getPropertyType(), adaptationContext);
		BeansUtil.setProperty(lObject, propertyName, value, adaptationContext.getGuidingClass());
	}
	
	public static void setPropertiesArray(Object lObject, String argsList, Term term) {
		BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(lObject.getClass(), argsList);
		Field field = adaptationContext.getPropertyField();
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(field.getGenericType());
		if(!(typeWrapper instanceof ArrayTypeWrapper))
			throw new RuntimeException("The property " + argsList + " is not an array instance variable in object " + lObject);
		List<Term> termArguments = term.args();
		Object adaptedArgs = new TermToObjectAdapter().adaptTerms(termArguments, adaptationContext.getPropertyType(), adaptationContext);
		BeansUtil.setProperty(lObject, argsList, adaptedArgs, adaptationContext.getGuidingClass());
	}
	
	public static void setPropertiesFromTermArgs(Object lObject, List<String> properties, Term term) {
		for(int i=0; i<properties.size(); i++) {
			setProperty(lObject, properties.get(i), term.arg(i+1));
		}
	}

	public static List<Term> propertiesAsTerms(Object object, List<String> propertyNames) {
		List<Term> arguments = new ArrayList<Term>();
		for(String propertyName : propertyNames) {
			try {
				Term term = propertyAsTerm(object, propertyName);
				arguments.add(term);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return arguments;
	}
	
	public static Term propertyAsTerm(Object lObject, String propertyName) {
		Term propertyAsTerm = null;
		if(propertyName.equals("this"))
			propertyAsTerm = new ObjectToTermAdapter().adapt(lObject);
		else {
			BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(lObject.getClass(), propertyName);
			Object propertyValue = BeansUtil.getProperty(lObject, propertyName, adaptationContext.getGuidingClass());
			//Field field = ReflectionUtil.getProperty(lObject, propertyName);
			propertyAsTerm = new ObjectToTermAdapter().adapt(propertyValue, adaptationContext);
		}
		return propertyAsTerm;
	}



}

