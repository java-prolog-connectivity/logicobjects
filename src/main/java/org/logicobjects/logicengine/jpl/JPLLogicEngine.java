package org.logicobjects.logicengine.jpl;

import org.logicobjects.logicengine.LogicEngine;
import org.logicobjects.term.Query;
import org.logicobjects.term.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPLLogicEngine extends LogicEngine {

	private static Logger logger = LoggerFactory.getLogger(JPLLogicEngine.class);
	

	@Override
	public Query createQuery(Term term) {
		return new JPLQueryAdapter(term);
	}

	@Override
	public Term asTerm(String termString) {
		jpl.Term jplTerm = jpl.Util.textToTerm(termString);
		JPLToLogicObjectsVisitor jplToLogicObjectsVisitor = new JPLToLogicObjectsVisitor();
		return (Term) jplToLogicObjectsVisitor.transform(jplTerm);
	}

	/*
	@Override
	public boolean halt() {
		logger.info("Shutting down the prolog engine ...");
		boolean result = hasSolution(new Atom("halt")); //WARNING: apparently there is a bug in JPL that makes the Java process dye at this point.
		if(result)
			logger.info("The prolog engine has been shut down.");
		else
			logger.warn("Impossible to shut down the prolog engine.");
		return result;
	}
	*/
}
