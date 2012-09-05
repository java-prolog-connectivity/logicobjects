package org.logicobjects.lib.examples.metro;

import java.util.List;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.annotation.method.LWrapper;

@LObject(args = {"name"})
public abstract class Station implements IStation {

	private String name;
	
	public Station() {}
	public Station(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {return name;}
	
	



}
