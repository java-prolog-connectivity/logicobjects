package org.logicobjects.lib.examples.metrojpl;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;
import jpl.Variable;

public class LineJpl {

	private String name;
	
	public LineJpl(String name) {
		this.name = name;
	}
	
	public Term asTerm() {
		return new Compound("line", new Term[] {new Atom(name)});
	}
	
	public static LineJpl create(Term term) {
		Compound lineTerm = (Compound)term;
		String lineName = lineTerm.arg(1).name();
		return new LineJpl(lineName);
	}


	public boolean connects(StationJpl s1, StationJpl s2) {
		Term message = new Compound("connects", new Term[]{s1.asTerm(), s2.asTerm()});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.hasSolution();
	}


	public int segments() {
		Term message = new Compound("connects", new Term[]{new Variable("_"), new Variable("_")});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		return query.allSolutions().length;
	}

}
