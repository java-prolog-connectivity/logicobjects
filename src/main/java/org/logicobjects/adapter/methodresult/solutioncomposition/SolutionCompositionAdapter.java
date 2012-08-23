package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.EachSolutionAdapter;
import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.VariableTypeWrapper;

/*
 * This adapter adapts one or more answers of type EachSolutionType as an object of type MethodResultType.
 */
public abstract class SolutionCompositionAdapter<MethodResultType, EachSolutionType> extends MethodResultAdapter<MethodResultType> {

	//private Type adapterWithBoundVariableTypes;
	private Type eachSolutionType;
	
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
	
	/*
	private Type getAdapterWithBoundVariableTypes() {
		if(adapterWithBoundVariableTypes == null) {
			adapterWithBoundVariableTypes = new GenericsUtil().bindTypeGivenDescendant(SolutionCompositionAdapter.class, getClass());
		}
		return adapterWithBoundVariableTypes;
	}
	*/

	
	public Type getEachSolutionType() {
		GenericsUtil util = new GenericsUtil();
		Type adapterWithBoundVariableTypes = util.bindTypeGivenDescendant(SolutionCompositionAdapter.class, getClass());
		AbstractTypeWrapper wrappedAdapterType = AbstractTypeWrapper.wrap(adapterWithBoundVariableTypes);
		Type declaredMethodResultType = wrappedAdapterType.getActualTypeArguments()[0];
		Type eachSolutionType = wrappedAdapterType.getActualTypeArguments()[1];
		if(! (AbstractTypeWrapper.wrap(eachSolutionType) instanceof VariableTypeWrapper) )
			return eachSolutionType;
		
		Map<TypeVariable, Type> map = util.unifyWithDescendant(declaredMethodResultType, getConcreteMethodResultType());
		Type boundType = map.get(eachSolutionType);
		
		if(boundType==null)
			throw new RuntimeException("boundtype = null");
		
		return boundType != null?boundType:Object.class;
	}


}
