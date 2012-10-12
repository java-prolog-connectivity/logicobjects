package org.logicobjects.term;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;


public abstract class LQuery implements Iterator<Map<String, LTerm>> {

	private LCompound goal;
	
	public LQuery(LCompound goal) {
		this.goal = goal;
	}

	public LCompound getGoal() {
		return goal;
	}

	/**
	 * isOpen() returns true iff the query is open.
	 * @return	true if the query is open, otherwise false.
	 */
	public abstract  boolean isOpen();

	public abstract void abort();
	//public abstract void open();
	public abstract void close();
	
	
	/**
	 * 
	 * @param n the number of solutions the method should return
	 * @return is the number of solutions
	 */
	public synchronized Map<String, LTerm>[] nSolutions(int n) {
		return solutionsRange(0, n-1);
	}

	/**
	 * 
	 * @param from the (0-based) index of the first solution
	 * @param to the (0-based) index of the last solution
	 * @return an array with the solutions according to the indexes sent as parameters
	 */
	public synchronized Map<String, LTerm>[] solutionsRange(int from, int to) {
		checkArgument(from >= 0);
		checkArgument(to >= from);
		if (isOpen()) {
			throw new LException("Query is already open");
		} else {
			int count = 0;
			List<Map<String, LTerm>> solutions = new ArrayList<>();
			while(count<=to) {
				Map<String, LTerm> solution = null;
				if(hasNext()) {
					solution = next();
					if(count >= from)
						solutions.add(solution);
				}
				else
					throw new LException("The query " + this + "has only " + count + " solutions");
				count++;
			}
			close();
			return solutions.toArray(new Map[]{});
		}
	}
	
	/**
	 * calls the Query's goal to exhaustion
	 * The query should not be open when this method is called
	 * and returns an array of zero or more Maps of zero or more variablename-to-term bindings (each Map represents a solution, in the order in which they were found).
	 * @return an array of zero or more Hashtables of zero or more variablename-to-term bindings (each Map represents a solution, in the order in which they were found)
	 */
	public synchronized Map<String, LTerm>[] allSolutions() {
		if (isOpen()) {
			throw new LException("Query is already open");
		} else {
			List<Map<String, LTerm>> allSolutions = new ArrayList<>();
			while (hasNext()) { 
				allSolutions.add(next());
			}
			return allSolutions.toArray(new Map[]{});
		}
	}
	
	/**
	 * Answers if there are still solutions to the query
	 * In case there are no more solutions, the query will be closed by this method
	 * @return  true if there are more solutions to the query
	 */
	@Override
	public abstract boolean hasNext();

	
	/**
	 * This method returns an instance of java.util.Map, which represents
	 * a set of bindings from the names of query variables to terms within the solution.
	 * <p>
	 * For example, if a Query has an occurrence of a Variable,
	 * say, named "X", one can obtain the Term bound to "X" in the solution
	 * by looking up "X" in the Map.
	 * <pre>
	 * Variable x = new Variable("X");
	 * Query q = // obtain Query reference (with x in the Term array)
	 * while (q.hasMoreSolutions()) {
	 *     Hashtable solution = q.nextSolution();
	 *     // make t the Term bound to "X" in the solution
	 *     Term t = (Term) solution.get("X");
	 *     // ...
	 * }
	 */
	@Override
	public abstract Map<String, LTerm> next();
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}



	@Override
	public String toString() {
		return goal.toString();
	}
	
	
}
