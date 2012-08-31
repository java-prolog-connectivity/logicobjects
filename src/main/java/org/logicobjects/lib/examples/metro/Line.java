package org.logicobjects.lib.examples.metro;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LMethod;




@LObject(args = {"name"})
public abstract class Line { 
	
	public Line() {}
	
	public Line(String name) {
		this.name = name;
	}
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {return name;}
	
	@LMethod
	public abstract boolean connects(Station s1, Station s2);
	
	@LMethod(name = "connects", args = {"_", "_"})
	public abstract int segments();
	
	
	

	
}
