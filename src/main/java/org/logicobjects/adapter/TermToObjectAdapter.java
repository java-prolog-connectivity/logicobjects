package org.logicobjects.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.datatype.XMLGregorianCalendar;

import jpl.Atom;
import jpl.Compound;
import jpl.Term;
import jpl.Variable;

import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.logicobjects.adapter.adaptingcontext.ClassAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.FieldAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.MethodAdaptationContext;
import org.logicobjects.adapter.objectadapters.TermToArrayAdapter;
import org.logicobjects.adapter.objectadapters.TermToCalendarAdapter;
import org.logicobjects.adapter.objectadapters.TermToCollectionAdapter;
import org.logicobjects.adapter.objectadapters.TermToMapAdapter;
import org.logicobjects.adapter.objectadapters.TermToMapAdapter.TermToEntryAdapter;
import org.logicobjects.adapter.objectadapters.TermToXMLGregorianCalendarAdapter;
import org.logicobjects.core.LogicClass;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.ArrayTypeWrapper;
import org.reflectiveutils.wrappertype.SingleTypeWrapper;
import org.reflectiveutils.wrappertype.VariableTypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Primitives;

public class TermToObjectAdapter<To> extends LogicAdapter<Term, To> {
	
	private static Logger logger = LoggerFactory.getLogger(TermToObjectAdapter.class);

	private LogicEngine engine;
	public TermToObjectAdapter() {
		engine = LogicEngine.getDefault();
	}
	
	/*
	 * This method provides a default adapter behaviour when no target type is specified
	 * (non-Javadoc)
	 * @see logicobjects.adapter.Adapter#adapt(java.lang.Object)
	 */
	
	@Override
	public To adapt(Term term) {
		return adapt(term, Object.class);
	}

	public To adapt(Term term, Type type) {
		return adapt(term, type, null);
	}

	public To adaptField(Term term, Field field) {
		return adapt(term, field.getGenericType(), new FieldAdaptationContext(field));
	}
	
	public To[] adaptField(Term term[], Field field) {
		return adaptTerms(term, field.getGenericType(), new FieldAdaptationContext(field));
	}
	
	public To adaptMethod(Term term, Method method) {
		return adapt(term, method.getGenericReturnType(), new MethodAdaptationContext(method));
	}
	
	public To[] adaptMethod(Term term[], Method method) {
		return adaptTerms(term, method.getGenericReturnType(), new MethodAdaptationContext(method));
	}
	
	public To[] adaptTerms(Term[] terms, Type type, AdaptationContext adaptingContext) {
		Object[] objects = new Object[terms.length];
		for(int i = 0; i<terms.length; i++)
			objects[i] = adapt(terms[i], type, adaptingContext);
		return (To[]) objects;
	}
	
	public To adapt(Term term, Type type, AdaptationContext adaptingContext) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		boolean errorMappingFromAnnotations = false;
		if( (typeWrapper instanceof VariableTypeWrapper) ) //the type is erased
			return adapt(term, Object.class, adaptingContext);
		try {
			return new TermToAnnotatedObjectAdapter<To>().adapt(term, type, adaptingContext);
		} catch(IncompatibleAdapterException | UnrecognizedAdaptationContextException e) {
			//do nothing, these exceptions mean the adapter recognizes it cannot transform the term to an object, but no error has been produced
		} catch(RuntimeException e) {
			errorMappingFromAnnotations = true;
			if(!engine.isList(term)) //TODO verify this...
				throw e;
		}
		if(!errorMappingFromAnnotations) {
			try {
				if( typeWrapper instanceof SingleTypeWrapper ) { //the type is not an array and not an erased type (but still it can be a collection)
					SingleTypeWrapper singleTypeWrapper = SingleTypeWrapper.class.cast(typeWrapper);
					if(term instanceof Variable && !Term.class.isAssignableFrom(singleTypeWrapper.asClass())) {//found a variable, and the method is not explicitly returning terms
						logger.warn("Attempting to transform the variable term " + term + " to an object of class " + singleTypeWrapper.asClass() + ". Transformed as null.");
						return null;
					}
					if(singleTypeWrapper.asClass().equals(Entry.class)) {
						Type entryParameters[] = new GenericsUtil().findAncestorTypeParameters(Entry.class, singleTypeWrapper.getWrappedType());
						return (To) new TermToEntryAdapter().adapt((Compound)term, entryParameters[0], entryParameters[1], adaptingContext);
					}
					if(Calendar.class.isAssignableFrom(singleTypeWrapper.asClass())) {
						return (To)new TermToCalendarAdapter().adapt(term);
					}
					if(XMLGregorianCalendar.class.isAssignableFrom(singleTypeWrapper.asClass())) {
						return (To)new TermToXMLGregorianCalendarAdapter().adapt(term);
					}
					if(Number.class.isAssignableFrom(Primitives.wrap(singleTypeWrapper.asClass()))  ||  LogicUtil.isNumber(term)) { //either the required type is a number or the term is a number
						if( Number.class.isAssignableFrom(Primitives.wrap(singleTypeWrapper.asClass())) ) { //the required type is a number
							if(term.isAtom() || LogicUtil.isNumber(term)) { //check if indeed the term can be converted to a number
								if(singleTypeWrapper.asClass().isPrimitive() || Primitives.isWrapperType(singleTypeWrapper.asClass())) {
									return (To) valueOf(singleTypeWrapper.asClass(), LogicUtil.toString(term));
								} else { //try to convert to a numeric type that is not a primitive nor a wrapper type
									if(singleTypeWrapper.asClass().equals(BigInteger.class))
										return (To) BigInteger.valueOf(LogicUtil.asLong(term));
									else if(singleTypeWrapper.asClass().equals(AtomicInteger.class))
										return (To) new AtomicInteger(LogicUtil.asInt(term));
									else if(singleTypeWrapper.asClass().equals(AtomicLong.class))
										return (To) new AtomicLong((long)LogicUtil.asLong(term));
									else if(singleTypeWrapper.asClass().equals(BigDecimal.class))
										return (To) BigDecimal.valueOf(LogicUtil.asDouble(term));
									else
										throw new RuntimeException(); //it should never arrive here !
								}
							}
						} else {
							if(singleTypeWrapper.asClass().equals(Object.class)) //if we arrive here the term should be a number (jpl.Integer or jpl.Float)
								return (To) LogicUtil.asNumber(term);
						}
					} else if (Primitives.isWrapperType( Primitives.wrap(singleTypeWrapper.asClass()))) { //checks if the class corresponds to a primitive or its wrapper. e.g., boolean, Boolean (at this point it is not a number)
						if(Primitives.wrap(singleTypeWrapper.asClass()).equals(Character.class)) {
							String termString = LogicUtil.toString(term);
							if(termString.length() == 1)
								return (To) Character.valueOf(termString.charAt(0));
							else
								throw new RuntimeException("Impossible to transform the string " + termString + "to a single character");
						} else
							return (To) valueOf(singleTypeWrapper.asClass(), LogicUtil.toString(term));
					} else if(singleTypeWrapper.asClass().equals(String.class)) {
						if(term.isAtom())
							return (To) ((Atom)term).name();
						else /*if(term.isVariable())
							return (VARIABLE_PREFIX+term.name());
						else*/
							return (To) term.toString();
					}
					/*
					if(Term.class.isAssignableFrom(singleTypeWrapper.asClass())) {
						if(singleTypeWrapper.asClass().isAssignableFrom(term.getClass() ))
							return (To) term;
					}*/
					
					if(singleTypeWrapper.isAssignableFrom(Term.class))
						return (To) term;
				}
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		if(engine.isList(term)) {
			return adaptListTerm(term, type, adaptingContext);
		}
		
		throw new TermToObjectException(term, type);  //no idea how to adapt the term
	}

	/**
	 * 
	 * @param clazz
	 * @param s
	 * @return the result of calling the static method "valueOf(String)" on the class sent as parameter
	 */
	private Object valueOf(Class clazz, String s) {
		Class wrapper = Primitives.wrap(clazz); //if the class is already a wrapper, the 'wrap' method will just return that class
		Method m;
		try {
			m = wrapper.getDeclaredMethod("valueOf", String.class);
			return (To) m.invoke(null, s); //'m' is a static method, so no object needs to be provided
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	private To adaptListTerm(Term term, Type type, AdaptationContext adaptingContext) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		if(typeWrapper instanceof ArrayTypeWrapper) {
			return (To) new TermToArrayAdapter().adapt(term, type, adaptingContext);
		}
		if(Collection.class.isAssignableFrom(typeWrapper.asClass())) {
			return (To) new TermToCollectionAdapter().adapt(term, type, adaptingContext);
		}
		if(Map.class.isAssignableFrom(typeWrapper.asClass())) {
			return (To) new TermToMapAdapter().adapt(term, type, adaptingContext);
		}
		throw new TermToObjectException(term, type);
	}
	

/*
	public static Object asObject(Term term, LObjectAdapter lObjectAdapterAnnotation) {
		try {
			TermToObjectAdapter objectAdapter = (TermToObjectAdapter)lObjectAdapterAnnotation.adapter().newInstance();
			objectAdapter.setParameters(lObjectAdapterAnnotation.args());
			return objectAdapter.adapt(term);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
*/

	/*
	 * This method transform a term in a logic object of a specified class using the information present in a logic object annotation.
	 * The annotation is not necessarily found in the instantiating class, but in a super class
	 */
	/*
	public Object asLogicObject(Term term, Type type, Class lObjectClass) {
		SingleTypeWrapper typeWrapper = (SingleTypeWrapper) AbstractTypeWrapper.wrap(type);
		LObjectAdapter lObjectAdapterAnnotation = null;
		try {
			lObjectAdapterAnnotation = (LObjectAdapter)lObjectClass.getAnnotation(LObjectAdapter.class);
			if(lObjectAdapterAnnotation != null) {
				Object obj = asObject(term, lObjectAdapterAnnotation);
				return typeWrapper.asClass().cast(obj);
			}
			LObject lObjectAnnotation = (LObject)lObjectClass.getAnnotation(LObject.class);
			//System.out.println("************"+typeWrapper.asClass().getName());
			Object lObject = null;
			if(typeWrapper.isAssignableFrom(lObjectClass)) {
				lObject = LogtalkObjectFactory.getDefault().create(lObjectClass);
			} else {
				lObject = typeWrapper.asClass().newInstance();  //type wrapper should be below in the hierarchy of lObjectClass
			}
			setParams(lObject, term, lObjectAnnotation.params());
			return lObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
*/

	
	
	
	
}
