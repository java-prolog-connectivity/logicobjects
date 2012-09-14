package org.logicobjects.lib.examples.metro;

import org.logicobjects.annotation.method.LExpression;
import org.logicobjects.annotation.method.LSolution;

public interface IMetroFactory {
	@LSolution("station($1)")
	@LExpression
	public abstract IStation station(String name);
	
	@LSolution("line($1)")
	@LExpression
	public abstract ILine line(String name);
}
