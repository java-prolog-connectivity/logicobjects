package org.logicobjects.converter.old;

import static org.logicobjects.LogicObjects.newLogicObject;

import java.lang.reflect.Type;

import org.jpc.term.Term;
import org.logicobjects.LogicObjects;
import org.logicobjects.converter.IncompatibleAdapterException;
import org.logicobjects.converter.context.old.AdaptationContext;
import org.logicobjects.converter.context.old.AnnotatedElementAdaptationContext;
import org.logicobjects.converter.context.old.ClassAdaptationContext;
import org.logicobjects.core.LogicClass;
import org.logicobjects.core.LogicObject;
import org.logicobjects.descriptor.LogicObjectDescriptor;
import org.logicobjects.methodadapter.LogicAdapter;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class TermToAnnotatedObjectAdapter<To> extends LogicAdapter<Term, Object> {

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
			if(annotatedContext.hasTermToObjectConverter()) {
				return (To) adaptToObjectFromAdapter(term, type, annotatedContext);
			} else if(annotatedContext.hasLogicObjectDescription()) {
				return (To) adaptToObjectFromDescription(term, type, annotatedContext);
			}
		}
		throw new IncompatibleAdapterException(this.getClass(), term);
	}


	protected Object adaptToObjectFromAdapter(Term term, Type type, AnnotatedElementAdaptationContext annotatedContext) {
		TypeWrapper typeWrapper = TypeWrapper.wrap(type);
		TermToObjectConverter objectAdapter = annotatedContext.getTermToObjectConverter();
		return typeWrapper.getRawClass().cast(objectAdapter.adapt(term));
	}
	
	
	/*
	 * This method transform a term into a logic object of a specified class using the information present in a LObjectGenericDescription object.
	 */
	protected Object adaptToObjectFromDescription(Term term, Type type, AnnotatedElementAdaptationContext annotatedContext) {
		LogicObjectDescriptor logicObjectDescription = annotatedContext.getLogicObjectDescription();
		TypeWrapper typeWrapper = TypeWrapper.wrap(type);
		try {
			Object lObject = null;
			if(typeWrapper.isRawClassAssignableFrom(annotatedContext.getContextClass())) {
                lObject = newLogicObject(annotatedContext.getContextClass());
            } else {
            	lObject = newLogicObject(typeWrapper.getRawClass());
            }
			String argsList = logicObjectDescription.argsList();
			if(argsList != null && !argsList.isEmpty())
				LogicObject.setPropertiesArray(lObject, argsList, term);
			
			LogicObject.setPropertiesFromTermArgs(lObject, logicObjectDescription.args(), term);
			
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
		TypeWrapper typeWrapper = TypeWrapper.wrap(type);
		Class logicClass = LogicObjects.findLogicClass(term);  
		/*
		 * find out if the term could be mapped to a logic object
		 * the additional type compatibilities verifications are necessary since the fact that the term 'could' be converted to a logic object does not mean that it 'should'
		 */
		if ( logicClass != null && (typeWrapper.isRawClassAssignableFrom(logicClass) || logicClass.isAssignableFrom(typeWrapper.getRawClass())) ) { 
			annotatedContext = new ClassAdaptationContext(logicClass);
		} else {
			if(LogicClass.findLogicClass(typeWrapper.getRawClass()) != null)
				annotatedContext = new ClassAdaptationContext(typeWrapper.getRawClass()); 
		}
		return annotatedContext;
	}
	

}
