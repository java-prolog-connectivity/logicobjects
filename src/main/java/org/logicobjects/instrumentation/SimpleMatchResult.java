package org.logicobjects.instrumentation;

import java.util.regex.MatchResult;

public class SimpleMatchResult implements MatchResult {

	private String expression;
	private int start;
	private int end;
	private String group;
	
	public SimpleMatchResult(String expression, int start, int end) {
		this.expression = expression;
		this.start = start;
		this.end = end;
	}
	
	@Override
	public int start() {
		return start;
	}
	
	@Override
	public int end() {
		return end;
	}

	@Override
	public String group() {
		return expression.substring(start, end+1);
	}
	
	@Override
	public int start(int arg0) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int end(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String group(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int groupCount() {
		throw new UnsupportedOperationException();
	}

}
