package org.logicobjects.logicengine.jpl;

import java.util.Map;
import java.util.Map.Entry;

import org.logicobjects.term.Compound;
import org.logicobjects.term.Query;
import org.logicobjects.term.Term;

public class JPLQueryAdapter extends Query {

	private jpl.Query jplQuery;
	
	public JPLQueryAdapter(Term goal) {
		super(goal);
		LogicObjectsToJPLVisitor logicObjectsToJPLVisitor = new LogicObjectsToJPLVisitor();
		jpl.Term jplGoal = (jpl.Term) logicObjectsToJPLVisitor.transform(goal);
		jplQuery = new jpl.Query(jplGoal);
	}

	@Override
	public boolean isOpen() {
		return jplQuery.isOpen();
	}

	@Override
	public void abort() {
		jplQuery.abort();
	}

	@Override
	public void close() {
		jplQuery.close();
	}

	@Override
	public boolean hasNext() {
		return jplQuery.hasMoreSolutions();
	}

	@Override
	public Map<String, Term> next() {
		JPLToLogicObjectsVisitor jplToLogicObjectsVisitor = new JPLToLogicObjectsVisitor();
		Map<String, Term> nextSolution = null;
		Map<String, jpl.Term> jplSolution = jplQuery.nextSolution();
		if(jplSolution != null) {
			for(Entry<String, jpl.Term> jplEntry : jplSolution.entrySet()) {
				String varName = jplEntry.getKey();
				Term term = (Term) jplToLogicObjectsVisitor.transform(jplEntry.getValue());
				nextSolution.put(varName, term);
			}
		}
		return nextSolution;
	}
}
