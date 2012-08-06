package org.logicobjects.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.util.AnnotationConstants.NO_ADAPTER;

//TODO: This annotation adapts the parameters of a logic method from a given initial array to a final array of parameters

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LArgsAdapter {
	Class value() default NO_ADAPTER.class;
	Class adapter() default NO_ADAPTER.class;
	
	public static class LArgsAdapterUtil {
		
		public static Class getAdapterClass(LArgsAdapter aLArgsAdapter) {
			Class adapterClass = aLArgsAdapter.adapter();
			if(adapterClass == null || adapterClass.equals(NO_ADAPTER.class))
				adapterClass = aLArgsAdapter.value();
			if(adapterClass.equals(NO_ADAPTER.class))
				adapterClass = null;
			return adapterClass;
		}
		
	}
}
