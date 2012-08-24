package org.logicobjects.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.util.AnnotationConstants.NULL;

/*
 *  Defines an adapter for transforming an object into a term
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface LTermAdapter {
	Class value() default NULL.class; //value() is a synonym of adapter()
	Class adapter() default NULL.class;
	String[] args() default {};
	
	public static class LTermAdapterUtil {
		
		public static Class getAdapterClass(LTermAdapter aLTermAdapter) {
			Class adapterClass = aLTermAdapter.adapter();
			if(adapterClass == null || adapterClass.equals(NULL.class))
				adapterClass = aLTermAdapter.value();
			if(adapterClass.equals(NULL.class))
				adapterClass = null;
			return adapterClass;
		}
		
		public static ObjectToTermAdapter newAdapter(LTermAdapter aLTermAdapter) {
			try {
				ObjectToTermAdapter termAdapter = (ObjectToTermAdapter)LTermAdapterUtil.getAdapterClass(aLTermAdapter).newInstance();
				termAdapter.setParameters(aLTermAdapter.args());
				return termAdapter;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	}
}
