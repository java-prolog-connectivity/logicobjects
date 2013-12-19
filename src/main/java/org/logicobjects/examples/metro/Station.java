package org.logicobjects.examples.metro;

import java.util.List;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LComposition;
import org.logicobjects.annotation.method.LMethod;

@LObject(args = {"id"})
public abstract class Station implements IStation {
	
	public abstract boolean connected(IStation station);
	
	@LMethod(name = "connected", args = {"_"})
	public abstract int numberConnections();

	@LMethod(args = {"LSolution", "$1"})
	public abstract IStation connected(ILine line);

	@LComposition 
	@LMethod(args = {"LSolution"})
	public abstract List<IStation> connected();

	public abstract boolean nearby(IStation station);
	
	@LMethod(name = "nearby", args = {"_"})
	public abstract int numberNearbyStations();

	@LComposition
	@LMethod(args = {"LSolution"})
	public abstract List<IStation> nearby();

	public abstract boolean reachable(IStation station);
	
	@LMethod(name = "reachable", args = {"_"})
	public abstract int numberReachableStations();

	@LMethod(name = "reachable", args = {"$1", "LSolution"})
	public abstract List<IStation> intermediateStations(IStation station);
}
