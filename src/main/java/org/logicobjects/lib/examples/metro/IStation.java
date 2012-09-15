package org.logicobjects.lib.examples.metro;

import java.util.List;

import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.annotation.method.LWrapper;

public interface IStation {

	public String getName(); //not a logic method, just to facilitate testing
	
	//@LMethod
	public abstract boolean connected(IStation station);
	
	@LMethod(name = "connected", args = {"_"})
	public abstract int numberConnections();
	
	//@LSolution("LSolution")
	@LMethod(args = {"LSolution", "$1"})
	public abstract IStation connected(ILine line);

	//@LSolution("LSolution")
	@LWrapper 
	@LMethod(args = {"LSolution"})
	public abstract List<IStation> connected();
	
	//@LMethod
	public abstract boolean nearby(IStation station);
	
	@LMethod(name = "nearby", args = {"_"})
	public abstract int numberNearbyStations();

	//@LSolution("LSolution")
	@LWrapper
	@LMethod(args = {"LSolution"})
	public abstract List<IStation> nearby();

	//@LMethod
	public abstract boolean reachable(IStation station);
	
	@LMethod(name = "reachable", args = {"_"})
	public abstract int numberReachableStations();

	//@LSolution("LSolution")
	@LMethod(name = "reachable", args = {"$1", "LSolution"})
	public abstract List<IStation> intermediateStations(IStation station);

}
