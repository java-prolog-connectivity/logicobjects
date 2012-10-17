package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Array;
import java.util.List;

import org.jpc.term.Query;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.ArrayTypeWrapper;

public class ArrayWrapperAdapter<LogicAnswerType> extends WrapperAdapter<LogicAnswerType[], LogicAnswerType>  {

	@Override
	public LogicAnswerType[] adapt(Query query) {
		List<LogicAnswerType> list = new SolutionEnumeration(query, getEachSolutionAdapter()).allElements();
		Class arrayClass = AbstractTypeWrapper.wrap(new ArrayTypeWrapper(getConcreteMethodResultType()).getComponentType()).asClass();
		Object array = Array.newInstance(arrayClass, list.size());
		for(int i=0; i<list.size(); i++) {
			Array.set(array, i, list.get(i));
		}
		return (LogicAnswerType[]) array;
	}

}
