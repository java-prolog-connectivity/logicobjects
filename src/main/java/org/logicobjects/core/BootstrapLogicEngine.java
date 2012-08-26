package org.logicobjects.core;

import java.util.List;

import jpl.Term;


public class BootstrapLogicEngine extends LogicEngine {
/*
	private static final ThreadLocal<BootstrapLogicEngine> bootstrapEngine = new ThreadLocal <BootstrapLogicEngine> () {
		@Override 
		protected BootstrapLogicEngine initialValue() {
			return new BootstrapLogicEngine();
	    }
	};
	
	public synchronized static BootstrapLogicEngine getDefault() {
		return bootstrapEngine.get();
	}
	*/
/*
	protected BootstrapLogicEngine() {
	}
*/
	



	
	@Override
	public String currentPrologFlag(String flagName) {
		if(true) throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public boolean flushOutput() {
		if(true) throw new UnsupportedOperationException();
		return false;
	}

	@Override
	public List<Term> currentObjects() {
		if(true) throw new UnsupportedOperationException();
		return null;
	}
	

}
