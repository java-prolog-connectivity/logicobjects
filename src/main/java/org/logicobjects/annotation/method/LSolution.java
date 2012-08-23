package org.logicobjects.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.adapter.methodresult.eachsolution.SolutionToLObjectAdapter;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LSolution {
	String value() default ""; //a string representation of a solution
	Class adapter() default SolutionToLObjectAdapter.class; //the adapter interpreting the solution
	//String wrap(); //yes, no, infer (default)
	//String type(); //variable, signature, infer (default)
}
