package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Type;

import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.SingleTypeWrapper;


//public abstract class MyWrapper extends WrapperAdapter<List<X>, X>    List<String>

public abstract class WrapperAdapter<MethodResultType, EachSolutionType> extends
		SolutionCompositionAdapter<MethodResultType, EachSolutionType> {

	protected Type getFirstParameterizedType() {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(getMethodResultType());
		if(typeWrapper instanceof SingleTypeWrapper) {
			SingleTypeWrapper sTypeWrapper = (SingleTypeWrapper) typeWrapper;
			if(sTypeWrapper.hasActualTypeArguments())
				return sTypeWrapper.getActualTypeArguments()[0];
		}
		return null;
	}
	
	/*
	 * By default wrapper adapters has its first parameterized type as each solution type.
	 * For example, if the wrapper type is : MyWrapper<String> , then each solution type will be a String
	 * This can be overridden if necessary
	 */
	public Type getEachSolutionType() {
		Type eachSolutionType = getFirstParameterizedType();
		return eachSolutionType!=null?eachSolutionType:Object.class;
	}

}
