package org.logicobjects.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.adapter.methodresult.eachsolution.SolutionToLObjectAdapter;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LSolution {
	Class adapter() default SolutionToLObjectAdapter.class;
	String value() default "";
}
