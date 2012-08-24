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
	String[] args() default {}; //the arguments of the logic object

	String[] imports() default {};
	String[] modules() default {};
	boolean automaticImport() default true;

 	//Class adapter() default NO_ADAPTER.class;
	//String[] adapterParameters() default {};
}
