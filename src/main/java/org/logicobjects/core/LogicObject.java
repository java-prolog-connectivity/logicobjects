package org.logicobjects.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jpc.converter.TermConvertable;
import org.jpc.engine.logtalk.LogtalkObject;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.jpc.util.PrologUtil;
import org.logicobjects.converter.context.old.BeanPropertyAdaptationContext;
import org.logicobjects.converter.old.ObjectToTermConverter;
import org.logicobjects.converter.old.TermToObjectConverter;
import org.minitoolbox.reflection.BeansUtil;
import org.minitoolbox.reflection.typewrapper.ArrayTypeWrapper;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;


public class LogicObject<T extends Term> implements TermConvertable<T> {
	
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
		this(name, PrologUtil.anonymousVariables(arity));
	}

	public LogicObject(Term term) {
		this(((Compound)term).getName(), new TermToObjectConverter().adaptTerms(term.getArgs()));
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
	public T asTerm() {
		if( isParametrizedObject() )
			return (T) new Compound(getName(), new ObjectToTermConverter().adaptObjects(getArguments()));
		else
			return (T) new Atom(getName());
	}
	
	
	public boolean isParametrizedObject() {
		return arity()>0;
	}
	
	public int arity() {
		return getArguments().size();
	}
	
	public Term asGoal(String methodName, List messageArgs) {
		Compound compound = LogtalkObject.logtalkMessage(asTerm(), methodName, messageArgs);
		return compound;
	}

	@Override
	public String toString() {
		return asTerm().toString();
	}
	
	
	
	
	
	
	
	public static void setProperty(Object lObject, String propertyName, Term term) {
		BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(lObject.getClass(), propertyName);
		Object value = new TermToObjectConverter().adapt(term, adaptationContext.getPropertyType(), adaptationContext);
		BeansUtil.setProperty(lObject, propertyName, value, adaptationContext.getGuidingClass());
	}
	
	public static void setPropertiesArray(Object lObject, String argsList, Term term) {
		BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(lObject.getClass(), argsList);
		Field field = adaptationContext.getPropertyField();
		TypeWrapper typeWrapper = TypeWrapper.wrap(field.getGenericType());
		if(!(typeWrapper instanceof ArrayTypeWrapper))
			throw new RuntimeException("The property " + argsList + " is not an array instance variable in object " + lObject);
		List<Term> termArguments = term.getArgs();
		Object adaptedArgs = new TermToObjectConverter().adaptTerms(termArguments, adaptationContext.getPropertyType(), adaptationContext);
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
			propertyAsTerm = new ObjectToTermConverter().adapt(lObject);
		else {
			BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(lObject.getClass(), propertyName);
			Object propertyValue = BeansUtil.getProperty(lObject, propertyName, adaptationContext.getGuidingClass());
			//Field field = ReflectionUtil.getProperty(lObject, propertyName);
			propertyAsTerm = new ObjectToTermConverter().adapt(propertyValue, adaptationContext);
		}
		return propertyAsTerm;
	}



}

