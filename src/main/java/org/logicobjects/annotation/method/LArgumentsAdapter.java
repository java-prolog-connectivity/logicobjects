package org.logicobjects.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.util.AnnotationConstants.NULL;

//TODO: This annotation adapts the arguments of a logic method from a given initial array to a final array of parameters

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LArgumentsAdapter {
	Class value() default NULL.class;
	Class adapter() default NULL.class;
	
	public static class LArgsAdapterUtil {
		
		public static Class getAdapterClass(LArgumentsAdapter aLArgsAdapter) {
			Class adapterClass = aLArgsAdapter.adapter();
			if(adapterClass == null || adapterClass.equals(NULL.class))
				adapterClass = aLArgsAdapter.value();
			if(adapterClass.equals(NULL.class))
				adapterClass = null;
			return adapterClass;
		}
		
	}
}
