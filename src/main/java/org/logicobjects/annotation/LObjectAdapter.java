package org.logicobjects.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.util.AnnotationConstants.NULL;

/*
 * Defines an adapter for transforming a term into an object
 */
@Target({ElementType.TYPE, ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface LObjectAdapter {
	Class value() default NULL.class; //synonym of adapter()
	Class adapter() default NULL.class;
	String[] args() default {};
	
	public static class LObjectAdapterUtil {
		
		public static Class getAdapterClass(LObjectAdapter aLObjectAdapter) {
			Class adapterClass = aLObjectAdapter.adapter();
			if(adapterClass == null || adapterClass.equals(NULL.class))
				adapterClass = aLObjectAdapter.value();
			if(adapterClass.equals(NULL.class))
				adapterClass = null;
			return adapterClass;
		}
		
		public static TermToObjectAdapter newAdapter(LObjectAdapter aLObjectAdapter) {
			try {
				TermToObjectAdapter adapter = null;
				Class adapterClass = LObjectAdapterUtil.getAdapterClass(aLObjectAdapter);
				if(adapterClass != null) {
					adapter = (TermToObjectAdapter)adapterClass.newInstance();
					adapter.setParameters(Arrays.asList(aLObjectAdapter.args()));
				}
				return adapter;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
