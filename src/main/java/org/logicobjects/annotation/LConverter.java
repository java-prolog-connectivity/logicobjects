package org.logicobjects.annotation;

import org.logicobjects.util.AnnotationConstants.NULL;

public @interface LConverter {
	Class value() default NULL.class; //synonym of converter()
	Class converter() default NULL.class;
	Class preferedClass() default NULL.class; //TODO this needs to be finished
	
	public static class LConverterUtil {
		public static Class getConverterClass(LConverter aLConverter) {
			Class converterClass = aLConverter.converter();
			if(converterClass == null || converterClass.equals(NULL.class))
				converterClass = aLConverter.value();
			if(converterClass.equals(NULL.class))
				converterClass = null;
			return converterClass;
		}
		
//		public static JpcConverter newConverter(LConverter aLConverter) {
//			try {
//				JpcConverter converter = null;
//				Class converterClass = LConverterUtil.getConverterClass(aLConverter);
//				if(converterClass != null) {
//					converter = (JpcConverter)converterClass.newInstance();
//				}
//				return converter;
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		}
	}
}
