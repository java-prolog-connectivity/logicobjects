package org.jpc.termvisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jpc.term.Term;

public abstract class AbstractTransformationVisitor<From, To> {


	
	public To transform(From source) {
		List<From> sourceChildren = getChildren(source);
		List<To> targetChildren = new ArrayList();
		for(From sourceChild : sourceChildren)
			targetChildren.add(transform(sourceChild));
		return doTransform(source, targetChildren);
	}

	protected abstract To doTransform(From source, List<To> transformedChildren);
	
	protected List<From> getChildren(From source) {
		return Collections.emptyList();
	}


}
