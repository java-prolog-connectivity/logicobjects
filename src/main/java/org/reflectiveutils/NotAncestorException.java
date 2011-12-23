package org.reflectiveutils;


public class NotAncestorException extends RuntimeException {

	private Class ancestor;
	private Class descendant;

	public NotAncestorException(Class ancestor, Class descendant) {
		this.ancestor = ancestor;
		this.descendant = descendant;
	}

	@Override
	public String getMessage() {
		return "Class "+ancestor.getName()+" is not an ancestor of "+descendant.getName();
	}
	
}
