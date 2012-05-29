package org.logicobjects.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.util.AnnotationConstants.NO_ADAPTER;

/*
 * Defines an adapter for transforming a term into an object
 */
@Target({ElementType.TYPE, ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface LObjectAdapter {
	Class value() default NO_ADAPTER.class; //synonym of adapter()
	Class adapter() default NO_ADAPTER.class;
	String[] args() default {};
	
	public static class LObjectAdapterUtil {
		public static Class getAdapter(LObjectAdapter aLObjectAdapter) {
			Class adapterClass = aLObjectAdapter.adapter();
			if(adapterClass == null || adapterClass.equals(NO_ADAPTER.class))
				adapterClass = aLObjectAdapter.value();
			if(adapterClass.equals(NO_ADAPTER.class))
				adapterClass = null;
			return adapterClass;
		}
	}
	
}
