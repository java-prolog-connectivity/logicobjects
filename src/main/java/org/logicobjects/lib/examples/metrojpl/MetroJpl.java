package org.logicobjects.lib.examples.metrojpl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;
import jpl.Variable;

import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IMetro;

public class MetroJpl implements IMetro {

	@Override
	public String toString() {return "metro";}
	
	public Term asTerm() {
		return new Atom("metro");
	}
	
	public static String LOADER = "logic_lib/examples/metro/load_all";
	
	public static boolean loadAll() {
		Term logtalkLoadTerm = new Compound("logtalk_load", new Term[]{new Atom(LOADER)});
		Query query = new Query(logtalkLoadTerm);
		return query.hasSolution();
	}

	@Override
	public List<ILine> lines() {
		String lineVarName = "Line";
		Term message = new Compound("line", new Term[]{new Variable(lineVarName)});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		Hashtable<String, Term> solutions[] = query.allSolutions();
		List<ILine> lines = new ArrayList<ILine>();
		for(Hashtable<String, Term> solution : solutions) {
			Term term = new Compound("line", new Term[] {solution.get(lineVarName)});
			lines.add(LineJpl.create(term));
		}
		return lines;
	}

	@Override
	public ILine line(String name) {
		Term message = new Compound("line", new Term[]{new Atom(name)});
		Term objectMessage = new Compound("::", new Term[] {asTerm(), message});
		Query query = new Query(objectMessage);
		if(query.hasSolution())
			return LineJpl.create(message);
		else
			return null;
	}
	
}
