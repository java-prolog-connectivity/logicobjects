package org.logicobjects.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.datatype.XMLGregorianCalendar;

import org.jpc.converter.TermConvertable;
import org.jpc.converter.instantiation.InstantiationManager;
import org.jpc.term.Atom;
import org.jpc.term.FloatTerm;
import org.jpc.term.Term;
import org.jpc.term.IntegerTerm;
import org.jpc.term.AbstractTerm;
import org.jpc.util.PrologUtil;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.logicobjects.adapter.adaptingcontext.FieldAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.MethodAdaptationContext;
import org.logicobjects.adapter.objectadapters.AnyCollectionToTermAdapter;
import org.logicobjects.adapter.objectadapters.CalendarToTermAdapter;
import org.logicobjects.adapter.objectadapters.MapToTermAdapter.EntryToTermAdapter;
import org.logicobjects.adapter.objectadapters.XMLGregorianCalendarToTermAdapter;
import org.logicobjects.core.LogicObject;
import org.logicobjects.core.LogicObjectClass;
import org.minitoolbox.reflection.ReflectionUtil;

import com.google.common.primitives.Primitives;

public class ObjectToTermAdapter<From> extends LogicAdapter<From, AbstractTerm> {
	
	@Override
	public Term adapt(From object) {
		return adapt(object, null);
	}

	public Term adaptField(From object, Field field) {
		return adapt(object, new FieldAdaptationContext(field));
	}
	
	public List<AbstractTerm> adaptField(List<From> objects, Field field) {
		return adaptObjects(objects, new FieldAdaptationContext(field));
	}
	
	public Term adaptMethod(From object, Method method) {
		return adapt(object, new MethodAdaptationContext(method));
	}
	
	public List<AbstractTerm> adaptMethod(List<From> objects, Method method) {
		return adaptObjects(objects, new MethodAdaptationContext(method));
	}
	
	public List<AbstractTerm> adaptObjects(List<From> objects) {
		return adaptObjects(objects, null);
	}
	
	public List<AbstractTerm> adaptObjects(List<From> objects, AdaptationContext adaptingContext) {
		List<AbstractTerm> terms = new ArrayList<>();
		for(From object : objects) {
			terms.add(adapt(object, adaptingContext));
		}
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
			if(!InstantiationManager.isCollectionObject(object))  //TODO verify this...
				throw e;
		}
		if(!errorMappingFromAnnotations) {
			if(ReflectionUtil.instanceOfOne(object, 
					String.class,
					StringBuilder.class,
					StringBuffer.class,
					Boolean.class,
					Number.class)) {
				return AbstractTerm.newTerm(object); //default conversion
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
					return ((TermConvertable)object).asTerm();
				throw new ObjectToTermException(object);  //if we arrive here something went wrong
			} else if(object instanceof AbstractTerm) {
				return (Term)object;
			} else if(object instanceof TermConvertable) {
				return ((TermConvertable)object).asTerm();
			} 
		}

		if(InstantiationManager.isCollectionObject(object)) 
			return new AnyCollectionToTermAdapter().adapt(object, adaptingContext);
		
		return adaptToTermFromClass(ReflectionUtil.findFirstNonSyntheticClass(object.getClass()));
		//throw new ObjectToTermException(object); //no idea how to adapt the object
	}


	public static Term asTerm(Object object) {
		return new ObjectToTermAdapter().adapt(object);
	}
	
	protected Term adaptToTermFromClass(Class clazz) {
		String logicObjectName = PrologUtil.javaClassNameToProlog(clazz.getSimpleName());
		return new LogicObject(logicObjectName, Collections.emptyList()).asTerm();
	}

}
