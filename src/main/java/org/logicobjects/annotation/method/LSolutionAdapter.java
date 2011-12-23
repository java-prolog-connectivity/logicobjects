package org.logicobjects.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/*
 * Provides a Composition Adapter for this method. Composition adapters abstract many common usage patterns of logic methods 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LSolutionAdapter {

	Class adapter();
	String[] args() default {};
	
}
