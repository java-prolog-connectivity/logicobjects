package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jpc.query.Query;
import org.jpc.term.AbstractTerm;
import org.logicobjects.adapter.methodresult.eachsolution.EachSolutionAdapter;

/*
 * A convenient wrapper over a query using adapters.
 * Given that it is wrapping an Enumeration object (the query and its results), this class implements also Enumeration.
 * Note that Enumeration is an almost deprecated class, for that reason the class also implements Iterator.
 * However, the class does not implements Iterable (and then it cannot be used in enhanced 'for' loops) since then it should provide a method for returning an Iterator. 
 * Then this method in theory could be called as many times as needed and in parallel. 
 * However, the wrapped query will not behave adequately in such circumstance.
 */
public class SolutionEnumeration<EachSolutionType> implements Enumeration<EachSolutionType>, Iterator<EachSolutionType> /*, Iterable<LogicAnswerType>*/ { 

	private EachSolutionAdapter<EachSolutionType> adapter;
	private Query query;
	
	public SolutionEnumeration(Query query) {
		this(query, (EachSolutionAdapter<EachSolutionType>) new EachSolutionAdapter.EachSolutionMapAdapter());
	}
	
	public SolutionEnumeration(Query query, EachSolutionAdapter<EachSolutionType> adapter) {
		this.query = query;
		this.adapter = adapter;
	}
	
	//will close the query when no more elements are present
	@Override
	public boolean hasMoreElements() {
		return query.hasNext();
	}

	@Override
	public EachSolutionType nextElement() {
		if(hasMoreElements())
			return adapter.adapt((Map)query.next());
		else
			return null;
	}
	
	private List<EachSolutionType> asList(List<Map<String, AbstractTerm>> solutions) {
		List<EachSolutionType> answers = new ArrayList<EachSolutionType>();
		for(Map<String, AbstractTerm> aSolution : solutions) {
			answers.add(adapter.adapt(aSolution));
		}
		return answers;
	}
	
	/*
	 * will not close the query, it should be closed afterwards
	 */
	public List<EachSolutionType> nElements(long n) {
		return asList(query.nSolutions(n));
	}
	
	public List<EachSolutionType> allElements() {
		return asList(query.allSolutions());
	}
	/*
	public void open() {
		query.open();
	}
	*/
	public void close() {
		query.close();
	}
	/*
	public void rewind() {
		query.rewind();
	}
*/
	@Override
	public boolean hasNext() {
		return hasMoreElements();
	}

	@Override
	public EachSolutionType next() {
		return nextElement();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
