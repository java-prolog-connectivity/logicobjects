package org.logicobjects.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import jpl.Atom;
import jpl.Compound;
import jpl.Term;
import jpl.Variable;
import org.logicobjects.adapter.objectadapters.TermToArrayAdapter;
import org.logicobjects.adapter.objectadapters.TermToMapAdapter;
import org.logicobjects.adapter.objectadapters.TermToMapAdapter.TermToEntryAdapter;

import org.logicobjects.adapter.objectadapters.TermToCollectionAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogtalkObject;
import org.logicobjects.core.LogtalkObjectFactory;
import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.AbstractTypeWrapper.ArrayTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.SingleTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.VariableTypeWrapper;

import com.google.code.guava.beans.Properties;
import com.google.code.guava.beans.Property;
import com.google.common.primitives.Primitives;

public class TermToObjectAdapter<To> extends LogicAdapter<Term, To> {
	
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

	public To adapt(Term term, Type type, Field field) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		if( (typeWrapper instanceof VariableTypeWrapper) ) //the type is erased
			return adapt(term, Object.class, field);
		try {
			if(field != null) {
				LObjectAdapter objectAdapterAnnotation = field.getAnnotation(LObjectAdapter.class);
				try {
					if(objectAdapterAnnotation != null) {
						//Class objectAdapterClazz = objectAdapterAnnotation.adapter();
						//if(typeWrapper.isAssignableFrom(AbstractTypeWrapper.wrap(Adapter.toType(objectAdapterClazz)).asClass())) //if the wrapper is compatible with the object use it
							return (To) typeWrapper.asClass().cast(asObject(term, objectAdapterAnnotation));
					} else {
						LObject logicObjectAnnotation = field.getAnnotation(LObject.class);
						if(logicObjectAnnotation != null) {
							Object logicObject = typeWrapper.asClass().newInstance();
							setParams(logicObject, term, logicObjectAnnotation.params());
							return (To) logicObject;
						}
					}
				} catch(TermToObjectException e) {
					if(!engine.isList(term)) //if term is a list, probably the adapters are defined for the elements of the list, not for the list itself
						throw e;
				}
			}


			Class logicObjectClass = null;
			if(! (term instanceof Variable || term instanceof jpl.Integer || term instanceof jpl.Float) )
				logicObjectClass = LogtalkObjectFactory.getDefault().getContext().findLogicClass(term);  
			/*
			 * find out if the term could be mapped to a logic object
			 * the additional type compatibilities verifications are necessary since the fact that the term 'could' be converted to a logic object does not mean that it 'should'
			 */
			if ( logicObjectClass != null && (typeWrapper.isAssignableFrom(logicObjectClass) || logicObjectClass.isAssignableFrom(typeWrapper.asClass())) ) { 
				//System.out.println("************* Logic class found !!!");
				//System.out.println(logicObjectClass.getName());
				return (To) asLogicObject(term, type, logicObjectClass);
			} //else
				//System.out.println("************* Logic class NOT found !!!");
			//System.out.println(typeWrapper.getClass());
			if( typeWrapper instanceof SingleTypeWrapper ) { //the type is not an array and not an erased type
				SingleTypeWrapper singleTypeWrapper = SingleTypeWrapper.class.cast(typeWrapper);
				logicObjectClass = LogtalkObject.findLogicClass(singleTypeWrapper.asClass());  //find out if the expected type is a logic object
				if( logicObjectClass != null ) 
					return (To) asLogicObject(term, type, logicObjectClass);
				
				if(typeWrapper.isAssignableFrom(Term.class))
					return (To) term;
				
				if(typeWrapper.isAssignableFrom(Entry.class)) {
					Type entryParameters[] = AbstractTypeWrapper.unwrap(new GenericsUtil().findParametersInstantiations(Entry.class, singleTypeWrapper.getWrappedType()));
					return (To) new TermToEntryAdapter().adapt((Compound)term, entryParameters[0], entryParameters[1], field);
				}
				if(singleTypeWrapper.asClass().isPrimitive() || Primitives.isWrapperType(singleTypeWrapper.asClass())) {
					if(term.isAtom()) {
						Class wrapper = singleTypeWrapper.asClass().isPrimitive()?Primitives.wrap(singleTypeWrapper.asClass()):singleTypeWrapper.asClass();
						Method m = wrapper.getDeclaredMethod("valueOf", String.class);
						//converting the name of the atom term to a native value
						return (To) m.invoke(null, term.name()); //it is a static method, so no object needs to be provided
					}
				} else if(singleTypeWrapper.asClass().equals(String.class)) {
					if(term.isAtom())
						return (To) ((Atom)term).name();
					else /*if(term.isVariable())
						return (VARIABLE_PREFIX+term.name());
					else*/
						return (To) term.toString();
				}
			}
			if(engine.isList(term)) {
				if(typeWrapper instanceof ArrayTypeWrapper) {
					return (To) new TermToArrayAdapter().adapt(term, type, field);
				}
				if(Collection.class.isAssignableFrom(typeWrapper.asClass())) {
					return (To) new TermToCollectionAdapter().adapt(term, type, field);
				}
				if(Map.class.isAssignableFrom(typeWrapper.asClass())) {
					return (To) new TermToMapAdapter().adapt(term, type, field);
				}
			}
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		throw new TermToObjectException(term, type);
	}




	public static Object asObject(Term term, LObjectAdapter lObjectAdapterAnnotation) {
		try {
			TermToObjectAdapter objectAdapter = (TermToObjectAdapter)lObjectAdapterAnnotation.adapter().newInstance();
			objectAdapter.setParameters(lObjectAdapterAnnotation.args());
			return objectAdapter.adapt(term);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * This method transform a term in a logic object of a specified class using the information present in a logic object annotation.
	 * The annotation is not necessarily found in the instantiating class, but in a super class
	 */
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

	public void setParams(Object lObject, Term term , String[] params) {
		for(int i=0; i<params.length; i++) {
			String propertyName = params[i];
			//Field field = lObject.getClass().getField(propertyName);  //remember, the commented out code fails for private fields
			Property property = Properties.getPropertyByName(lObject, propertyName);
			Field field = property.getField();
			Object fieldValue = adapt(term.arg(i+1), field.getGenericType(), field);
			//field.set(lObject, fieldValue); ////remember, the commented out code fails for private fields
			try {
				property.setValueWithSetter(lObject, fieldValue); //try to use the setter if any
			} catch(NullPointerException e) { //setter no defined
				property.setFieldValue(lObject, fieldValue);
			}
		}
	}
	
	
	
	
}
