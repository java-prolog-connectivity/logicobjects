package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import jpl.Query;

import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.EachSolutionAdapter;
import org.logicobjects.instrumentation.ParsedLogicMethod;

import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.SingleTypeWrapper;

/*
 * This adapter adapts one or more answers of type EachSolutionType as an object of type MethodResultType.
 */
public abstract class SolutionCompositionAdapter<MethodResultType, EachSolutionType> extends MethodResultAdapter<MethodResultType> {

	
	private EachSolutionAdapter<EachSolutionType> eachSolutionAdapter;
	
	/*
	public SolutionCompositionAdapter() {
		setEachSolutionAdapter((EachSolutionAdapter<EachSolutionType>) new EachSolutionAdapter.DefaultEachSolutionAdapter());
	}
	*/
	
	public SolutionCompositionAdapter() {
		setEachSolutionAdapter((EachSolutionAdapter<EachSolutionType>) new EachSolutionAdapter.EachSolutionMapAdapter());
	}


	public EachSolutionAdapter<EachSolutionType> getEachSolutionAdapter() {
		return eachSolutionAdapter;
	}

	public void setEachSolutionAdapter(
			EachSolutionAdapter<EachSolutionType> eachSolutionAdapter) {
		this.eachSolutionAdapter = eachSolutionAdapter;
		eachSolutionAdapter.setCompositionAdapter(this);
	}
	
	public abstract Type getEachSolutionType();


}
