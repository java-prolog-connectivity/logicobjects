package org.logicobjects.adapter.adaptingcontext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.core.LogicObjectClass;
import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

public class BeanPropertyAdaptationContext extends AnnotatedElementAdaptationContext {

	private Class clazz;
	private String propertyName;
	
	private Field propertyField;
	private Method propertyGetter;
	private Method propertySetter;
	private Type propertyType;
	private Class guidingClass;
	
	private final static Class[] VALID_GETTER_ANNOTATIONS = new Class[] {LObject.class, LTermAdapter.class};
	private final static Class[] VALID_SETTER_PARAMETER_ANNOTATIONS = new Class[] {LObject.class, LObjectAdapter.class};
	
	public BeanPropertyAdaptationContext(Class clazz, String propertyName) {
		this.clazz = clazz;
		this.propertyName = propertyName;

		Class propertyDeclaringClass = LogicObjectClass.findGuidingClass(clazz);
		
		if(propertyDeclaringClass != null) {
			List<Class> guidingCLasses = LogicObjectClass.findAllLogicClasses(propertyDeclaringClass);
			for(Class aGuidingClass : guidingCLasses) {
				try {
					propertyField = aGuidingClass.getDeclaredField(propertyName);
					if(propertyField != null)
						break;
				} catch (NoSuchFieldException e) { //do nothing if the field does not exist
				} catch (SecurityException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		propertyGetter = ReflectionUtil.getter(clazz,  propertyName);
		propertySetter = ReflectionUtil.setter(clazz,  propertyName);
		propertyType = findPropertyType(); //this requires the property field, the getter and the setter being initialized
		
		Class propertyClass = AbstractTypeWrapper.wrap(propertyType).asClass();
		guidingClass = LogicObjectClass.findGuidingClass(propertyClass);
	}
	
	public Class getGuidingClass() {
		return guidingClass;
	}
	
	public String getPropertyName() {
		return propertyName;
	}

	public Type getPropertyType() {
		return propertyType;
	}

	public Field getPropertyField() {
		return propertyField;
	}

	public Method getPropertyGetter() {
		return propertyGetter;
	}

	public Method getPropertySetter() {
		return propertySetter;
	}

	private Type findPropertyType() {
		if(propertyField != null)
			return propertyField.getGenericType();
		if(propertyGetter != null)
			return propertyGetter.getGenericReturnType();
		if(propertySetter != null)
			return propertySetter.getGenericParameterTypes()[0];
		return Object.class;
	}

	private <A extends Annotation> A getAnnotationField(Class<A> annotationClass) {
		if(propertyField != null)
			return propertyField.getAnnotation(annotationClass);
		return null;
	}
	
	private <A extends Annotation> A getAnnotationGetter(Class<A> annotationClass) {
		if(propertyGetter != null && isValidGetterAnnotation(annotationClass))
			return (A)propertyGetter.getAnnotation(annotationClass);
		return null;
	}
	
	private <A extends Annotation> A getAnnotationSetterParameter(Class<A> annotationClass) {
		if(propertySetter != null && isValidSetterParameterAnnotation(annotationClass))
			return (A)ReflectionUtil.getAnnotationParameter(propertySetter, 0, annotationClass);
		return null;
	}
	
	private boolean isValidGetterAnnotation(Class annotationClass) {
		return Arrays.asList(VALID_GETTER_ANNOTATIONS).contains(annotationClass);
	}
	
	private boolean isValidSetterParameterAnnotation(Class annotationClass) {
		return Arrays.asList(VALID_SETTER_PARAMETER_ANNOTATIONS).contains(annotationClass);
	}

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

	@Override
	public Class getContextClass() {
		return AbstractTypeWrapper.wrap(getPropertyType()).asClass();
	}
	
}
