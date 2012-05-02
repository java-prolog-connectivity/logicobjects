package org.logicobjects.adapter.methodresult.eachsolution;

import java.lang.reflect.Method;
import java.util.Map;

import org.logicobjects.adapter.LogicAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.SolutionCompositionAdapter;

/*
 * Adapts a single logic answer. This adapter is used by the composition adapters, so many individual adapted solutions can be composed together in another object
 * such as a List, Enumeration, etc
 */
public abstract class EachSolutionAdapter<EachSolutionType> extends LogicAdapter<Map, EachSolutionType> {
	/*
	 * A reference to the composition adapter that uses this EachSolutionAdapter for generating the final answer
	 */
	SolutionCompositionAdapter compositionAdapter;
	
	/*
	 * Default answer adapter used by composition adapters without an answer adapter explicitly set.
	 * This adapter return each answer (a Hashtable) without changing it
	 */
	public static class DefaultEachSolutionAdapter extends EachSolutionAdapter<Map> {
		
		@Override
		public Map adapt(Map source) {
			return source;
		}
	}

	public SolutionCompositionAdapter getCompositionAdapter() {
		return compositionAdapter;
	}

	public void setCompositionAdapter(SolutionCompositionAdapter compositionAdapter) {
		this.compositionAdapter = compositionAdapter;
	}
	
	public Method getMethod() {
		return getCompositionAdapter().getMethod();
	}
	
	public Object getTargetObject() {
		return getCompositionAdapter().getTargetObject();
	}
	
	public Object[] getJavaMethodParams() {
		return getCompositionAdapter().getJavaMethodParams();
	}
	
}
