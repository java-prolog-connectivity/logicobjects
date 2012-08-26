package org.logicobjects.adapter.adaptingcontext;

import jpl.Term;

import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LObject;
import org.logicobjects.core.LogicClass;

/**
 * This class represents the context of a logic method invocation
 * The Java object needs to be transformed to a Term first in order to execute a logic method
 * 
 * @author scastro
 *
 */
public class MethodInvokerContext extends ClassAdaptingContext {

	public MethodInvokerContext(Class clazz) {
		super(clazz);
	}
	

	
	@Override
	public boolean canAdaptToTerm() {
		return hasLogicObjectDescription();
	}
	
	@Override
	public Term adaptToTerm(Object object) {
		return adaptToTermWithDescription(object);
	}
	
	@Override
	public LObjectGenericDescription getLogicObjectDescription() {
		Class invokerClass = LogicClass.findDelegationObjectClass(getContextClass());
		if(invokerClass == null)
			return null;
		LDelegationObject aLDelegationObject = (LDelegationObject) invokerClass.getAnnotation(LDelegationObject.class);
		return LObjectGenericDescription.create(aLDelegationObject);
	}

}
