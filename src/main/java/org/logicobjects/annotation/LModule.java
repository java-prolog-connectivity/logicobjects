package org.logicobjects.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO missing support for this annotation
/**
 * If an object has this annotation, all its abstract methods will be considered Prolog queries instead of Logtalk methods (no receiver will be created)
 * @author scastro
 *
 */
@Target({ElementType.TYPE, ElementType.FIELD}) //TODO, add compile time support for ElementType.LOCAL_VARIABLE (this annotations are not available at runtime)
@Retention(RetentionPolicy.RUNTIME)
public @interface LModule {
	String[] modules() default {};
	boolean automaticImport() default true;
}
