package org.logicobjects.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.converter.descriptor.LogicObjectDescriptor;

@Target({ElementType.TYPE, ElementType.FIELD}) //TODO, add compile time support for ElementType.LOCAL_VARIABLE (this annotations are not available at runtime)
@Retention(RetentionPolicy.RUNTIME)
public @interface LObject {
	String name() default "";  //the id of the logic object
	String[] args() default {}; //the properties of the Java object acting as arguments of the logic object
	String[] imports() default {};
	String[] modules() default {};
	boolean automaticImport() default true;
	boolean referenceTerm() default false;
	int termIndex() default LogicObjectDescriptor.DEFAULT_TERM_INDEX;
}
