package org.logicobjects.lib.examples.metro;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LExpression;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.core.LogicObjectFactory;

@LObject
public abstract class MetroFactory {

	public static MetroFactory getDefault() {
		return LogicObjectFactory.getDefault().create(MetroFactory.class);
	}
	
	@LExpression
	public abstract Station station(String name);
	
	@LExpression
	public abstract Line line(String name);

}
