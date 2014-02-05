package org.logicobjects.converter.old;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectConverter;
import org.logicobjects.annotation.LTermConverter;
import org.logicobjects.core.LogicClass;
import org.minitoolbox.reflection.BeansUtil;
import org.minitoolbox.reflection.ReflectionUtil;

/**
 * This class provides information about the field, getter or setter that could contain mapping information for a logic property in a class.
 * The framework will inspect not only the annotations in the field, but also annotations in its accessors and mutators.
 * Even if the field is not visible at the descendant class, the annotations in that field (if any) should guide the mapping.
 * The relevant field to inspect, is the first with the desired name in the first logic class in the hierarchy.
 * In case there are not logic classes in the hierarchy, it will be considered the first visible field with the desired id
 * @author scastro
 *
 */
public class LogicBeanProperty {

	private Class clazz;
	private String propertyName;
	
	private Field propertyField;
	private Method propertyGetter;
	private Method propertySetter;
	private Type propertyType;
	private Class propertyClass;
	
	private LObject lObject;
	private LTermConverter lTermConverter;
	private LObjectConverter lObjectConverter;
	
	public LogicBeanProperty(Class clazz, String propertyName) {
		this.clazz = clazz;
		this.propertyName = propertyName;
		Class propertyDeclaringClass = LogicClass.findGuidingClass(clazz);
		
		if(propertyDeclaringClass != null) {
			List<LogicClass> logicClasses = LogicClass.findAllLogicObjectClasses(propertyDeclaringClass);
			for(LogicClass aGuidingClass : logicClasses) {
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
		
		if(propertyField == null)
			propertyField = ReflectionUtil.getVisibleField(clazz, propertyName);
		
		if(propertyField != null) {
			propertyClass = propertyField.getType();
			propertyType = propertyField.getGenericType();
			lObject = propertyField.getAnnotation(LObject.class);
			lTermConverter = propertyField.getAnnotation(LTermConverter.class);
			lObjectConverter = propertyField.getAnnotation(LObjectConverter.class);
		}
			
		if(propertyClass != null)
			propertyGetter = BeansUtil.getterInHierarchy(clazz,  propertyName, propertyClass);
		else {
			propertyGetter = BeansUtil.getterInHierarchy(clazz,  propertyName);
			if(propertyGetter != null) {
				propertyClass = propertyGetter.getReturnType();
				propertyType = propertyGetter.getGenericReturnType();
			}
		}
		
		if(propertyGetter != null) {
			if(lObject == null)
				lObject = propertyGetter.getAnnotation(LObject.class);
			if(lTermConverter == null) 
				lTermConverter = propertyGetter.getAnnotation(LTermConverter.class);
			if(lObjectConverter == null)
				lObjectConverter = propertyGetter.getAnnotation(LObjectConverter.class);
		}
		
		if(propertyClass != null)
			propertySetter = BeansUtil.setterInHierarchy(clazz,  propertyName, propertyClass);
		else {
			propertySetter = BeansUtil.setterInHierarchy(clazz,  propertyName);
			if(propertySetter != null) {
				propertyClass = propertySetter.getParameterTypes()[0];
				propertyType = propertySetter.getGenericParameterTypes()[0];
			}
		}
		
		if(propertySetter != null) {
			if(lObject == null)
				lObject = ReflectionUtil.getParameterAnnotation(propertySetter, 0, LObject.class);
			if(lTermConverter == null) 
				lTermConverter = ReflectionUtil.getParameterAnnotation(propertySetter, 0, LTermConverter.class);
			if(lObjectConverter == null)
				lObjectConverter = ReflectionUtil.getParameterAnnotation(propertySetter, 0, LObjectConverter.class);
		}
	}

	public LObject getLObject() {
		return lObject;
	}

	public LTermConverter getLTermConverter() {
		return lTermConverter;
	}

	public LObjectConverter getLObjectConverter() {
		return lObjectConverter;
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
