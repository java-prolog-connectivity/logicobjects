package org.logicobjects.lib.examples.metro;

import org.logicobjects.annotation.LObject;


@LObject(args = {"name"})
public abstract class Line implements ILine { 

	public Line() {}
	
	public Line(String name) {
		this.name = name;
	}
	
	String name;

	@Override
	public String toString() {return name;}

}
