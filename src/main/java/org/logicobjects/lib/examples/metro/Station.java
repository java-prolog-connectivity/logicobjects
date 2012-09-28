package org.logicobjects.lib.examples.metro;

import org.logicobjects.annotation.LObject;

@LObject(args = {"name"})
public abstract class Station implements IStation {

	String name;
	
	public Station() {}
	public Station(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {return name;}

}
