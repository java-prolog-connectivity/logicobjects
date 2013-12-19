package org.logicobjects.converter.old;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.datatype.XMLGregorianCalendar;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.jpc.util.PrologUtil;
import org.logicobjects.converter.IncompatibleAdapterException;
import org.logicobjects.converter.context.old.AdaptationContext;
import org.logicobjects.converter.context.old.FieldAdaptationContext;
import org.logicobjects.converter.context.old.MethodAdaptationContext;
import org.logicobjects.methodadapter.LogicAdapter;
import org.minitoolbox.reflection.TypeUtil;
import org.minitoolbox.reflection.typewrapper.ArrayTypeWrapper;
import org.minitoolbox.reflection.typewrapper.SingleTypeWrapper;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;
import org.minitoolbox.reflection.typewrapper.VariableTypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Primitives;

public class TermToObjectConverter<To> extends LogicAdapter<Term, To> {
	
	private static Logger logger = LoggerFactory.getLogger(TermToObjectConverter.class);

	private PrologEngine engine;
	public TermToObjectConverter() {
		//engine = LogicEngine.getDefault();
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
	
	public List<To> adaptField(List<Term> terms, Field field) {
		return adaptTerms(terms, field.getGenericType(), new FieldAdaptationContext(field));
	}
	
	public To adaptMethod(Term term, Method method) {
		return adapt(term, method.getGenericReturnType(), new MethodAdaptationContext(method));
	}
	
	public List<To> adaptMethod(List<Term> terms, Method method) {
		return adaptTerms(terms, method.getGenericReturnType(), new MethodAdaptationContext(method));
	}
	
	public List<To> adaptTerms(List<Term> terms) {
		return adaptTerms(terms, Object.class, null);
	}
	
	public List<To> adaptTerms(List<Term> terms, Type type, AdaptationContext adaptingContext) {
		List<To> objects = new ArrayList<>();
		for(Term term : terms) {
			objects.add(adapt(term, type, adaptingContext));
		}
		return objects;
	}
	
	public To adapt(Term term, Type type, AdaptationContext adaptingContext) {
		TypeWrapper typeWrapper = TypeWrapper.wrap(type);
		boolean errorMappingFromAnnotations = false;
		if( (typeWrapper instanceof VariableTypeWrapper) ) //the type is erased
			return adapt(term, Object.class, adaptingContext);
		try {
			return new TermToAnnotatedObjectAdapter<To>().adapt(term, type, adaptingContext);
		} catch(IncompatibleAdapterException | UnrecognizedAdaptationContextException e) {
			//do nothing, these exceptions mean the adapter recognizes it cannot transform the term to an object, but no error has been produced
		} catch(RuntimeException e) {
			errorMappingFromAnnotations = true;
			if(!term.isList()) //TODO verify this...
				throw e;
		}
		if(!errorMappingFromAnnotations) {
			try {
				if( typeWrapper instanceof SingleTypeWrapper ) { //the type is not an array and not an erased type (but still it can be a collection)
					SingleTypeWrapper singleTypeWrapper = SingleTypeWrapper.class.cast(typeWrapper);
					if(term.isVariable() && !Term.class.isAssignableFrom(singleTypeWrapper.getRawClass())) {//found a variable, and the method is not explicitly returning terms
						logger.warn("Attempting to transform the variable term " + term + " to an object of class " + singleTypeWrapper.getRawClass() + ". Transformed as null.");
						return null;
					}
					if(Entry.class.equals(singleTypeWrapper.getRawClass())) {
						Type entryParameters[] = new TypeUtil().findAncestorTypeParameters(Entry.class, singleTypeWrapper.getWrappedType());
						return (To) new TermToEntryAdapter().adapt((Compound)term, entryParameters[0], entryParameters[1], adaptingContext);
					}
					if(Calendar.class.isAssignableFrom(singleTypeWrapper.getRawClass())) {
						return (To)new TermToCalendarAdapter().adapt(term);
					}
					if(XMLGregorianCalendar.class.isAssignableFrom(singleTypeWrapper.getRawClass())) {
						return (To)new TermToXMLGregorianCalendarAdapter().adapt(term);
					}
					if(Number.class.isAssignableFrom(Primitives.wrap(singleTypeWrapper.getRawClass()))  ||  term.isNumber()) { //either the required type is a number or the term is a number
						if( Number.class.isAssignableFrom(Primitives.wrap(singleTypeWrapper.getRawClass())) ) { //the required type is a number
							if(term.isAtom() || term.isNumber()) { //check if indeed the term can be converted to a number
								if(singleTypeWrapper.getRawClass().isPrimitive() || Primitives.isWrapperType(singleTypeWrapper.getRawClass())) {
									return (To) valueOf(singleTypeWrapper.getRawClass(), PrologUtil.toString(term));
								} else { //try to convert to a numeric type that is not a primitive nor a wrapper type
									if(singleTypeWrapper.getRawClass().equals(BigInteger.class))
										return (To) BigInteger.valueOf(PrologUtil.toLong(term));
									else if(singleTypeWrapper.getRawClass().equals(AtomicInteger.class))
										return (To) new AtomicInteger(PrologUtil.toInt(term));
									else if(singleTypeWrapper.getRawClass().equals(AtomicLong.class))
										return (To) new AtomicLong((long)PrologUtil.toLong(term));
									else if(singleTypeWrapper.getRawClass().equals(BigDecimal.class))
										return (To) BigDecimal.valueOf(PrologUtil.toDouble(term));
									else
										throw new RuntimeException(); //it should never arrive here !
								}
							}
						} else {
							if(singleTypeWrapper.getRawClass().equals(Object.class)) //if we arrive here the term should be a number (jpl.Integer or jpl.Float)
								return (To) PrologUtil.toNumber(term);
						}
					} else if (Primitives.isWrapperType( Primitives.wrap(singleTypeWrapper.getRawClass()))) { //checks if the class corresponds to a primitive or its wrapper. e.g., boolean, Boolean (at this point it is not a number)
						if(Primitives.wrap(singleTypeWrapper.getRawClass()).equals(Character.class)) {
							String termString = PrologUtil.toString(term);
							if(termString.length() == 1)
								return (To) Character.valueOf(termString.charAt(0));
							else
								throw new RuntimeException("Impossible to transform the string " + termString + "to a single character");
						} else
							return (To) valueOf(singleTypeWrapper.getRawClass(), PrologUtil.toString(term));
					} else if( (term.isAtom() && singleTypeWrapper.getRawClass().isAssignableFrom(String.class))
							|| singleTypeWrapper.getRawClass().equals(String.class)) {
						if(term.isAtom())
							return (To) ((Atom)term).getName();
						else /*if(term.isVariable())
							return (VARIABLE_PREFIX+term.name());
						else*/
							return (To) term.toString();
					}
					/*
					if(LTerm.class.isAssignableFrom(singleTypeWrapper.asClass())) {
						if(singleTypeWrapper.asClass().isAssignableFrom(term.getClass() ))
							return (To) term;
					}*/
					
					if(singleTypeWrapper.isRawClassAssignableFrom(Term.class))
						return (To) term;
				}
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		if(term.isList()) {
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
		TypeWrapper typeWrapper = TypeWrapper.wrap(type);
		if(typeWrapper instanceof ArrayTypeWrapper) {
			return (To) new TermToArrayAdapter().adapt(term, type, adaptingContext);
		}
		if(Collection.class.isAssignableFrom(typeWrapper.getRawClass())) {
			return (To) new TermToCollectionAdapter().adapt(term, type, adaptingContext);
		}
		if(Map.class.isAssignableFrom(typeWrapper.getRawClass())) {
			return (To) new TermToMapAdapter().adapt(term, type, adaptingContext);
		}
		throw new TermToObjectException(term, type);
	}
	

/*
	public static Object asObject(LTerm term, LObjectAdapter lObjectAdapterAnnotation) {
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
	public Object asLogicObject(LTerm term, Type type, Class lObjectClass) {
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
