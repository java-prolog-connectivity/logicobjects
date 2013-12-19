package org.logicobjects.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.logicobjects.descriptor.LogicObjectDescriptor;

/**
 * This annotation specifies which is the Logtalk object receiving the message in the event of a logic method invocation.
 * Outside the logic method invocation scenario, this annotation is ignored.
 * @author scastro
 *
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
//TODO Maybe choose a better id ? e.g. LMethodDispatcher, LMethodInvoker, LProxyObject, LPseudoObject ...
public @interface LDelegationObject {
	String name() default "";  //the id of the logic object
	String[] args() default {}; //the properties of the Java object acting as arguments of the logic object
	String[] imports() default {};
	String[] modules() default {};
	boolean automaticImport() default true;
	boolean referenceTerm() default false;
	int termIndex() default LogicObjectDescriptor.DEFAULT_TERM_INDEX;
}
