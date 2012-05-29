package org.logicobjects.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.adapter.methodresult.solutioncomposition.SmartWrapperAdapter;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

/*
 * Indicates the wrapper adapter of a logic method. Wrapper adapters abstract many common usage patterns of logic methods 
 */
public @interface LWrapper {
	Class adapter() default SmartWrapperAdapter.class;
}
