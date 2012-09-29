package org.logicobjects.lib.examples.metrojpl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;
import jpl.Util;
import jpl.Variable;

import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IStation;

public class StationJpl implements IStation {

	private String name;
	
	public StationJpl(String name) {
		this.name = name;
	}

	//@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {return name;}
	
	public Term asTerm() {
		return new Compound("station", new Term[] {new Atom(name)});
	}
	
	public static IStation create(Term term) {
		Compound stationTerm = (Compound)term;
		String lineName = stationTerm.arg(1).name();
		return new StationJpl(lineName);
	}


	public boolean connected(IStation station) {
		Term[] arguments = new Term[]{((StationJpl) station).asTerm()};
		Term message = new Compound("connected", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.hasSolution();
	}


	public int numberConnections() {
		Term[] arguments = new Term[]{new Variable("_")};
		Term message = new Compound("connected", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.allSolutions().length;
	}


	public IStation connected(ILine line) {
		IStation connectedStation = null;
		String stationVarName = "Station";
		Term[] arguments = new Term[]{new Variable(stationVarName), ((LineJpl) line).asTerm()};
		Term message = new Compound("connected", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		Hashtable<String, Term> solution = query.oneSolution();
		if(solution != null) {
			Term term = solution.get(stationVarName);
			connectedStation = create(term);
		}
		return connectedStation;
	}


	public List<IStation> connected() {
		String stationVarName = "Station";
		Term[] arguments = new Term[]{new Variable(stationVarName)};
		Term message = new Compound("connected", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		Hashtable<String, Term> solutions[] = query.allSolutions();
		List<IStation> stations = new ArrayList<IStation>();
		for(Hashtable<String, Term> solution : solutions) {
			Term term = solution.get(stationVarName);
			stations.add(create(term));
		}
		return stations;
	}


	public boolean nearby(IStation station) {
		Term[] arguments = new Term[]{((StationJpl) station).asTerm()};
		Term message = new Compound("nearby", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.hasSolution();
	}


	public int numberNearbyStations() {
		Term[] arguments = new Term[]{new Variable("_")};
		Term message = new Compound("nearby", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.allSolutions().length;
	}


	public List<IStation> nearby() {
		String stationVarName = "Station";
		Term[] arguments = new Term[]{new Variable(stationVarName)};
		Term message = new Compound("nearby", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		Hashtable<String, Term> solutions[] = query.allSolutions();
		List<IStation> stations = new ArrayList<IStation>();
		for(Hashtable<String, Term> solution : solutions) {
			Term term = solution.get(stationVarName);
			stations.add(create(term));
		}
		return stations;
	}


	public boolean reachable(IStation station) {
		Term[] arguments = new Term[]{((StationJpl) station).asTerm()};
		Term message = new Compound("reachable", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.hasSolution();
	}


	public int numberReachableStations() {
		Term[] arguments = new Term[]{new Variable("_")};
		Term message = new Compound("reachable", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.allSolutions().length;
	}


	public List<IStation> intermediateStations(IStation station) {
		List<IStation> intermediateStations = null;
		String stationsVarName = "Stations";
		Term[] arguments = new Term[]{((StationJpl) station).asTerm(), new Variable(stationsVarName)};
		Term message = new Compound("reachable", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		Hashtable<String, Term> solution = query.oneSolution();
		if(solution != null) {
			Term solutionTerm = solution.get(stationsVarName);
			Term[] terms = Util.listToTermArray(solutionTerm);
			List<IStation> stations = new ArrayList<IStation>();
			for(Term term : terms) {
				stations.add(create(term));
			}
			intermediateStations = stations;
		}
		return intermediateStations;
	}
}
