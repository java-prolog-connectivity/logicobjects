package org.logicobjects.adapter.methodresult.solutioncomposition;


import java.lang.reflect.Method;
import java.util.Set;

import jpl.Query;

import org.logicobjects.core.LogicObjectFactory;
import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.GenericsUtil;

public class SmartWrapperAdapter extends WrapperAdapter<Object, Object>  {

	public SmartWrapperAdapter(Method method, Object targetObject, Object[] javaMethodParams) {
		super(method, targetObject, javaMethodParams);
	}

	@Override
	public Object adapt(Query source) {
		Class<? extends WrapperAdapter> wrapperAdapterClass = findWrapperAdapterClass();
		if(wrapperAdapterClass!=null) {
			try {
				WrapperAdapter wrapperAdapter = wrapperAdapterClass.getConstructor(Method.class, Object.class, Object[].class).newInstance(getMethod(), getTargetObject(), getJavaMethodParams());
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
