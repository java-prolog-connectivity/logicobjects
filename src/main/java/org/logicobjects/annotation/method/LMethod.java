package org.logicobjects.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.util.AnnotationConstants;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LMethod {
	
	String name() default "";
	String[] args() default {AnnotationConstants.NULL};  //if no parameters are explicitly added, the logic method will have the same parameters than the Java method

	public static class LMethodUtil {
		public static String[] getArgs(LMethod aLMethod) {
			return AnnotationConstants.isNullArray(aLMethod.args())?null:aLMethod.args();
		}
	}
}
