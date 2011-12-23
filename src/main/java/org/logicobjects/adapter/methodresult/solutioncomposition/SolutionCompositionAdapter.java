package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Type;

import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.EachSolutionAdapter;

import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.SingleTypeWrapper;

/*
 * This adapter adapts one or more answers of type EachSolutionType as an object of type MethodResultType.
 */
public abstract class SolutionCompositionAdapter<MethodResultType, EachSolutionType> extends MethodResultAdapter<MethodResultType> {

	private EachSolutionAdapter<EachSolutionType> eachSolutionAdapter;
	
	public SolutionCompositionAdapter() {
		setEachSolutionAdapter((EachSolutionAdapter<EachSolutionType>) new EachSolutionAdapter.DefaultEachSolutionAdapter());
	}
	/*
	public SolutionCompositionAdapter(Object ...parameters) {
		super(parameters);
	}
	*/


	public EachSolutionAdapter<EachSolutionType> getEachSolutionAdapter() {
		return eachSolutionAdapter;
	}

	public void setEachSolutionAdapter(
			EachSolutionAdapter<EachSolutionType> eachSolutionAdapter) {
		this.eachSolutionAdapter = eachSolutionAdapter;
		eachSolutionAdapter.setCompositionAdapter(this);
	}
	
	protected Type getFirstParameterizedType() {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(getMethodResultType());
		if(typeWrapper instanceof SingleTypeWrapper) {
			SingleTypeWrapper sTypeWrapper = (SingleTypeWrapper) typeWrapper;
			if(sTypeWrapper.isParameterized())
				return sTypeWrapper.getParameters()[0];
		}
		return null;
	}
	
	public Type getEachSolutionType() {
		Type eachSolutionType = getFirstParameterizedType();
		return eachSolutionType!=null?eachSolutionType:Object.class;
	}
	



}
