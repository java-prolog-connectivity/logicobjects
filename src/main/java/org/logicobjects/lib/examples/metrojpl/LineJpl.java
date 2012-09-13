package org.logicobjects.lib.examples.metrojpl;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;
import jpl.Variable;

import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IStation;

public class LineJpl implements ILine {

	private String name;
	
	public LineJpl(String name) {
		this.name = name;
	}
	
	//@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {return name;}
	
	public Term asTerm() {
		return new Compound("line", new Term[] {new Atom(name)});
	}
	
	public static ILine create(Term term) {
		Compound lineTerm = (Compound)term;
		String lineName = lineTerm.arg(1).name();
		return new LineJpl(lineName);
	}


	public boolean connects(IStation s1, IStation s2) {
		Term[] arguments = new Term[]{((StationJpl) s1).asTerm(), ((StationJpl) s2).asTerm()};
		Term message = new Compound("connects", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.hasSolution();
	}


	public int segments() {
		Term[] arguments = new Term[]{new Variable("_"), new Variable("_")};
		Term message = new Compound("connects", arguments);
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.allSolutions().length;
	}
	
}
