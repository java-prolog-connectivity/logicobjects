package org.logicobjects.lib.examples.metro;

import org.logicobjects.annotation.method.LMethod;

public interface ILine {

	//public String getName(); //not a logic method, just to facilitate testing
	
	//@LMethod
	public boolean connects(IStation s1, IStation s2);
	
	@LMethod(name = "connects", args = {"_", "_"})
	public int segments();
	
}
