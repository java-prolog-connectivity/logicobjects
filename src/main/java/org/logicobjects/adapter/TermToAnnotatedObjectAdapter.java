package org.logicobjects.adapter;

import java.lang.reflect.Type;

import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.logicobjects.adapter.adaptingcontext.AnnotatedElementAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.ClassAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.LogicObjectDescriptor;
import org.logicobjects.core.LogicClass;
import org.logicobjects.core.LogicObject;
import org.logicobjects.core.LogicObjectFactory;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

import jpl.Term;

public class TermToAnnotatedObjectAdapter<To> extends Adapter<Term, Object> {

	@Override
	public Object adapt(Term term) {
		return adapt(term, null);
	}

	public To adapt(Term term, Type type) {
		return adapt(term, type, null);
	}

	public To adapt(Term term, Type type, AdaptationContext context) {
		AnnotatedElementAdaptationContext annotatedContext;
		if(context != null) {
			if(understandsContext(context))
				annotatedContext = (AnnotatedElementAdaptationContext)context;
			else
				throw new UnrecognizedAdaptationContextException(this.getClass(), context);
		} else {
			annotatedContext = getTermAnnotationContext(term, type); //the current context is null, then create default context
		}
		if(annotatedContext!=null) {
			if(annotatedContext.hasTermToObjectAdapter()) {
				return (To) adaptToObjectFromAdapter(term, type, annotatedContext);
			} else if(annotatedContext.hasLogicObjectDescription()) {
				return (To) adaptToObjectFromDescription(term, type, annotatedContext);
			}
		}
		throw new IncompatibleAdapterException(this.getClass(), term);
	}


	protected Object adaptToObjectFromAdapter(Term term, Type type, AnnotatedElementAdaptationContext annotatedContext) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		TermToObjectAdapter objectAdapter = annotatedContext.getTermToObjectAdapter();
		return typeWrapper.asClass().cast(objectAdapter.adapt(term));
	}
	
	
	/*
	 * This method transform a term in a logic object of a specified class using the information present in a LObjectGenericDescription object.
	 */
	protected Object adaptToObjectFromDescription(Term term, Type type, AnnotatedElementAdaptationContext annotatedContext) {
		LogicObjectDescriptor logicObjectDescription = annotatedContext.getLogicObjectDescription();
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		try {
			Object lObject = null;
			if(typeWrapper.isAssignableFrom(annotatedContext.getContextClass())) {
                lObject = LogicObjectFactory.getDefault().create(annotatedContext.getContextClass());
            } else {
            	lObject = LogicObjectFactory.getDefault().create(typeWrapper.asClass());
            }
			String argsArray = logicObjectDescription.argsArray();
			if(argsArray != null && !argsArray.isEmpty())
				LogicObject.setPropertiesArray(lObject, argsArray, term);
			else
				LogicObject.setProperties(lObject, logicObjectDescription.args(), term);
			return lObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean understandsContext(AdaptationContext context) {
		return context != null && context instanceof AnnotatedElementAdaptationContext;
	}
	
	
	public static AnnotatedElementAdaptationContext getTermAnnotationContext(Term term, Type type) {
		AnnotatedElementAdaptationContext annotatedContext = null;
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		Class logicObjectClass = LogicObjectFactory.getDefault().getContext().findLogicClass(term);  
		/*
		 * find out if the term could be mapped to a logic object
		 * the additional type compatibilities verifications are necessary since the fact that the term 'could' be converted to a logic object does not mean that it 'should'
		 */
		if ( logicObjectClass != null && (typeWrapper.isAssignableFrom(logicObjectClass) || logicObjectClass.isAssignableFrom(typeWrapper.asClass())) ) { 
			annotatedContext = new ClassAdaptationContext(logicObjectClass);
		} else {
			logicObjectClass = LogicClass.findLogicClass(typeWrapper.asClass()); //find out if the expected type is a logic object
			if(logicObjectClass != null)
				annotatedContext = new ClassAdaptationContext(logicObjectClass); 
		}
		return annotatedContext;
	}
	

}
