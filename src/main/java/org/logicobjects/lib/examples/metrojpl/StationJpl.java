package org.logicobjects.lib.examples.metrojpl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;
import jpl.Variable;

public class StationJpl {

	private String name;
	
	public StationJpl(String name) {
		this.name = name;
	}

	public Term asTerm() {
		return new Compound("station", new Term[] {new Atom(name)});
	}
	
	public static StationJpl create(Term term) {
		Compound stationTerm = (Compound)term;
		String lineName = stationTerm.arg(1).name();
		return new StationJpl(lineName);
	}


	public boolean connected(StationJpl station) {
		Term message = new Compound("connected", new Term[]{station.asTerm()});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.hasSolution();
	}


	public int numberConnections() {
		Term message = new Compound("connected", new Term[]{new Variable("_")});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.allSolutions().length;
	}


	public StationJpl connected(LineJpl line) {
		String stationVarName = "Station";
		Term message = new Compound("connected", new Term[]{new Variable(stationVarName), line.asTerm()});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		Hashtable<String, Term> solution = query.oneSolution();
		Term term = solution.get(stationVarName);
		return create(term);
	}


	public List<StationJpl> connected() {
		String stationVarName = "Station";
		Term message = new Compound("connected", new Term[]{new Variable(stationVarName)});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		Hashtable<String, Term> solutions[] = query.allSolutions();
		List<StationJpl> stations = new ArrayList<StationJpl>();
		for(Hashtable<String, Term> solution : solutions) {
			Term term = solution.get(stationVarName);
			stations.add(create(term));
		}
		return stations;
	}


	public boolean nearby(StationJpl station) {
		// TODO Auto-generated method stub
		return false;
	}


	public int numberNearbyStations() {
		// TODO Auto-generated method stub
		return 0;
	}


	public List<StationJpl> nearby() {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean reachable(StationJpl station) {
		// TODO Auto-generated method stub
		return false;
	}


	public int numberReachableStations() {
		// TODO Auto-generated method stub
		return 0;
	}


	public List<StationJpl> intermediateStations(StationJpl station) {
		// TODO Auto-generated method stub
		return null;
	}
}
