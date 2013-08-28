package org.logicobjects.methodadapter.methodresult.solutioncomposition;



//public abstract class MyWrapper extends WrapperAdapter<List<X>, X>    List<String>

public abstract class WrapperAdapter<MethodResultType, EachSolutionType> extends
		SolutionCompositionAdapter<MethodResultType, EachSolutionType> {


	/*
	protected Type[] getMethodActualTypeArguments() {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(getMethodResultType());
		if(typeWrapper.hasActualTypeArguments())
			return typeWrapper.getActualTypeArguments();
		else
			return null;
	}
	
	protected Type getFirstParameterizedType() {
		Type[] actualTypeArguments = getMethodActualTypeArguments();
		if(actualTypeArguments != null)
			return actualTypeArguments[0];
		else
			return null;
	}
	*/
	
	
	
	/*
	 * By default wrapper adapters has its first parameterized type as each solution type.
	 * For example, if the wrapper type is : MyWrapper<String> , then each solution type will be a String
	 * This can be overridden if necessary
	 */
	/*
	public Type getEachSolutionType() {
		//Type eachSolutionType = getFirstParameterizedType();
		//return eachSolutionType!=null?eachSolutionType:Object.class;
		return null;
	}
*/
	
}
