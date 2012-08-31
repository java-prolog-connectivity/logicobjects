package org.logicobjects.lib.examples.metro;

import java.util.List;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LWrapper;

@LObject
public abstract class Metro {
	
	@LWrapper @LMethod(name="line", args={"L"})
	public abstract List<Line> lines();
	
	@LMethod
	public abstract Line line(String s);
}
