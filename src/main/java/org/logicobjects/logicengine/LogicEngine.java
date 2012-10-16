package org.logicobjects.logicengine;

import static org.logicobjects.term.Atom.FALSE_TERM;
import static org.logicobjects.term.Atom.TRUE_TERM;
import static org.logicobjects.term.Variable.ANONYMOUS_VAR;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.logicobjects.core.flags.PrologFlag;
import org.logicobjects.term.Atom;
import org.logicobjects.term.Compound;
import org.logicobjects.term.Query;
import org.logicobjects.term.Term;
import org.logicobjects.term.Variable;
import org.logicobjects.util.LogicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class LogicEngine {

	private static Logger logger = LoggerFactory.getLogger(LogicEngine.class);
	
	//protected static final String VARIABLE_PREFIX = "LOGIC_OBJECTS_"; //prefix for generated framework variables 
	
	public static boolean isAnonymousVariableName(String variableName) {
		return variableName.substring(0, 1).equals("_"); //the variable name is equals to "_" or starts with "_"
	}
	
	public Term getTrueTerm() {
		return TRUE_TERM;
	}
	
	public Term getFalseTerm() {
		return FALSE_TERM;
	}
	
	public Variable getAnonymousVariable() {
		return ANONYMOUS_VAR;
	}
	
	public boolean halt() {
		throw new UnsupportedOperationException();
	}
	
	public boolean assertTerms(List<Term> terms) {
		return allSucceed(LogicUtil.forAllApplyFunctor("assert", terms));
	}

	public boolean ensureLoaded(List<Term> resourceTerms) {
		return allSucceed(LogicUtil.forAllApplyFunctor("ensure_loaded", resourceTerms));
	}
	
	public boolean allSucceed(List<Term> terms) {
		boolean success = true;
		for(Term term: terms) {
			if(!hasSolution(term))
				success = false;
		}
		return success;
	}
	
	public boolean hasSolution(Term term) {
		return createQuery(term).hasSolution();
	}
	
	public Map<String, Term> oneSolution(Term term) {
		return createQuery(term).oneSolution();
	}
	
	public List<Map<String, Term>> allSolutions(Term term) {
		return createQuery(term).allSolutions();
	}
	


	public String currentPrologFlag(String flagName) {
		String flagValue = null;
		Variable varFlag = new Variable("Var");
		Map<String, Term> solutions = oneSolution(new Compound("current_prolog_flag", Arrays.asList(new Atom(flagName), varFlag)));
		if(solutions!=null) {
			Atom flagValueTerm = (Atom) solutions.get(varFlag.name());
			flagValue = flagValueTerm.name();
		}
		return flagValue;
	}
	
	public String prologDialect() {
		return currentPrologFlag(PrologFlag.DIALECT);
	}
	
	public boolean flushOutput() {
		return hasSolution(new Atom("flush_output"));
	}

	public abstract Query createQuery(Term term);

	/**
	 * 
	 * @param termString
	 * @return the term representation of a String. Variable names should be preserved.
	 */
	public abstract Term asTerm(String termString);
	
}
