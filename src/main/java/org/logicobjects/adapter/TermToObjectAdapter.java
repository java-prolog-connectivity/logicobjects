package org.logicobjects.adapter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import jpl.Atom;
import jpl.Compound;
import jpl.Term;
import jpl.Variable;

import org.logicobjects.adapter.adaptingcontext.AdaptingContext;
import org.logicobjects.adapter.adaptingcontext.ClassAdaptingContext;
import org.logicobjects.adapter.objectadapters.TermToArrayAdapter;
import org.logicobjects.adapter.objectadapters.TermToCollectionAdapter;
import org.logicobjects.adapter.objectadapters.TermToMapAdapter;
import org.logicobjects.adapter.objectadapters.TermToMapAdapter.TermToEntryAdapter;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LObjectAdapter.LObjectAdapterUtil;
import org.logicobjects.core.LogicClass;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.ArrayTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.SingleTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.VariableTypeWrapper;
import org.reflectiveutils.GenericsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Primitives;

public class TermToObjectAdapter<To> extends LogicAdapter<Term, To> {
	
	private static Logger logger = LoggerFactory.getLogger(TermToObjectAdapter.class);
	
	public static TermToObjectAdapter create(LObjectAdapter aLObjectAdapter) {
		try {
			TermToObjectAdapter objectAdapter = (TermToObjectAdapter)LObjectAdapterUtil.getAdapter(aLObjectAdapter).newInstance();
			objectAdapter.setParameters(aLObjectAdapter.args());
			return objectAdapter;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
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

	public To adapt(Term term, Type type, AdaptingContext adaptingContext) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		if( (typeWrapper instanceof VariableTypeWrapper) ) //the type is erased
			return adapt(term, Object.class, adaptingContext);

		if(adaptingContext != null && adaptingContext.canAdaptToLObject()) {
			try {
				return (To) adaptingContext.adaptToLObject(term, type);
			} catch(RuntimeException e) {
				if(!engine.isList(term))
					throw e;
			}
		} else {
			try {
				Class logicObjectClass = LogicObjectFactory.getDefault().getContext().findLogicClass(term);  

				/*
				 * find out if the term could be mapped to a logic object
				 * the additional type compatibilities verifications are necessary since the fact that the term 'could' be converted to a logic object does not mean that it 'should'
				 */
				if ( logicObjectClass != null && (typeWrapper.isAssignableFrom(logicObjectClass) || logicObjectClass.isAssignableFrom(typeWrapper.asClass())) ) { 
					//System.out.println("************* Logic class found !!!");
					//System.out.println(logicObjectClass.getName());
					return (To)new ClassAdaptingContext(logicObjectClass).adaptToLObject(term, type);
				} //else
					//System.out.println("************* Logic class NOT found !!!");
				//System.out.println(typeWrapper.getClass());
				if( typeWrapper instanceof SingleTypeWrapper ) { //the type is not an array and not an erased type (but it can be a collection)
					SingleTypeWrapper singleTypeWrapper = SingleTypeWrapper.class.cast(typeWrapper);
					
					if(term instanceof Variable && !Term.class.isAssignableFrom(singleTypeWrapper.asClass())) {//found a variable, and the method is not explicitly returning terms
						logger.warn("Attempting to transform the variable term " + term + " to an object of class " + singleTypeWrapper.asClass() + ". Transformed as null.");
						return null;
					}
					logicObjectClass = LogicClass.findLogicClass(singleTypeWrapper.asClass());  //find out if the expected type is a logic object
					if( logicObjectClass != null ) 
						return (To) new ClassAdaptingContext(logicObjectClass).adaptToLObject(term, type);
					
					if(singleTypeWrapper.asClass().equals(Entry.class)) {
						Type entryParameters[] = new GenericsUtil().findAncestorTypeParameters(Entry.class, singleTypeWrapper.getWrappedType());
						return (To) new TermToEntryAdapter().adapt((Compound)term, entryParameters[0], entryParameters[1], adaptingContext);
					}
					if(singleTypeWrapper.asClass().isPrimitive() || Primitives.isWrapperType(singleTypeWrapper.asClass())) {
						if(term.isAtom() || term.isInteger() || term.isFloat() ) {
							Class wrapper = singleTypeWrapper.asClass().isPrimitive()?Primitives.wrap(singleTypeWrapper.asClass()):singleTypeWrapper.asClass();
							Method m = wrapper.getDeclaredMethod("valueOf", String.class);
							//converting the name of the atom term to a native value
							return (To) m.invoke(null, LogicUtil.toString(term)); //'m' is a static method, so no object needs to be provided
						}
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

	
	private To adaptListTerm(Term term, Type type, AdaptingContext adaptingContext) {
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
