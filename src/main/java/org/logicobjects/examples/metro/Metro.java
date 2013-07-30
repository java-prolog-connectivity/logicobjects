package org.logicobjects.examples.metro;

import java.util.List;

import org.logicobjects.annotation.method.LComposition;
import org.logicobjects.annotation.method.LMethod;


public abstract class Metro implements IMetro {
	
	@LComposition 
	@LMethod(name="line", args={"L"})
	public abstract List<ILine> lines();
	
	public abstract ILine line(String s);

}
