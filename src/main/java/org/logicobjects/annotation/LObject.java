package org.logicobjects.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Target({ElementType.TYPE, ElementType.FIELD}) //TODO, add compile time support for ElementType.LOCAL_VARIABLE (this annotations are not available at runtime)
@Retention(RetentionPolicy.RUNTIME)
public @interface LObject {
	String name() default "";  //the name of the logic object
	String[] args() default {}; //the properties of the Java object acting as arguments of the logic object
	/*
	 * if set, the properties are not given by different instance variables (one per property) but for an array keeping all of them.
	 * Still properties names can be declared and used as macros, but they will refer (according to their declaration order) to positions on this array
	 */
	String argsList() default "";
	String[] imports() default {};
	String[] modules() default {};
	boolean automaticImport() default true;

 	//Class adapter() default NO_ADAPTER.class;
	//String[] adapterParameters() default {};
}
