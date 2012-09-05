package org.logicobjects.lib.examples.metro;

import org.logicobjects.annotation.method.LExpression;

public interface IMetroFactory {

	@LExpression
	public abstract IStation station(String name);
	
	@LExpression
	public abstract ILine line(String name);
	
}
