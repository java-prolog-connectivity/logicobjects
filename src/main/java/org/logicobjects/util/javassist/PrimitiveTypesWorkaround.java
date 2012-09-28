package org.logicobjects.util.javassist;

/**
 * 
 * This class is a workaround to the issue that Javassist works often with the java 1.4 specification.
 * This is useful in certain method generation routines, where the generated method returns an object from a given expression
 * This class just converts primitive types to objects
 * TODO find an equivalent in Guava or something like that
 *
 */
public class PrimitiveTypesWorkaround {
	
	public static Object toObject(Object o) {
		return o;
	}
	
	public static Object toObject(boolean o) {
		return o;
	}
	
	public static Object toObject(byte o) {
		return o;
	}
	
	public static Object toObject(char o) {
		return o;
	}
	
	public static Object toObject(short o) {
		return o;
	}
	
	public static Object toObject(int o) {
		return o;
	}
	
	public static Object toObject(long o) {
		return o;
	}
	
	public static Object toObject(float o) {
		return o;
	}
	
	public static Object toObject(Double o) {
		return o;
	}
	
	/*
	public static Object toObject(Object o) {
		return o;
	}
	
	public static Object toObject(boolean o) {
		return Boolean.valueOf(o);
	}
	
	public static Object toObject(byte o) {
		return Byte.valueOf(o);
	}
	
	public static Object toObject(char o) {
		return Character.valueOf(o);
	}
	
	public static Object toObject(short o) {
		return Short.valueOf(o);
	}
	
	public static Object toObject(int o) {
		return Integer.valueOf(o);
	}
	
	public static Object toObject(long o) {
		return Long.valueOf(o);
	}
	
	public static Object toObject(float o) {
		return Float.valueOf(o);
	}
	
	public static Object toObject(Double o) {
		return Double.valueOf(o);
	}
	*/

}
