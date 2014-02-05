package org.logicobjects.converter.old;


import java.util.Arrays;
import java.util.List;

import org.jpc.term.Term;
import org.jpc.util.ParadigmLeakUtil;
import org.logicobjects.converter.IncompatibleAdapterException;
import org.logicobjects.converter.context.old.AdaptationContext;
import org.logicobjects.converter.context.old.AnnotatedElementAdaptationContext;
import org.logicobjects.converter.context.old.BeanPropertyAdaptationContext;
import org.logicobjects.converter.context.old.ClassAdaptationContext;
import org.logicobjects.converter.descriptor.LogicObjectDescriptor;
import org.logicobjects.core.LogicObject;
import org.logicobjects.methodadapter.LogicAdapter;
import org.minitoolbox.reflection.BeansUtil;

public class AnnotatedObjectToTermConverter<From> extends LogicAdapter<From, Term> {

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
		if(annotatedContext.hasObjectToTermConverter()) { //first check if there is an explicit adapter, in the current implementation, an Adapter annotation overrides any method invoker description
			return adaptToTermWithAdapter(object, annotatedContext);
		}
		else if(annotatedContext.hasLogicObjectDescription()) { 
			return adaptToTermFromDescription(object, annotatedContext);
		}
		throw new IncompatibleAdapterException(this.getClass(), object);
	}
	
	protected Term adaptToTermWithAdapter(From object, AnnotatedElementAdaptationContext annotatedContext) {
		ObjectToTermConverter termAdapter = annotatedContext.getObjectToTermConverter();
		return termAdapter.adapt(object);
	}
	
	protected Term adaptToTermFromDescription(From object, AnnotatedElementAdaptationContext annotatedContext) {
		LogicObjectDescriptor logicObjectDescription = annotatedContext.getLogicObjectDescription();
		String logicObjectName = logicObjectDescription.name();
		if(logicObjectName.isEmpty())
			logicObjectName = infereLogicObjectName(annotatedContext);
		
		

		List<Term> arguments;
		String argsListPropertyName = logicObjectDescription.argsList();
		if(argsListPropertyName != null && !argsListPropertyName.isEmpty()) {
			BeanPropertyAdaptationContext adaptationContext = new BeanPropertyAdaptationContext(object.getClass(), argsListPropertyName);
			Object argsListObject = BeansUtil.getProperty(object, argsListPropertyName, adaptationContext.getGuidingClass());
			List argsList = null;
			if(List.class.isAssignableFrom(argsListObject.getClass()))
				argsList = (List) argsListObject;
			else if(Object[].class.isAssignableFrom(argsListObject.getClass()))
				argsList = Arrays.asList((Object[])argsListObject);
			else
				throw new RuntimeException("Property " + argsListPropertyName + " is neither a list nor an array");
			arguments = new ObjectToTermConverter().adaptObjects(argsList, adaptationContext);
		} else {
			arguments = LogicObject.propertiesAsTerms(object, logicObjectDescription.args());
		}
		return new LogicObject(logicObjectName, arguments).asTerm();
	}

	/**
	 * In case the id is not explicitly specified (e.g., with an annotation), it will have to be inferred
	 * It can be inferred from a class id (if the transformation context is a class instance to a logic object), from a field id (if the context is the transformation of a field), etc
	 * Different context override this method to specify how they infer the logic id of the object
	 * @return
	 */
	public String infereLogicObjectName(AnnotatedElementAdaptationContext annotatedContext) {
		return ParadigmLeakUtil.javaClassNameToProlog(annotatedContext.getGuidingClass().getSimpleName());
	}
	
	

	public static boolean understandsContext(AdaptationContext context) {
		return context != null && context instanceof AnnotatedElementAdaptationContext;
	}
	
	


}
