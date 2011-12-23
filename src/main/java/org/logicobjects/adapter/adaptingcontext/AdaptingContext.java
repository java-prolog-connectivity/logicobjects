package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import jpl.Term;

import org.logicobjects.adapter.Adapter;
import org.logicobjects.adapter.LogtalkObjectAdapter;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.ObjectToTermException;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.objectadapters.ImplementationMap;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;
import org.reflectiveutils.AbstractTypeWrapper;

import com.google.code.guava.beans.Properties;
import com.google.code.guava.beans.Property;

public abstract class AdaptingContext {
	
	public static TermToObjectAdapter getTermToObjectAdapter(LObjectAdapter annotation) {
		try {
			TermToObjectAdapter objectAdapter = (TermToObjectAdapter)annotation.adapter().newInstance();
			objectAdapter.setParameters(annotation.args());
			return objectAdapter;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ObjectToTermAdapter getObjectToTermAdapter(LTermAdapter annotation) {
		try {
			ObjectToTermAdapter objectAdapter = (ObjectToTermAdapter)annotation.adapter().newInstance();
			objectAdapter.setParameters(annotation.args());
			return objectAdapter;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public abstract LObject getLogicObjectAnnotation();
	
	public abstract LObjectAdapter getTermToObjectAdapterAnnotation();

	public abstract LTermAdapter getObjectToTermAdapterAnnotation();
	
	public boolean hasLogicObjectAnnotation() {
		return getLogicObjectAnnotation() != null;
	}
	
	public boolean hasTermToObjectAdapter() {
		return getTermToObjectAdapterAnnotation() != null;
	}
	
	public boolean hasObjectToTermAdapter() {
		return getObjectToTermAdapterAnnotation() != null;
	}
	
	public TermToObjectAdapter getTermToObjectAdapter() {
		return getTermToObjectAdapter(getTermToObjectAdapterAnnotation());
	}

	public ObjectToTermAdapter getObjectToTermAdapter() {
		return getObjectToTermAdapter(getObjectToTermAdapterAnnotation());
	}
	
	public boolean canAdaptToTerm() {
		if (hasObjectToTermAdapter())
			return true;
		return hasLogicObjectAnnotation();
	}
	
	public Term adaptToTerm(Object object) {
		LTermAdapter termAdapterAnnotation = getObjectToTermAdapterAnnotation();

		Class termAdapterClazz = termAdapterAnnotation.adapter();

		if(AbstractTypeWrapper.wrap(Adapter.fromType(termAdapterClazz)).asClass().isAssignableFrom(object.getClass())) //if the wrapper is compatible with the object use it
			return ObjectToTermAdapter.asTerm(object, termAdapterAnnotation);
		else { //in the current implementation, an Adapter annotation overrides any LogicObjectAnnotation (even if it was not used because the adapter is not compatible with the object type)
			LObject logicObjectAnnotation = getLogicObjectAnnotation();
			return new LogtalkObjectAdapter().asLogtalkObject(object, logicObjectAnnotation).asTerm();
		}
	}
	

	
	public boolean canAdaptToLObject() {
		if(hasTermToObjectAdapter())
			return true;
		return hasLogicObjectAnnotation();
	}
	

	public Object adaptToLObject(Term term, Type type) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		
		if(hasTermToObjectAdapter()) {
			TermToObjectAdapter objectAdapter = getTermToObjectAdapter();
			return typeWrapper.asClass().cast(objectAdapter.adapt(term));
		} else {
			return createLObjectFromAnnotation(term, type, getLogicObjectAnnotation());
		}
	}
	
	/*
	 * This method transform a term in a logic object of a specified class using the information present in a logic object annotation.
	 * The annotation is not necessarily found in the instantiating class, but in a super class
	 */
	public Object createLObjectFromAnnotation(Term term, Type type, LObject lObjectAnnotation) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		try {
			Object lObject = typeWrapper.asClass().newInstance();
			setParams(lObject, term, lObjectAnnotation.params());
			return lObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setParams(Object lObject, Term term , String[] params) {
		for(int i=0; i<params.length; i++) {
			String propertyName = params[i];
			//Field field = lObject.getClass().getField(propertyName);  //remember, the commented out code fails for private fields
			Property property = Properties.getPropertyByName(lObject, propertyName);
			Field field = property.getField();
			Object fieldValue = new TermToObjectAdapter().adapt(term.arg(i+1), field.getGenericType(), new AccessibleObjectAdaptingContext(field));
			//field.set(lObject, fieldValue); ////remember, the commented out code fails for private fields
			try {
				property.setValueWithSetter(lObject, fieldValue); //try to use the setter if any
			} catch(NullPointerException e) { //setter no defined
				property.setFieldValue(lObject, fieldValue);
			}
		}
	}

}
