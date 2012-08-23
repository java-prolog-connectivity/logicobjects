package org.reflectiveutils;

import org.reflectiveutils.wrappertype.AbstractTypeWrapper;


public class NotAncestorException extends RuntimeException {

	private Class ancestor;
	private Class descendant;

	public NotAncestorException(Class ancestor, Class descendant) {
		this.ancestor = ancestor;
		this.descendant = descendant;
	}
	
	public NotAncestorException(AbstractTypeWrapper ancestor, AbstractTypeWrapper descendant) {
		this(ancestor.asClass(), descendant.asClass());
	}

	@Override
	public String getMessage() {
		return "Class "+ancestor.getName()+" is not an ancestor of "+descendant.getName();
	}
	
}
