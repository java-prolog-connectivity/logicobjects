package org.logicobjects.lib.example.metro;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LSolution;

@LObject(params = {"name"})
public abstract class Station {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*@LSolution("S")
	@LMethod(params = {"S", "$1"})
	public abstract Station connected(Line line);
	*/
	@LSolution("S")
	@LMethod(params = {"S", "$1"})
	public abstract Station connected(String line);
	
	@Override
	public String toString() {return name;}
}
