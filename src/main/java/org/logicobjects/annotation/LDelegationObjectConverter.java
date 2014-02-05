package org.logicobjects.annotation;

import org.jpc.converter.JpcConverter;


public @interface LDelegationObjectConverter {
	Class<? extends JpcConverter> value();
	
	public static class LDelegationObjectConverterUtil {
		public static Class<? extends JpcConverter> getConverterClass(LDelegationObjectConverter aLConverter) {
			return aLConverter.value();
		}
	}
	
	/*
	Class<? extends JpcConverter> value() default NULL.class; //synonym of converter()
	Class<? extends JpcConverter> converter() default NULL.class;
	Class preferedClass() default NULL.class;
	
	
	public static class LDelegationObjectConverterUtil {
		public static Class<? extends JpcConverter> getConverterClass(LDelegationObjectConverter aLConverter) {
			Class converterClass = aLConverter.converter();
			if(converterClass == null || converterClass.equals(NULL.class))
				converterClass = aLConverter.value();
			if(converterClass.equals(NULL.class))
				converterClass = null;
			return converterClass;
		}
	}
	 */

}
