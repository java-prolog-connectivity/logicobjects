package org.logicobjects.lib.example.metro;

import java.util.List;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.annotation.method.LWrapper;

@LObject(params = {"name"})
public abstract class Station {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@LSolution("S")
	@LMethod(params = {"S", "$1"})
	public abstract Station connected(Line line);

	@LWrapper @LSolution("S")
	@LMethod(params = {"S", "_"})
	public abstract List<Station> connected();
	
	@LMethod(name = "connected", params = {"S", "_"})
	public abstract int numberConnections();


	@Override
	public String toString() {return name;}
}
