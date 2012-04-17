package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Type;

import jpl.Term;

import org.logicobjects.adapter.LogicObjectAdapter;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.core.LogicObject;
import org.logicobjects.core.LogicObjectFactory;
import org.reflectiveutils.AbstractTypeWrapper;

public abstract class AdaptingContext {

	protected abstract LMethodInvokerDescription getMethodInvokerDescription();

	protected abstract TermToObjectAdapter getTermToObjectAdapter();

	public abstract ObjectToTermAdapter getObjectToTermAdapter();

	public abstract String infereLogicObjectName();
	


	public boolean hasMethodInvokerDescription() {
		return getMethodInvokerDescription() != null;
	}
	
	public boolean hasObjectToTermAdapter() {
		return getObjectToTermAdapter() != null;
	}

	public boolean hasTermToObjectAdapter() {
		return getTermToObjectAdapter() != null;
	}
	
	public boolean canAdaptToTerm() {
		if (hasObjectToTermAdapter())
			return true;
		return hasMethodInvokerDescription();
	}
	
	protected Term adaptToTermWithAdapter(Object object) {
		ObjectToTermAdapter termAdapter = getObjectToTermAdapter();
		return termAdapter.adapt(object);
	}
	
	protected Term adaptToTermWithDescription(Object object) {
		LMethodInvokerDescription methodInvokerDescription = getMethodInvokerDescription();
		String logicObjectName = methodInvokerDescription.name();
		if(logicObjectName.isEmpty())
			logicObjectName = infereLogicObjectName();
		return new LogicObjectAdapter().asLogicObject(object, logicObjectName, methodInvokerDescription.params()).asTerm();
	}
	
	public Term adaptToTerm(Object object) {
		if(hasObjectToTermAdapter()) {
			return adaptToTermWithAdapter(object);
		}
		else { //in the current implementation, an Adapter annotation overrides any method invoker description
			return adaptToTermWithDescription(object);
		}
	}

	public boolean canAdaptToLObject() {
		if(hasTermToObjectAdapter())
			return true;
		return hasMethodInvokerDescription();
	}
	
	protected Object adaptToObjectFromAdapter(Term term, Type type) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		TermToObjectAdapter objectAdapter = getTermToObjectAdapter();
		return typeWrapper.asClass().cast(objectAdapter.adapt(term));
	}
	/*
	 * This method transform a term in a logic object of a specified class using the information present in a LMethodInvokerDescription object.
	 */
	protected Object adaptToObjectFromDescription(Term term, Type type, LMethodInvokerDescription lMethodInvokerDescription) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		try {
			Object lObject = LogicObjectFactory.getDefault().create(typeWrapper.asClass());
			LogicObject.setParams(lObject, term, lMethodInvokerDescription.params());
			return lObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object adaptToLObject(Term term, Type type) {
		if(hasTermToObjectAdapter()) {
			return adaptToObjectFromAdapter(term, type);
		} else {
			return adaptToObjectFromDescription(term, type, getMethodInvokerDescription());
		}
	}
	
	

}
