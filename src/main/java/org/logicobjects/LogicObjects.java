package org.logicobjects;

/**
 * 
 * @author scastro
 *
 */
public class LogicObjects {

	public static <T> T newLogicObject(Class<T> clazz, Object... params) {
		return newLogicObject(null, clazz, params);
	}
	
	public static <T> T newLogicObject(Object declaringObject, Class<T> clazz, Object... params) {
		return null;
	}
	
}
