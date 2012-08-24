package org.logicobjects.adapter.adaptingcontext;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LDelegationObject;


/**
 * This class describes a logic object: Currently these are objects annotated with @LObject and @LDelegationObject
 * NOTE: The class is a workaround to the problem than in Java annotations cannot extend other annotations or implement an interface,
 *   since @LObject and @LDelegationObject are quite similar, this class and its subclasses define methods to access common functionality from these two annotations
 *   (without having to be aware which of the two ones are the used ones behind the curtains).
 * @author scastro
 *
 */
public abstract class LObjectGenericDescription {

	public abstract String name(); 
	public abstract String[] args(); 
	public abstract String[] imports();
	public abstract String[] modules();
	public abstract boolean automaticImport();

	public static LObjectGenericDescription create(Class clazz) {
		LObject aLObject = (LObject) clazz.getAnnotation(LObject.class);
		if(aLObject != null)
			return create(aLObject);
		LDelegationObject aLDelegationObject = (LDelegationObject) clazz.getAnnotation(LDelegationObject.class);
		if(aLDelegationObject != null)
			return create(aLDelegationObject);
		throw new RuntimeException("Impossible to create Method invoker description from class: " + clazz.getSimpleName());
	}
	
	public static LObjectGenericDescription create(LObject aLObject) {
		return new LObjectDescription(aLObject);
	}
	
	public static LObjectGenericDescription create(LDelegationObject aLDelegationObject) {
		return new LDelegationObjectDescription(aLDelegationObject);
	}
	
	public static class LObjectDescription extends LObjectGenericDescription {
		LObject aLObject;

		public LObjectDescription(LObject aLObject) {
			this.aLObject = aLObject;
		}
		
		public String name() {
			return aLObject.name();
		}

		public String[] args() {
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
	
	
	
	
	public static class LDelegationObjectDescription extends LObjectGenericDescription {
		LDelegationObject aLDelegationObject;

		public LDelegationObjectDescription(LDelegationObject aLDelegationObject) {
			this.aLDelegationObject = aLDelegationObject;
		}
		
		public String name() {
			return aLDelegationObject.name();
		}

		public String[] args() {
			return aLDelegationObject.args();
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
