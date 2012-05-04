package org.logicobjects.lib.example.metro;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LSolution;

@LObject(params = {"name"})
public abstract class Line {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {return name;}
	
	@LMethod(name = "connects", params = {"_", "_"})
	public abstract int segments();
	
	
}
