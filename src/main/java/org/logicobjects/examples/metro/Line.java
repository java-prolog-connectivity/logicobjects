package org.logicobjects.examples.metro;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LMethod;


@LObject(args = {"id"})
public abstract class Line implements ILine { 
	
	public abstract boolean connects(IStation s1, IStation s2);
	
	@LMethod(name = "connects", args = {"_", "_"})
	public abstract int segments();
}
