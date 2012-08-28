package org.logicobjects.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.datatype.XMLGregorianCalendar;

import jpl.Atom;
import jpl.Term;

import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.logicobjects.adapter.adaptingcontext.ClassAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.FieldAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.MethodAdaptationContext;
import org.logicobjects.adapter.objectadapters.AnyCollectionToTermAdapter;
import org.logicobjects.adapter.objectadapters.CalendarToTermAdapter;
import org.logicobjects.adapter.objectadapters.ImplementationMap;
import org.logicobjects.adapter.objectadapters.MapToTermAdapter.EntryToTermAdapter;
import org.logicobjects.adapter.objectadapters.XMLGregorianCalendarToTermAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.annotation.LTermAdapter.LTermAdapterUtil;
import org.logicobjects.core.ITermObject;
import org.logicobjects.core.LogicObjectClass;

import com.google.common.primitives.Primitives;

public class ObjectToTermAdapter<From> extends LogicAdapter<From, Term> {
	
	@Override
	public Term adapt(From object) {
		return adapt(object, null);
	}

	public Term adaptField(From object, Field field) {
		return adapt(object, new FieldAdaptationContext(field));
	}
	
	public Term[] adaptField(From[] objects, Field field) {
		return adaptObjects(objects, new FieldAdaptationContext(field));
	}
	
	public Term adaptMethod(From object, Method method) {
		return adapt(object, new MethodAdaptationContext(method));
	}
	
	public Term[] adaptMethod(From[] objects, Method method) {
		return adaptObjects(objects, new MethodAdaptationContext(method));
	}
	
	public Term[] adaptObjects(From[] objects, AdaptationContext adaptingContext) {
		Term[] terms = new Term[objects.length];
		for(int i = 0; i<objects.length; i++)
			terms[i] = adapt(objects[i], adaptingContext);
		return terms;
	}
	
	public Term adapt(From object, AdaptationContext adaptingContext) {
		boolean errorMappingFromAnnotations = false;
		try {
			return new AnnotatedObjectToTermAdapter<From>().adapt(object, adaptingContext);
		} catch(IncompatibleAdapterException | UnrecognizedAdaptationContextException e) {
			//do nothing, these exceptions mean the adapter recognizes it cannot transform the term to an object, but no error has been produced
		} catch(RuntimeException e) {
			errorMappingFromAnnotations = true;
			//catch the exception and do nothing if it is a collection object.
			//that could mean that the adapting context is targeting the individual components of the collection instead of the entire collection itself
			if(!ImplementationMap.isCollectionObject(object))  //TODO verify this...
				throw e;
		}
		if(!errorMappingFromAnnotations) {
			if(object.getClass().equals(String.class)){
				String text = object.toString();
				/*
				//check if the string represents a variable (the first character a '?', after a capital letter and some printable characters later
				if(text.matches("\\"+VARIABLE_PREFIX+"[A-Z][\\w]*"))  //TODO verify if VARIABLE_PREFIX needs escaping with '\\'or not (this -weak- code assumes than yes)
					return new Variable(text.substring(1, text.length()));
				*/
				return new Atom(text);
			} else if(object.getClass().equals(java.lang.Byte.class)) {
				return new jpl.Integer(java.lang.Byte.class.cast(object).byteValue());
			} else if(object.getClass().equals(java.lang.Short.class)) {
				return new jpl.Integer(java.lang.Short.class.cast(object).shortValue());
			} else if(object.getClass().equals(java.lang.Integer.class)) { //better full class name to avoid confusions here
				return new jpl.Integer(java.lang.Integer.class.cast(object).intValue());
			}  else if(object.getClass().equals(AtomicInteger.class)) { 
				return new jpl.Integer(AtomicInteger.class.cast(object).intValue());
			}  else if(object.getClass().equals(java.lang.Long.class)) { //better full class name to avoid confusions here 
				return new jpl.Integer(java.lang.Long.class.cast(object).longValue());
			} else if(object.getClass().equals(AtomicLong.class)) { 
				return new jpl.Integer(AtomicLong.class.cast(object).longValue());
			} else if(object.getClass().equals(BigInteger.class)) {
				return new jpl.Integer(BigInteger.class.cast(object).longValue());
			} else if(object.getClass().equals(java.lang.Float.class)) { 
				return new jpl.Float(java.lang.Float.class.cast(object).floatValue());
			} else if(object.getClass().equals(java.lang.Double.class)) { 
				return new jpl.Float(java.lang.Double.class.cast(object).doubleValue());
			} else if(object.getClass().equals(BigDecimal.class)) { 
				return new jpl.Float(BigDecimal.class.cast(object).doubleValue());
			} else if(Primitives.isWrapperType(object.getClass())) {  //any other primitive
				return new Atom(object.toString());
			} else if(Calendar.class.isAssignableFrom(object.getClass())) {
				return new CalendarToTermAdapter().adapt((Calendar) object);
			} else if(XMLGregorianCalendar.class.isAssignableFrom(object.getClass())) {
				return new XMLGregorianCalendarToTermAdapter().adapt((XMLGregorianCalendar) object);
			} else if(object instanceof Entry) {
				return new EntryToTermAdapter().adapt((Entry) object, adaptingContext);
			} 
			Class guidingClass = LogicObjectClass.findGuidingClass(object.getClass());
			if(guidingClass != null) {
				if(LogicObjectClass.isTermObjectClass(guidingClass))
					return ((ITermObject)object).asTerm();
				throw new ObjectToTermException(object);  //if we arrive here something went wrong
			} else if(object instanceof Term) {
				return (Term)object;
			} else if(object instanceof ITermObject) {
				return ((ITermObject)object).asTerm();
			} 
		}

		if(ImplementationMap.isCollectionObject(object)) 
			return new AnyCollectionToTermAdapter().adapt(object, adaptingContext);
		
		throw new ObjectToTermException(object); //no idea how to adapt the object
	}

	public static Term asTerm(Object object) {
		return new ObjectToTermAdapter().adapt(object);
	}
	
}
