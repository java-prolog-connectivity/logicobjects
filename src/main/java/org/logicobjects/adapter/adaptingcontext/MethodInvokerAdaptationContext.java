package org.logicobjects.adapter.adaptingcontext;

import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.core.LogicObjectClass;

/**
 * This class represents the context of a logic method invocation
 * The Java object needs to be transformed to a Term first in order to execute a logic method
 * 
 * @author scastro
 *
 */
public class MethodInvokerAdaptationContext extends ClassAdaptationContext {

	public MethodInvokerAdaptationContext(Class clazz) {
		super(clazz);
	}
	
	/**
	 * In this context term adapters annotations should be ignored
	 */
	@Override
	public LTermAdapter getObjectToTermAdapterAnnotation() {
		return null;
	}
	
	@Override
	public LogicObjectDescriptor getLogicObjectDescription() {
		Class invokerClass = LogicObjectClass.findDelegationObjectClass(getContextClass());
		if(invokerClass != null) {
			LDelegationObject aLDelegationObject = (LDelegationObject) invokerClass.getAnnotation(LDelegationObject.class);
			return LogicObjectDescriptor.create(aLDelegationObject);
		} else {
			return super.getLogicObjectDescription();
		}
	}

}
