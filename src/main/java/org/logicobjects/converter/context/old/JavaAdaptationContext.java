package org.logicobjects.converter.context.old;


/**
 * This class help to guide the transformation between java artefacts and logic terms
 * For example, it answers questions such as: is the term going to be assigned to a field ? or the term is the result of a method invocation ?
 * @author scastro
 *
 */
public abstract class JavaAdaptationContext extends AdaptationContext {

	public boolean hasObjectToTermConverter() {
		return getObjectToTermConverter() != null;
	}

	public boolean hasTermToObjectConverter() {
		return getTermToObjectConverter() != null;
	}

	public boolean hasLogicObjectDescription() {
		return getLogicObjectDescription() != null;
	}
	
	public abstract Class getGuidingClass();

	public abstract Object getObjectToTermConverter();
	
	public abstract Object getTermToObjectConverter();
	
	public abstract Object getLogicObjectDescription();
	
}
