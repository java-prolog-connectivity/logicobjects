package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Type;
import jpl.Term;
import org.logicobjects.adapter.LogicObjectAdapter;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.core.LogicObject;
import org.reflectiveutils.AbstractTypeWrapper;

public abstract class AdaptingContext {
	
	protected static TermToObjectAdapter getTermToObjectAdapter(LObjectAdapter annotation) {
		try {
			TermToObjectAdapter objectAdapter = (TermToObjectAdapter)annotation.adapter().newInstance();
			objectAdapter.setParameters(annotation.args());
			return objectAdapter;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected static ObjectToTermAdapter getObjectToTermAdapter(LTermAdapter annotation) {
		try {
			ObjectToTermAdapter objectAdapter = (ObjectToTermAdapter)annotation.adapter().newInstance();
			objectAdapter.setParameters(annotation.args());
			return objectAdapter;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected abstract LObject getLogicObjectAnnotation();
	
	protected abstract LObjectAdapter getTermToObjectAdapterAnnotation();

	protected abstract LTermAdapter getObjectToTermAdapterAnnotation();
	
	protected boolean hasLogicObjectAnnotation() {
		return getLogicObjectAnnotation() != null;
	}
	
	public abstract Object getContext();
	
	public abstract String infereLogicObjectName();
	
	public boolean hasTermToObjectAdapter() {
		return getTermToObjectAdapterAnnotation() != null;
	}
	
	public boolean hasObjectToTermAdapter() {
		return getObjectToTermAdapterAnnotation() != null;
	}
	
	public TermToObjectAdapter getTermToObjectAdapter() {
		return getTermToObjectAdapter(getTermToObjectAdapterAnnotation());
	}

	public ObjectToTermAdapter getObjectToTermAdapter() {
		return getObjectToTermAdapter(getObjectToTermAdapterAnnotation());
	}
	
	public boolean canAdaptToTerm() {
		if (hasObjectToTermAdapter())
			return true;
		return hasLogicObjectAnnotation();
	}
	
	public Term adaptToTerm(Object object) {
		LTermAdapter termAdapterAnnotation = getObjectToTermAdapterAnnotation();
		if(termAdapterAnnotation != null) {
			Class termAdapterClazz = termAdapterAnnotation.adapter();
			//if(AbstractTypeWrapper.wrap(Adapter.fromType(termAdapterClazz)).asClass().isAssignableFrom(object.getClass())) //if the wrapper is compatible with the object use it
				return ObjectToTermAdapter.create(termAdapterAnnotation).adapt(object);
		}
		else { //in the current implementation, an Adapter annotation overrides any LogicObjectAnnotation (even if it was not used because the adapter is not compatible with the object type)
			LObject logicObjectAnnotation = getLogicObjectAnnotation();
			String logicObjectName = logicObjectAnnotation.name();
			if(logicObjectName.isEmpty())
				logicObjectName = infereLogicObjectName();
			return new LogicObjectAdapter().asLogicObject(object, logicObjectName, logicObjectAnnotation.params()).asTerm();
		}
	}
	

	
	public boolean canAdaptToLObject() {
		if(hasTermToObjectAdapter())
			return true;
		return hasLogicObjectAnnotation();
	}
	

	public Object adaptToLObject(Term term, Type type) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		
		if(hasTermToObjectAdapter()) {
			TermToObjectAdapter objectAdapter = getTermToObjectAdapter();
			return typeWrapper.asClass().cast(objectAdapter.adapt(term));
		} else {
			return createLObjectFromAnnotation(term, type, getLogicObjectAnnotation());
		}
	}
	
	/*
	 * This method transform a term in a logic object of a specified class using the information present in a logic object annotation.
	 * The annotation is not necessarily found in the instantiating class, but in a super class
	 */
	protected Object createLObjectFromAnnotation(Term term, Type type, LObject lObjectAnnotation) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		try {
			Object lObject = typeWrapper.asClass().newInstance();
			LogicObject.setParams(lObject, term, lObjectAnnotation.params());
			return lObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	


}
