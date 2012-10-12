package org.logicobjects.test;

import java.util.Map;

import jpl.Atom;
import jpl.Query;
import jpl.Term;
import static com.google.common.base.Preconditions.*;

/**
 * An utility class for testing evaluation of queries in threads
 * //JUnit cannot see the exceptions that occur in threads other than the thread in which the tests are running 
 * (http://stackoverflow.com/questions/4039873/weird-problem-using-junit-in-multi-thread-environment).
 * It does not support assert expressions in a thread. The QueryThreadEvaluator class is a workaround to this problem
 * @author scastro
 *
 */
public class QueryThreadEvaluator extends Thread {

	private Term term;
	private Map[] allSolutions;
	private Query query;
	private boolean hasSolution;
	
	public QueryThreadEvaluator(Term term) {
		this.term = term;
	}
	
	@Override
	public void run() {
		query = new Query(term);
		hasSolution = query.hasSolution();
		allSolutions = query.allSolutions();
	}

	public Map[] getAllSolutions() {
		checkNotNull(query);
		return allSolutions;
	}
	
	public boolean hasSolution() {
		checkNotNull(query);
		return hasSolution;
		//checkNotNull(allSolutions);
		//return allSolutions != null && allSolutions.length > 0;
	}
	
	public void startAndJoin() {
		super.start();
		uncheckedJoin();
	}
	
	public void uncheckedJoin() {
		try {
			join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
