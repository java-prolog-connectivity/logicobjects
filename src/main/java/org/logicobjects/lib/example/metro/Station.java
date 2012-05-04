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

	@Override
	public String toString() {return name;}
	
	

	@LMethod
	public abstract boolean connected(Station station);
	
	@LMethod(name = "connected", params = {"_"})
	public abstract int numberConnections();
	
	@LSolution("S")
	@LMethod(params = {"S", "$1"})
	public abstract Station connected(Line line);

	@LWrapper @LSolution("S")
	@LMethod(params = {"S"})
	public abstract List<Station> connected();
	
	
	
	@LMethod
	public abstract boolean nearby(Station station);
	
	@LMethod(name = "nearby", params = {"_"})
	public abstract int numberNearbyStations();

	@LWrapper @LSolution("S")
	@LMethod(params = {"S"})
	public abstract List<Station> nearby();
	
	
	@LMethod
	public abstract boolean reachable(Station station);
	
	@LMethod(name = "reachable", params = {"_"})
	public abstract int numberReachableStations();

	@LSolution("IntermediateStations")
	@LMethod(name = "reachable", params = {"$1", "IntermediateStations"})
	public abstract List<Station> intermediateStations(Station station);
	

}
