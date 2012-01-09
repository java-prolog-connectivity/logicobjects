package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.util.Set;

import jpl.Query;

import org.logicobjects.core.LogicObjectFactory;
import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.GenericsUtil;

public class SmartWrapperAdapter extends WrapperAdapter<Object, Object>  {

	@Override
	public Object adapt(Query source) {
		Class<? extends WrapperAdapter> wrapperAdapterClass = findWrapperAdapterClass();
		if(wrapperAdapterClass!=null) {
			try {
				WrapperAdapter wrapperAdapter = wrapperAdapterClass.newInstance();
				return wrapperAdapter.adapt(source);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("Impossible to find a wrapper adapter");
		}
	}
	
	
	public Class findWrapperAdapterClass() {
		AbstractTypeWrapper[] typeWrappers = new GenericsUtil().findParametersInstantiations(WrapperAdapter.class, getMethod().getGenericReturnType());
		Set<Class<? extends WrapperAdapter>> wrapperAdaptersClasses = LogicObjectFactory.getDefault().getContext().getWrapperAdapters();
		for(Class wrapperAdaptersClass : wrapperAdaptersClasses) {
			if(typeWrappers[0].isAssignableFrom(wrapperAdaptersClass));
				return wrapperAdaptersClass;
		}
		return null;
	}

}
