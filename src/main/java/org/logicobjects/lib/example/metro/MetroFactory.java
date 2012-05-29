package org.logicobjects.lib.example.metro;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.core.LogicObjectFactory;

@LObject
public abstract class MetroFactory {

	public static MetroFactory getDefault() {
		return LogicObjectFactory.getDefault().create(MetroFactory.class);
	}
	
	@LSolution
	public abstract Station station(String name);
	
	@LSolution
	public abstract Line line(String name);

}
