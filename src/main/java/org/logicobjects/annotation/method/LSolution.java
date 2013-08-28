package org.logicobjects.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.methodadapter.methodresult.eachsolution.SolutionToLObjectAdapter;
import org.logicobjects.util.AnnotationConstants.NULL;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LSolution {
	String value() default ""; //a string representation of a solution
	Class adapter() default SolutionToLObjectAdapter.class; //the adapter interpreting one solution (a solution is a set of frames binding variables to terms)
	//String wrap(); //yes, no, infer (default)
	//String type(); //variable, signature, infer (default)
	Class preferedClass() default NULL.class; //TODO this needs to be finished
}
