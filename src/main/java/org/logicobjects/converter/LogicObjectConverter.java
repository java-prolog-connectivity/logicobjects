package org.logicobjects.converter;

import java.lang.annotation.Annotation;

import org.jpc.converter.JpcConverter;
import org.logicobjects.core.LogicClass;

public class LogicObjectConverter extends JpcConverter {

	protected <A extends Annotation> A getMappingAnnotationGuidingClass(Class<A> annotationClass) {
		A annotation = null;
		Class guidingClass = getGuidingClass();
		if(guidingClass != null) {
			annotation = (A) guidingClass.getAnnotation(annotationClass);
		}
		return annotation;
	}
	
	protected <A extends Annotation> A getMappingAnnotation(Class<A> annotationClass) {
		A annotation = getMappingAnnotationLocalContext(annotationClass);
		if(annotation == null && !shouldIgnoreHierarchyMappingAnnotations()) {
			annotation = (A) getMappingAnnotationGuidingClass(annotationClass);
		}
		return annotation;
	}
	
	/**
	 * 
	 * @return a boolean indicating if there are mapping annotations that should be found in the hierarchy
	 * by default, if the guiding class (the first class in the hierarchy identified as a logic class) implements ITermObject then any annotation present in classes in the hierarchy should be ignored 
	 */
	public boolean shouldIgnoreHierarchyMappingAnnotations() {
		return getGuidingClass() != null && LogicClass.isTermObjectClass(getGuidingClass());
	}
}
