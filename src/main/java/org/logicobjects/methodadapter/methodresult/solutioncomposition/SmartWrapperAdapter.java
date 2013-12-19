package org.logicobjects.methodadapter.methodresult.solutioncomposition;


import java.util.Set;

import org.jpc.query.Query;
import org.logicobjects.LogicObjects;
import org.minitoolbox.reflection.TypeUtil;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class SmartWrapperAdapter extends WrapperAdapter<Object, Object>  {

	@Override
	public Object adapt(Query source) {
		Class<? extends WrapperAdapter> wrapperAdapterClass = findWrapperAdapterClass();
		if(wrapperAdapterClass!=null) {
			try {
				WrapperAdapter wrapperAdapter = wrapperAdapterClass.getConstructor().newInstance();
				wrapperAdapter.setParsedLogicMethod(getParsedLogicMethod());
				//WrapperAdapter wrapperAdapter = wrapperAdapterClass.newInstance();
				//wrapperAdapter.setMethod(getMethod());
				wrapperAdapter.setEachSolutionAdapter(getEachSolutionAdapter());
				return wrapperAdapter.adapt(source);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("Impossible to find a composition adapter");
		}
	}
	
	public Class findWrapperAdapterClass() {
		Set<Class<? extends WrapperAdapter>> wrapperAdaptersClasses = LogicObjects.getCompositionAdapters();
		TypeWrapper methodTypeWrapper = TypeWrapper.wrap(getConcreteMethodResultType());
		
		for(Class wrapperAdaptersClass : wrapperAdaptersClasses) {
			TypeWrapper wrapperAdapterReturnType = TypeWrapper.wrap(new TypeUtil().findAncestorTypeParameters(WrapperAdapter.class, wrapperAdaptersClass)[0]);
			if(methodTypeWrapper.isRawClassAssignableFrom(wrapperAdapterReturnType.getWrappedType()))
				return wrapperAdaptersClass;
		}
		return null;
	}

}
