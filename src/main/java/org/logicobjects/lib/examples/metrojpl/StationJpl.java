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

	@Override
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
		Term message = new Compound("connected", new Term[]{((StationJpl) station).asTerm()});
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


	public IStation connected(ILine line) {
		String stationVarName = "Station";
		Term message = new Compound("connected", new Term[]{new Variable(stationVarName), ((LineJpl) line).asTerm()});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		Hashtable<String, Term> solution = query.oneSolution();
		if(solution != null) {
			Term term = solution.get(stationVarName);
			return create(term);
		} else
			return null;
	}


	public List<IStation> connected() {
		String stationVarName = "Station";
		Term message = new Compound("connected", new Term[]{new Variable(stationVarName)});
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
		Term message = new Compound("nearby", new Term[]{((StationJpl) station).asTerm()});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.hasSolution();
	}


	public int numberNearbyStations() {
		Term message = new Compound("nearby", new Term[]{new Variable("_")});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.allSolutions().length;
	}


	public List<IStation> nearby() {
		String stationVarName = "Station";
		Term message = new Compound("nearby", new Term[]{new Variable(stationVarName)});
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
		Term message = new Compound("reachable", new Term[]{((StationJpl) station).asTerm()});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.hasSolution();
	}


	public int numberReachableStations() {
		Term message = new Compound("reachable", new Term[]{new Variable("_")});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.allSolutions().length;
	}


	public List<IStation> intermediateStations(IStation station) {
		String stationsVarName = "Stations";
		Term message = new Compound("reachable", new Term[]{((StationJpl) station).asTerm(), new Variable(stationsVarName)});
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
			return stations;
		} else
			return null;
	}
}
