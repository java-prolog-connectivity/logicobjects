package org.logicobjects.adapter;

import java.lang.reflect.Field;

import jpl.Term;

import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.logicobjects.adapter.adaptingcontext.AnnotatedElementAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.BeanPropertyAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.ClassAdaptationContext;
import org.logicobjects.adapter.adaptingcontext.AbstractLogicObjectDescriptor;
import org.logicobjects.core.LogicObject;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.ReflectionUtil;

public class AnnotatedObjectToTermAdapter<From> extends Adapter<From, Term> {

	@Override
	public Term adapt(From object) {
		return adapt(object, null);
	}
	
	
	public Term adapt(From object, AdaptationContext context) {
		AnnotatedElementAdaptationContext annotatedContext;
		if(context != null) {
			if(understandsContext(context))
				annotatedContext = (AnnotatedElementAdaptationContext)context;
			else
				throw new UnrecognizedAdaptationContextException(this.getClass(), context);
		} else {
			annotatedContext = new ClassAdaptationContext(object.getClass()); //the current context is null, then create default context
		}
		if(annotatedContext!=null) {
			if(annotatedContext.hasObjectToTermAdapter()) { //first check if there is an explicit adapter, in the current implementation, an Adapter annotation overrides any method invoker description
				return adaptToTermWithAdapter(object, annotatedContext);
			}
			else if(annotatedContext.hasLogicObjectDescription()) { 
				return adaptToTermFromDescription(object, annotatedContext);
			}
		}
		throw new IncompatibleAdapterException(this.getClass(), object);
	}
	
	protected Term adaptToTermWithAdapter(From object, AnnotatedElementAdaptationContext annotatedContext) {
		ObjectToTermAdapter termAdapter = annotatedContext.getObjectToTermAdapter();
		return termAdapter.adapt(object);
	}
	
	protected Term adaptToTermFromDescription(From object, AnnotatedElementAdaptationContext annotatedContext) {
		AbstractLogicObjectDescriptor logicObjectDescription = annotatedContext.getLogicObjectDescription();
		String logicObjectName = logicObjectDescription.name();
		if(logicObjectName.isEmpty())
			logicObjectName = infereLogicObjectName(annotatedContext);
		
		Term[] arguments;
		String argsListPropertyName = logicObjectDescription.argsList();
		if(argsListPropertyName != null && !argsListPropertyName.isEmpty()) {
			BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(object.getClass(), argsListPropertyName);
			Object[] objects = (Object[]) ReflectionUtil.getProperty(object, argsListPropertyName, adaptationContext.getGuidingClass());
			//Field field = ReflectionUtil.getField(object, argsList);
			arguments = new ObjectToTermAdapter().adaptObjects(objects, adaptationContext);
		} else {
			arguments = LogicObject.propertiesAsTerms(object, logicObjectDescription.args());
		}
		return new LogicObject(logicObjectName, arguments).asTerm();
	}
	
	
	

	/**
	 * In case the name is not explicitly specified (e.g., with an annotation), it will have to be inferred
	 * It can be inferred from a class name (if the transformation context is a class instance to a logic object), from a field name (if the context is the transformation of a field), etc
	 * Different context override this method to specify how they infer the logic name of the object
	 * @return
	 */
	public String infereLogicObjectName(AnnotatedElementAdaptationContext annotatedContext) {
		return LogicUtil.javaClassNameToProlog(annotatedContext.getGuidingClass().getSimpleName());
	}
	
	

	public static boolean understandsContext(AdaptationContext context) {
		return context != null && context instanceof AnnotatedElementAdaptationContext;
	}
	
	


}
