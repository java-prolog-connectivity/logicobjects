package org.logicobjects.util.termvisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractTransformationVisitor {

	public Object transform(Object source) {
		List sourceChildren = getChildren(source);
		List targetChildren = new ArrayList();
		for(Object sourceChild : sourceChildren)
			targetChildren.add(transform(sourceChild));
		return doTransform(source, targetChildren);
	}

	protected abstract  Object doTransform(Object source, List transformedChildren);
	
	protected List getChildren(Object source) {
		return Collections.emptyList();
	}



}
