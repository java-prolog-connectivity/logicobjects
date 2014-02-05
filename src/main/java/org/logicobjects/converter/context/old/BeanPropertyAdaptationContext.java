package org.logicobjects.converter.context.old;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectConverter;
import org.logicobjects.annotation.LTermConverter;
import org.logicobjects.converter.old.LogicBeanProperty;
import org.logicobjects.core.LogicClass;
import org.minitoolbox.reflection.ReflectionUtil;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class BeanPropertyAdaptationContext extends AnnotatedElementAdaptationContext {

	private LogicBeanProperty beanProperty;
	private Class guidingClass;
	
	//private final static Class[] VALID_GETTER_ANNOTATIONS = new Class[] {LObject.class, LTermConverter.class};
	//private final static Class[] VALID_SETTER_PARAMETER_ANNOTATIONS = new Class[] {LObject.class, LObjectConverter.class};
	
	public BeanPropertyAdaptationContext(Class clazz, String propertyName) {
		beanProperty = new LogicBeanProperty(clazz, propertyName);
		guidingClass = LogicClass.findGuidingClass(beanProperty.getPropertyClass());
	}
	
	public Class getGuidingClass() {
		return guidingClass;
	}


	private <A extends Annotation> A getAnnotationField(Class<A> annotationClass) {
		if(beanProperty.getPropertyField() != null)
			return beanProperty.getPropertyField().getAnnotation(annotationClass);
		return null;
	}
	
	private <A extends Annotation> A getAnnotationGetter(Class<A> annotationClass) {
		if(beanProperty.getPropertyGetter() != null && isValidGetterAnnotation(annotationClass))
			return (A)beanProperty.getPropertyGetter().getAnnotation(annotationClass);
		return null;
	}
	
	private <A extends Annotation> A getAnnotationSetterParameter(Class<A> annotationClass) {
		if(beanProperty.getPropertySetter() != null && isValidSetterParameterAnnotation(annotationClass))
			return (A)ReflectionUtil.getParameterAnnotation(beanProperty.getPropertySetter(), 0, annotationClass);
		return null;
	}
	
//	private boolean isValidGetterAnnotation(Class annotationClass) {
//		return Arrays.asList(VALID_GETTER_ANNOTATIONS).contains(annotationClass);
//	}
//	
//	private boolean isValidSetterParameterAnnotation(Class annotationClass) {
//		return Arrays.asList(VALID_SETTER_PARAMETER_ANNOTATIONS).contains(annotationClass);
//	}

	@Override
	protected <A extends Annotation> A getMappingAnnotationLocalContext(Class<A> annotationClass) {
		A annotation = getAnnotationGetter(annotationClass);
		if(annotation != null)
			return annotation;
		annotation = getAnnotationSetterParameter(annotationClass);
		if(annotation != null)
			return annotation;
		annotation = getAnnotationField(annotationClass);
		return annotation;
	}

	public Type getPropertyType() {
		return beanProperty.getPropertyType();
	}
	
	@Override
	public Class getContextClass() {
		return beanProperty.getPropertyClass();
	}
	
	//TODO delete ???
	public Field getPropertyField() {
		return beanProperty.getPropertyField();
	}
	
}
