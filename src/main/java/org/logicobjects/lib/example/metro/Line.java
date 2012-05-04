package org.logicobjects.lib.example.metro;

import org.logicobjects.annotation.LObject;

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
}
