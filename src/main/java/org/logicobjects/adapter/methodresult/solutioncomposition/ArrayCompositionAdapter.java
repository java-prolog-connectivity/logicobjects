package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import jpl.Query;

import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.ArrayTypeWrapper;
import org.reflectiveutils.GenericsUtil;

public class ArrayCompositionAdapter<LogicAnswerType> extends WrapperAdapter<LogicAnswerType[], LogicAnswerType>  {

	@Override
	public LogicAnswerType[] adapt(Query query) {
		List<LogicAnswerType> list = new SolutionEnumeration(query, getEachSolutionAdapter()).allElements();
		Class arrayClass = AbstractTypeWrapper.wrap(new ArrayTypeWrapper(getMethod().getGenericReturnType()).getComponentType()).asClass();
		Object array = Array.newInstance(arrayClass, list.size());
		for(int i=0; i<list.size(); i++) {
			Array.set(array, i, list.get(i));
		}
		return (LogicAnswerType[]) array;
	}
	
	@Override
	public Type getEachSolutionType() {
		ArrayTypeWrapper typeWrapper = new ArrayTypeWrapper(getMethodResultType());
		return typeWrapper.getComponentType();
	}

}
