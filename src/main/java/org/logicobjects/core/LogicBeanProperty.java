package org.logicobjects.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

public class LogicBeanProperty {

	private Class clazz;
	private String propertyName;
	
	private Field propertyField;
	private Method propertyGetter;
	private Method propertySetter;
	private Type propertyType;
	private Class propertyClass;
	
	public LogicBeanProperty(Class clazz, String propertyName) {
		this.clazz = clazz;
		this.propertyName = propertyName;
		Class propertyDeclaringClass = LogicObjectClass.findGuidingClass(clazz);
		
		if(propertyDeclaringClass != null) {
			List<LogicObjectClass> logicClasses = LogicObjectClass.findAllLogicObjectClasses(propertyDeclaringClass);
			for(LogicObjectClass aGuidingClass : logicClasses) {
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
	
	public String getPropertyName() {
		return propertyName;
	}

	public Type getPropertyType() {
		return propertyType;
	}
	
	public Class getPropertyClass() {
		return propertyClass;
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
}
