package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Array;
import java.util.List;

import org.jpc.query.Query;
import org.minitoolbox.reflection.typewrapper.ArrayTypeWrapper;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class ArrayWrapperAdapter<LogicAnswerType> extends WrapperAdapter<LogicAnswerType[], LogicAnswerType>  {

	@Override
	public LogicAnswerType[] adapt(Query query) {
		List<LogicAnswerType> list = new SolutionEnumeration(query, getEachSolutionAdapter()).allElements();
		Class arrayClass = TypeWrapper.wrap(new ArrayTypeWrapper(getConcreteMethodResultType()).getComponentType()).getRawClass();
		Object array = Array.newInstance(arrayClass, list.size());
		for(int i=0; i<list.size(); i++) {
			Array.set(array, i, list.get(i));
		}
		return (LogicAnswerType[]) array;
	}

}
