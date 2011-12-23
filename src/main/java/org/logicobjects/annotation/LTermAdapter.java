package org.logicobjects.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 *  Defines an adapter for transforming an object into a term
 */
@Target({ElementType.TYPE, ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface LTermAdapter {
	Class adapter();
	String[] args() default {};
}
