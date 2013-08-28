package org.logicobjects.converter.context;

import org.jpc.Jpc;

public abstract class AnnotatedContextFactory {

	protected Jpc jpcContext;

	public AnnotatedContextFactory(Jpc jpcContext) {
		this.jpcContext = jpcContext;
	}
	
}
