package org.logicobjects.adapter;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import jpl.Atom;
import jpl.Term;

import org.logicobjects.adapter.adaptingcontext.AdaptingContext;
import org.logicobjects.adapter.objectadapters.ImplementationMap;

import org.logicobjects.adapter.objectadapters.AnyCollectionToTermAdapter;
import org.logicobjects.adapter.objectadapters.MapToTermAdapter.EntryToTermAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.core.ITermObject;
import org.reflectiveutils.AbstractTypeWrapper;

import com.google.common.primitives.Primitives;

public class ObjectToTermAdapter<From> extends LogicAdapter<From, Term> {

	@Override
	public Term adapt(From object) {
		return adapt(object, null);
	}


	public Term adapt(From object, AdaptingContext adaptingContext) {
		if(adaptingContext != null && adaptingContext.canAdaptToTerm()) {
			try {
				return adaptingContext.adaptToTerm(object);
			} catch(RuntimeException e) {
				if(!ImplementationMap.isCollectionObject(object))
					throw e;
			}
		} else {
			if(object instanceof String){
				String text = object.toString();
				/*
				//check if the string represents a variable (the first character a '?', after a capital letter and some printable characters later
				if(text.matches("\\"+VARIABLE_PREFIX+"[A-Z][\\w]*"))  //TODO verify if VARIABLE_PREFIX needs escaping with '\\'or not (this -weak- code assumes than yes)
					return new Variable(text.substring(1, text.length()));
				*/
				return new Atom(text);
			} else if(object.getClass().equals(java.lang.Integer.class)) { //better full class name to avoid confusions here ;)
				return new jpl.Integer(java.lang.Integer.class.cast(object).intValue());
			}  else if(object.getClass().equals(java.lang.Long.class)) { //better full class name to avoid confusions here ;)
				return new jpl.Integer(java.lang.Long.class.cast(object).longValue());
			}  else if(object.getClass().equals(java.lang.Float.class)) { 
				return new jpl.Float(java.lang.Float.class.cast(object).floatValue());
			} else if(object.getClass().equals(java.lang.Double.class)) { 
				return new jpl.Float(java.lang.Double.class.cast(object).doubleValue());
			} else if(Primitives.isWrapperType(object.getClass())) {  //any other primitive
				return new Atom(object.toString());
			}
			Class guidingClass = findGuidingClass(object.getClass());
			if(guidingClass != null) {
				if(isTermObjectClass(guidingClass))
					return ((ITermObject)object).asTerm();
				
				LTermAdapter termAdapterAnnotation = (LTermAdapter)guidingClass.getAnnotation(LTermAdapter.class);
				if(termAdapterAnnotation!=null) 
					return asTerm(object, termAdapterAnnotation);
				
				LObject logicObjectAnnotation = (LObject)guidingClass.getAnnotation(LObject.class);
				if(logicObjectAnnotation!=null) 
					return new LogtalkObjectAdapter().asLogtalkObject(object, logicObjectAnnotation).asTerm();

				throw new ObjectToTermException(object);  //if we arrive here something went wrong
			} else if(object instanceof Term) {
				return (Term)object;
			} else if(object instanceof ITermObject) {
				return ((ITermObject)object).asTerm();
			} else if(object instanceof Entry) {
				return new EntryToTermAdapter().adapt((Entry) object, adaptingContext);
			} 
		}

		if(ImplementationMap.isCollectionObject(object)) 
			return new AnyCollectionToTermAdapter().adapt(object, adaptingContext);
		
		throw new ObjectToTermException(object); //no idea how to adapt the object
	}

	
	
	public static Term asTerm(Object object) {
		return new ObjectToTermAdapter().adapt(object);
	}
	

	
	public static Term asTerm(Object object, LTermAdapter termAdapterAnnotation) {
		try {
			ObjectToTermAdapter termAdapter = (ObjectToTermAdapter)termAdapterAnnotation.adapter().newInstance();
			termAdapter.setParameters(termAdapterAnnotation.args());
			return termAdapter.adapt(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}


	
	
	
	/*
	 * The guiding class is the first class in the hierarchy that either implements TermObject, has a LogicObject annotation, or a LogicTerm annotation
	 */
	public static Class findGuidingClass(Class candidateClass) {
		if(candidateClass.equals(Object.class))
			return null;
		if(isTermObjectClass(candidateClass) || candidateClass.getAnnotation(LObject.class) != null || candidateClass.getAnnotation(LTermAdapter.class) != null)
			return candidateClass;
		else
			return findGuidingClass(candidateClass.getSuperclass());
	}
	
	private static boolean isTermObjectClass(Class aClass) {
		for(Class anInterface : aClass.getInterfaces()) {
			if(anInterface.equals(ITermObject.class))
				return true;
		}
		return false;
	}
	
}
