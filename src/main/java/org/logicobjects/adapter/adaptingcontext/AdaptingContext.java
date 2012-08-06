package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Type;

import jpl.Term;

import org.logicobjects.adapter.LogicObjectAdapter;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.core.LogicObject;
import org.logicobjects.core.LogicObjectFactory;
import org.reflectiveutils.AbstractTypeWrapper;

/**
 * This class help to guide the transformation to or from a term, given the context of this transformation
 * For example, it this necessary because the term is going to be assigned to a field ? or the term is the result of a method invocation ?
 * @author sergioc78
 *
 */
public abstract class AdaptingContext {

	protected abstract TermToObjectAdapter getTermToObjectAdapter();

	public abstract ObjectToTermAdapter getObjectToTermAdapter();

	protected abstract LObjectGenericDescription getLogicObjectDescription();
	
	public abstract String infereLogicObjectName();

	
	public boolean hasObjectToTermAdapter() {
		return getObjectToTermAdapter() != null;
	}

	public boolean hasTermToObjectAdapter() {
		return getTermToObjectAdapter() != null;
	}

	public boolean hasLogicObjectDescription() {
		return getLogicObjectDescription() != null;
	}
	
	public boolean canAdaptToTerm() {
		if (hasObjectToTermAdapter()) //first check if there is an explicit adapter
			return true;
		return hasLogicObjectDescription(); //if no adapter is found, try to use a method invoker description
	}
	
	protected Term adaptToTermWithAdapter(Object object) {
		ObjectToTermAdapter termAdapter = getObjectToTermAdapter();
		return termAdapter.adapt(object);
	}
	
	protected Term adaptToTermWithDescription(Object object) {
		LObjectGenericDescription logicObjectDescription = getLogicObjectDescription();
		String logicObjectName = logicObjectDescription.name();
		if(logicObjectName.isEmpty())
			logicObjectName = infereLogicObjectName();
		return new LogicObjectAdapter().asLogicObject(object, logicObjectName, logicObjectDescription.args()).asTerm();
	}
	
	public Term adaptToTerm(Object object) {
		if(hasObjectToTermAdapter()) { //first check if there is an explicit adapter
			return adaptToTermWithAdapter(object);
		}
		else { //in the current implementation, an Adapter annotation overrides any method invoker description
			return adaptToTermWithDescription(object);
		}
	}

	public boolean canAdaptToLObject() {
		if(hasTermToObjectAdapter())
			return true;
		return hasLogicObjectDescription();
	}
	
	protected Object adaptToObjectFromAdapter(Term term, Type type) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		TermToObjectAdapter objectAdapter = getTermToObjectAdapter();
		return typeWrapper.asClass().cast(objectAdapter.adapt(term));
	}
	
	/*
	 * This method transform a term in a logic object of a specified class using the information present in a LObjectGenericDescription object.
	 */
	protected Object adaptToObjectFromDescription(Term term, Type type, LObjectGenericDescription lObjectDescription) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		try {
			Object lObject = LogicObjectFactory.getDefault().create(typeWrapper.asClass());
			LogicObject.setProperties(lObject, lObjectDescription.args(), term);
			return lObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object adaptToLObject(Term term, Type type) {
		if(hasTermToObjectAdapter()) {
			return adaptToObjectFromAdapter(term, type);
		} else {
			return adaptToObjectFromDescription(term, type, getLogicObjectDescription());
		}
	}

}
