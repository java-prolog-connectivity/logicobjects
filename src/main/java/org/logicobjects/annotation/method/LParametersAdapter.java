package org.logicobjects.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)  //TODO we could apply this to TYPEs also
@Retention(RetentionPolicy.RUNTIME)
public @interface LParametersAdapter {
	Class adapter();
	String[] args() default {};
}
