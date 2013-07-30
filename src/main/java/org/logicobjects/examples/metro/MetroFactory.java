package org.logicobjects.examples.metro;

import static org.logicobjects.LogicObjects.newLogicObject;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LExpression;
import org.logicobjects.annotation.method.LSolution;

@LObject
public abstract class MetroFactory implements IMetroFactory {

	public static MetroFactory getDefault() {
		return newLogicObject(MetroFactory.class);
	}

	@LExpression
	public abstract IMetro metro();
	
	@LSolution("station($1)")
	@LExpression
	public abstract IStation station(String name);
	
	@LSolution("line($1)")
	@LExpression
	public abstract ILine line(String name);

}
