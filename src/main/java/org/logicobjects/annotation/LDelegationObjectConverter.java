package org.logicobjects.annotation;

import org.logicobjects.util.AnnotationConstants.NULL;

public @interface LDelegationObjectConverter {
	Class value() default NULL.class; //synonym of converter()
	Class converter() default NULL.class;
	Class preferedClass() default NULL.class;
	
	public static class LDelegationObjectConverterUtil {
		public static Class getConverterClass(LDelegationObjectConverter aLConverter) {
			Class converterClass = aLConverter.converter();
			if(converterClass == null || converterClass.equals(NULL.class))
				converterClass = aLConverter.value();
			if(converterClass.equals(NULL.class))
				converterClass = null;
			return converterClass;
		}
	}

}
