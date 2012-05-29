package org.logicobjects.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.util.AnnotationConstants.NO_ADAPTER;

/*
 *  Defines an adapter for transforming an object into a term
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface LTermAdapter {
	Class value() default NO_ADAPTER.class; //value() is a synonym of adapter()
	Class adapter() default NO_ADAPTER.class;
	String[] args() default {};
	
	public static class LTermAdapterUtil {
		public static Class getAdapter(LTermAdapter aLTermAdapter) {
			Class adapterClass = aLTermAdapter.adapter();
			if(adapterClass == null || adapterClass.equals(NO_ADAPTER.class))
				adapterClass = aLTermAdapter.value();
			if(adapterClass.equals(NO_ADAPTER.class))
				adapterClass = null;
			return adapterClass;
		}
	}
}
