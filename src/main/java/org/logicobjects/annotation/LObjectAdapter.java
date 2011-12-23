package org.logicobjects.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Defines an adapter for transforming a term into an object
 */
@Target({ElementType.TYPE, ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface LObjectAdapter {
	Class adapter();
	String[] args() default {};
}
