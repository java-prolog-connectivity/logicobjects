package org.logicobjects.adapter.adaptingcontext;

import jpl.Term;

import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.core.LogicClass;

public class MethodInvokerContext extends ClassAdaptingContext {

	public MethodInvokerContext(Class clazz) {
		super(clazz);
	}

	@Override
	public Term adaptToTerm(Object object) {
		return adaptToTermWithDescription(object);
	}
	
	@Override
	public LMethodInvokerDescription getMethodInvokerDescription() {
		Class invokerClass = LogicClass.findDelegationObjectClass(getContext());
		if(invokerClass == null)
			return null;
		LDelegationObject aLDelegationObject = (LDelegationObject) invokerClass.getAnnotation(LDelegationObject.class);
		return LMethodInvokerDescription.create(aLDelegationObject);
	}

}
