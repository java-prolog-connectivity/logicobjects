package org.logicobjects.term;

public class LException extends RuntimeException {

	public LException() {}
	
	public LException(String s) {
		super(s);
	}
	
	public LException(String s, Exception cause) {
		super(s, cause);
	}
}
