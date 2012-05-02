package org.logicobjects.util;

/**
 * Helper class to denote null values for default annotation attributes
 * (This workaround is necessary since null is not a possible value when declaring default values in annotations)
 * @author sergioc78
 *
 */
public class AnnotationConstants {

	/*
	 * Class used as a default for annotation attributes indicating an Adapter class
	 * This trick is needed since null values are not allowed in annotation attributes
	 */
	public static final class NO_ADAPTER {}
	
	public static final String NULL = "_NULL_CONSTANT_";
	
	public static boolean isNullArray(String[] array) {
		return array.length == 1 && array[0].equals(NULL);
	}
	
}
