package org.logicobjects.adapter.adaptingcontext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LObject;


/**
 * This class describes a logic object: Currently these are objects annotated with @LObject and @LDelegationObject
 * NOTE: The class is a workaround to the problem than in Java annotations cannot extend other annotations or implement an interface,
 *   since @LObject and @LDelegationObject are quite similar, this class and its subclasses define methods to access common functionality from these two annotations
 *   (without having to be aware which of the two ones are the used ones behind the curtains).
 *   The class also uses List types instead of arrays as the multi-value attributes of the annotations
 * @author scastro
 *
 */
public abstract class AbstractLogicObjectDescriptor {

	public abstract String name();
	
	public abstract List<String> args();
	
	public abstract String argsList();
	
	public abstract List<String> imports();
	
	public abstract List<String> modules();
	
	public abstract boolean automaticImport();
	

	public static AbstractLogicObjectDescriptor create(Class clazz) {
		LObject aLObject = (LObject) clazz.getAnnotation(LObject.class);
		if(aLObject != null)
			return create(aLObject);
		
		LDelegationObject aLDelegationObject = (LDelegationObject) clazz.getAnnotation(LDelegationObject.class);
		if(aLDelegationObject != null)
			return create(aLDelegationObject);
		
		//if(true) throw new RuntimeException("Impossible to create Method invoker description from class: " + clazz.getSimpleName());
		return new DefaultLogicObjectDescriptor();
		
	}
	
	public static AbstractLogicObjectDescriptor create(LObject aLObject) {
		return new LObjectAnnotationDescriptor(aLObject);
	}
	
	public static AbstractLogicObjectDescriptor create(LDelegationObject aLDelegationObject) {
		return new LDelegationObjectAnnotationDescriptor(aLDelegationObject);
	}
	
	
	public static class DefaultLogicObjectDescriptor extends AbstractLogicObjectDescriptor {
		/*
		private Class clazz;
		
		public DefaultLogicObjectDescriptor(Class clazz) {
			this.clazz = clazz;
		}
		*/
		@Override
		public String name() {
			return "";
		}
		
		@Override
		public List<String> args() {
			return Collections.emptyList();
		}
		
		@Override
		public String argsList() {
			return "";
		}
		
		@Override
		public List<String> imports() {
			return Collections.emptyList();
		}
		
		@Override
		public List<String> modules() {
			return Collections.emptyList();
		}
		
		@Override
		public boolean automaticImport() {
			return true;
		}
		
	}
	
	
	static class LObjectAnnotationDescriptor extends AbstractLogicObjectDescriptor {
		
		LObject aLObject;

		public LObjectAnnotationDescriptor(LObject aLObject) {
			assert(aLObject != null);
			this.aLObject = aLObject;
		}
		
		@Override
		public String name() {
			return aLObject.name();
		}

		@Override
		public List<String> args() {
			return Arrays.asList(aLObject.args());
		}

		@Override
		public String argsList() {
			return aLObject.argsList();
		}
		
		@Override
		public List<String> imports() {
			return Arrays.asList(aLObject.imports());
		}

		@Override
		public List<String> modules() {
			return Arrays.asList(aLObject.modules());
		}

		@Override
		public boolean automaticImport() {
			return aLObject.automaticImport();
		}


	}
	
	
	
	
	static class LDelegationObjectAnnotationDescriptor extends AbstractLogicObjectDescriptor {
		LDelegationObject aLDelegationObject;

		public LDelegationObjectAnnotationDescriptor(LDelegationObject aLDelegationObject) {
			assert(aLDelegationObject != null);
			this.aLDelegationObject = aLDelegationObject;
		}
		
		@Override
		public String name() {
			return aLDelegationObject.name();
		}

		@Override
		public List<String> args() {
			return Arrays.asList(aLDelegationObject.args());
		}

		@Override
		public String argsList() {
			return aLDelegationObject.argsList();
		}
		
		@Override
		public List<String> imports() {
			return Arrays.asList(aLDelegationObject.imports());
		}

		@Override
		public List<String> modules() {
			return Arrays.asList(aLDelegationObject.modules());
		}

		@Override
		public boolean automaticImport() {
			return aLDelegationObject.automaticImport();
		}


	}
	
	
}
