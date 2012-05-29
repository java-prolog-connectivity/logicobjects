package org.logicobjects.adapter.methodresult.solutioncomposition;


import java.lang.reflect.Method;
import java.util.Set;

import jpl.Query;

import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.instrumentation.ParsedLogicMethod;
import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.GenericsUtil;

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
			throw new RuntimeException("Impossible to find a wrapper adapter");
		}
	}
	
	public Class findWrapperAdapterClass() {
		Set<Class<? extends WrapperAdapter>> wrapperAdaptersClasses = LogicObjectFactory.getDefault().getContext().getWrapperAdapters();
		//AbstractTypeWrapper methodType = AbstractTypeWrapper.wrap(getMethod().getGenericReturnType());
		AbstractTypeWrapper methodTypeWrapper = AbstractTypeWrapper.wrap(getMethodResultType());
		
		for(Class wrapperAdaptersClass : wrapperAdaptersClasses) {
			AbstractTypeWrapper wrapperAdapterReturnType = AbstractTypeWrapper.wrap(new GenericsUtil().findAncestorTypeParameters(WrapperAdapter.class, wrapperAdaptersClass)[0]);
			if(methodTypeWrapper.isAssignableFrom(wrapperAdapterReturnType.getWrappedType()))
				return wrapperAdaptersClass;
				
		}
		return null;
	}

}
