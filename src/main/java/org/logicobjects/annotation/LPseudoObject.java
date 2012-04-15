package org.logicobjects.annotation;

/**
 * This annotation specify which is the logtalk object receiving the message of declared logic methods
 * However, when converting this class into a term, this annotation will be ignored
 * A class should not be annotated at the same time with LObject and LPseudoObject
 * @author sergioc78
 *
 */
//TODO Maybe choose a better name ? LMethodDispatcher, LMethodInvoker, ...
public @interface LPseudoObject {
	String name() default "";  //the name of the logic object
	String[] params() default {}; //the parameters of the logic object

	String[] imports() default {};
	String[] modules() default {};
	boolean automaticImport() default true;
}
