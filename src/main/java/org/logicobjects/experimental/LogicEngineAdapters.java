package org.logicobjects.experimental;

import org.logicobjects.adapter.methodparameters.MethodArgumentsAdapter;

public class LogicEngineAdapters {
	
	public static class PrologFlagAdapters {
		public static class PrologFlag_ParametersAdapter extends MethodArgumentsAdapter {
			/*
			@Override
			public Object[] adapt(Object[] source) {
				return new Object[] {source[0], "_","?Bindings"}
			}*/
		}
		public static class PrologFlag_ResultAdapter {
			
		}
	}
}
