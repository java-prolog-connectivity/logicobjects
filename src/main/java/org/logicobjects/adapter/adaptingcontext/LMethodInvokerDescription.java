package org.logicobjects.adapter.adaptingcontext;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LDelegationObject;


/**
 * This class is a workaround to the problem than in Java annotations cannot extend other annotations or implement an interface
 * Since @LObject and @LDelegationObject are quite similar, this class and its subclasses define methods to access common functionality from these two annotations
 * (without having to be aware which of the two ones are the used ones behind the curtains)
 * @author sergioc78
 *
 */
public abstract class LMethodInvokerDescription {

	public abstract String name(); 
	public abstract String[] params(); 
	public abstract String[] imports();
	public abstract String[] modules();
	public abstract boolean automaticImport();

	public static LMethodInvokerDescription create(Class clazz) {
		LObject aLObject = (LObject) clazz.getAnnotation(LObject.class);
		if(aLObject != null)
			return create(aLObject);
		LDelegationObject aLDelegationObject = (LDelegationObject) clazz.getAnnotation(LDelegationObject.class);
		if(aLDelegationObject != null)
			return create(aLDelegationObject);
		throw new RuntimeException("Impossible to create Method invoker description from class: " + clazz.getSimpleName());
	}
	
	public static LMethodInvokerDescription create(LObject aLObject) {
		return new LObjectDescription(aLObject);
	}
	
	public static LMethodInvokerDescription create(LDelegationObject aLDelegationObject) {
		return new LDelegationObjectDescription(aLDelegationObject);
	}
	
	public static class LObjectDescription extends LMethodInvokerDescription {
		LObject aLObject;

		public LObjectDescription(LObject aLObject) {
			this.aLObject = aLObject;
		}
		
		public String name() {
			return aLObject.name();
		}

		public String[] params() {
			return aLObject.args();
		}

		public String[] imports() {
			return aLObject.imports();
		}

		public String[] modules() {
			return aLObject.modules();
		}

		public boolean automaticImport() {
			return aLObject.automaticImport();
		}

	}
	
	
	
	
	public static class LDelegationObjectDescription extends LMethodInvokerDescription {
		LDelegationObject aLDelegationObject;

		public LDelegationObjectDescription(LDelegationObject aLDelegationObject) {
			this.aLDelegationObject = aLDelegationObject;
		}
		
		public String name() {
			return aLDelegationObject.name();
		}

		public String[] params() {
			return aLDelegationObject.params();
		}

		public String[] imports() {
			return aLDelegationObject.imports();
		}

		public String[] modules() {
			return aLDelegationObject.modules();
		}

		public boolean automaticImport() {
			return aLDelegationObject.automaticImport();
		}

	}
	
	
}
