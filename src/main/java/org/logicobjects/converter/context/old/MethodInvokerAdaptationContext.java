package org.logicobjects.converter.context.old;

import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LTermConverter;
import org.logicobjects.core.LogicClass;
import org.logicobjects.descriptor.LogicObjectDescriptor;

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
	public LTermConverter getObjectToTermConverterAnnotation() {
		return null;
	}
	
	@Override
	public LogicObjectDescriptor getLogicObjectDescription() {
		Class invokerClass = LogicClass.findDelegationObjectClass(getContextClass());
		if(invokerClass != null) {
			LDelegationObject aLDelegationObject = (LDelegationObject) invokerClass.getAnnotation(LDelegationObject.class);
			return LogicObjectDescriptor.create(aLDelegationObject);
		} else {
			return super.getLogicObjectDescription();
		}
	}

}
