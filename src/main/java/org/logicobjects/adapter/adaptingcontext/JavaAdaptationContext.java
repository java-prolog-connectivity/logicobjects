package org.logicobjects.adapter.adaptingcontext;


/**
 * This class help to guide the transformation between java artefacts and logic terms
 * For example, it answers questions such as: is the term going to be assigned to a field ? or the term is the result of a method invocation ?
 * @author scastro
 *
 */
public abstract class JavaAdaptationContext extends AdaptationContext {

	public boolean hasObjectToTermAdapter() {
		return getObjectToTermAdapter() != null;
	}

	public boolean hasTermToObjectAdapter() {
		return getTermToObjectAdapter() != null;
	}

	public boolean hasLogicObjectDescription() {
		return getLogicObjectDescription() != null;
	}
	
	public abstract Class getGuidingClass();

	public abstract Object getObjectToTermAdapter();
	
	public abstract Object getTermToObjectAdapter();
	
	public abstract Object getLogicObjectDescription();
	
}
