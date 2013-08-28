package org.logicobjects.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.util.AnnotationConstants.NULL;

//TODO: This annotation adapts the arguments of a logic method from a given initial array (the invocation arguments in Java) to an array of objects (of possible different length) to be passed to Prolog

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LArgumentsAdapter {
	Class value() default NULL.class;
	Class adapter() default NULL.class; //TODO delete
	
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
