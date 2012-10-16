package org.logicobjects.util;

import static org.logicobjects.term.Compound.newCompound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.core.flags.LogtalkFlag;
import org.logicobjects.logicengine.LogicEngine;
import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.logicobjects.term.Atom;
import org.logicobjects.term.Compound;
import org.logicobjects.term.FloatTerm;
import org.logicobjects.term.IntegerTerm;
import org.logicobjects.term.LException;
import org.logicobjects.term.Query;
import org.logicobjects.term.Term;
import org.logicobjects.term.Variable;

/**
 * An utility class for general purpose queries and term manipulation
 * DISCLAIMER: In the current version many methods in this class have been copied or adapted from the class jpl.Util in the JPL library.
 * @author scastro
 *
 */
public class LogicUtil {
	
	private LogicEngine engine;

	public LogicUtil(LogicEngineConfiguration engineConfig) {
		this(engineConfig.getEngine());
	}
	
	public LogicUtil(LogicEngine engine) {
		this.engine = engine;
	}

	//STATIC UTILITY METHODS
	public static Term termsToList(List<Term> terms) {
		return termsToList(terms.toArray(new Term[]{}));
	}
	
	/**
	 * Converts an array of Terms to a Prolog list term
	 * whose members correspond to the respective array elements.
	 * 
	 * @param   terms  An array of Term
	 * @return  Term   a list of the array elements
	 */
	public static Term termsToList(Term... terms) {
		Term list = new Atom("[]");

		for (int i = terms.length - 1; i >= 0; --i) {
			list = new Compound(".", Arrays.asList(terms[i], list));
		}
		return list;
	}
	
	/**
	 * Converts an array of String to a corresponding Term list
	 * 
	 * @param a
	 *            An array of String objects
	 * @return Term a Term list corresponding to the given String array
	 */
	public static Term stringsToList(String... a) {
		Term list = new Atom("[]");
		for (int i = a.length - 1; i >= 0; i--) {
			list = new Compound(".", Arrays.asList(new Atom(a[i]), list));
		}
		return list;
	}
	
	/**
	 * Converts an array of int to a corresponding term list
	 * 
	 * @param a
	 *            An array of int values
	 * @return a term list corresponding to the given int array
	 */
	public static Term intsToList(int... a) {
		Term list = new Atom("[]");
		for (int i = a.length - 1; i >= 0; i--) {
			list = new Compound(".", Arrays.asList(new IntegerTerm(a[i]), list));
		}
		return list;
	}
	
	/**
	 * Converts an array of arrays of int to a corresponding list of lists
	 * 
	 * @param a
	 *            An array of arrays of int values
	 * @return a term list of lists corresponding to the given int array of arrays
	 */
	public static Term intTableToList(int[][] a) {
		Term list = new Atom("[]");
		for (int i = a.length - 1; i >= 0; i--) {
			list = new Compound(".", Arrays.asList(intsToList(a[i]), list));
		}
		return list;
	}
	
	
	public static int listToLength(Term t) {
		int length = 0;
		Term head = t;
		while (head.hasFunctor(".", 2)) {
			length++;
			head = head.arg(2);
		}
		return (head.hasFunctor("[]", 0) ? length : -1);
	}
	
	/** converts a proper list to an array of terms, else throws an exception
	 * 
	 * @throws LException
	 * @return an array of terms whose successive elements are the corresponding members of the list (if it is a list)
	 */
	public static List<Term> listToTerms(Term t) {
		try {
			int len = t.listLength();
			Term[] ts = new Term[len];

			for (int i = 0; i < len; i++) {
				ts[i] = t.arg(1);
				t = t.arg(2);
			}
			return Arrays.asList(ts);
		} catch (LException e) {
			throw new LException("term " + t + " is not a proper list");
		}
	}
	
	public static List<String> atomListToStrings(Term t){
		int n = listToLength(t);
		String[] a = new String[n];
		int i = 0;
		Term head = t;
		while ( head.hasFunctor(".", 2)){
			Term x = head.arg(1);
			if ( x.isAtom()){
				a[i++]=((Atom)x).name();
			} else {
				return null;
			}
			head = head.arg(2);
		}
		return (head.hasFunctor("[]", 0) ? Arrays.asList(a) : null );
	}
	
	/**
	 * Surround an atom with a functor
	 * @param atom
	 * @param functor
	 * @return
	 */
	public static Term applyFunctor(String functor, String atom) {
		return applyFunctor(functor, new Atom(atom));
	}
	
	public static Term applyFunctor(String functor, Term term) {
		return new Compound(functor, Arrays.asList(term));
	}
	
	public static List<Term> forAllApplyFunctor(String functor, List<Term> terms) {
		List<Term> appliedFunctorTerms = new ArrayList<>();
		for(Term term : terms) {
			appliedFunctorTerms.add(applyFunctor(functor, term));
		}
		return appliedFunctorTerms;
	}
	
	
	public static String javaClassNameToProlog(String javaClassName) {
		String prologName = javaNameToProlog(javaClassName);
		String start = prologName.substring(0, 1);
		return start.toLowerCase() + prologName.substring(1);
	}
	
	public static String prologObjectNameToJava(String prologObjectName) {
		String javaName = prologNameToJava(prologObjectName);
		String start = javaName.substring(0, 1);
		return start.toUpperCase() + javaName.substring(1);
	}
	
	/*
	 * Transforms from camel case to prolog like names
	 */
	public static String javaNameToProlog(String javaName) {
		/*
		 * capital letters that do not have at the left:
		 * 	- another capital letter
		 *  - beginning of line
		 *  - an underscore
		 */
		Pattern pattern = Pattern.compile("[^^A-Z_][A-Z]");  
		Matcher matcher = pattern.matcher(javaName);
		
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String matched = matcher.group();
			String replacement = matched.substring(0, 1) + "_" + matched.substring(1);
			matcher.appendReplacement(sb,replacement);
		}
		matcher.appendTail(sb);

		/*
		 * capital letters that have at the left:
		 * - another capital letter
		 * and have at the right:
		 *  - a non capital letter
		 */
		pattern = Pattern.compile("[A-Z][A-Z][a-z]");  
		matcher = pattern.matcher(sb.toString());
		
		sb = new StringBuffer();
		while (matcher.find()) {
			String matched = matcher.group();
			String replacement = matched.substring(0, 1) + "_" + matched.substring(1);
			matcher.appendReplacement(sb,replacement);
		}
		matcher.appendTail(sb);
		
		String start = sb.toString().substring(0,1);
		return start + sb.toString().substring(1).toLowerCase(); //will not modify the case of the first character
	}
	
	public static String prologNameToJava(String prologName) {
		Pattern pattern = Pattern.compile("_(\\w)");
		Matcher matcher = pattern.matcher(prologName);
		
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String matched = matcher.group(1);
			String replacement = matched.toUpperCase();
			matcher.appendReplacement(sb,replacement);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	
	
	
	
	//UTILITY METHODS DEPENDING ON A LOGIC ENGINE
	public Query createQuery(Term term) {
		return engine.createQuery(term);
	}
	
	public Query createQuery(String termString) {
		return createQuery(asTerm(termString));
	}
	
	public Term asTerm(String termString) {
		return engine.asTerm(termString);
	}
	
	public List<Term> asTerms(List<String> termsString) {
		List<Term> terms = new ArrayList<>();
		for(String s : termsString)
			terms.add(asTerm(s));
		return terms;
	}

	public boolean assertTerms(List<Term> terms) {
		return engine.assertTerms(terms);
	}
	
	public boolean ensureLoaded(List<Term> resources) {
		return engine.ensureLoaded(resources);
	}
	
	public boolean ensureLoaded(String... resources) {
		return ensureLoaded(asTerms(Arrays.asList(resources)));
	}

	public boolean allSucceed(List<Term> terms) {
		return engine.allSucceed(terms);
	}
	
	public boolean hasSolution(Term term) {
		return engine.hasSolution(term);
	}
	
	public Map<String, Term> oneSolution(Term term) {
		return engine.oneSolution(term);
	}
	
	public List<Map<String, Term>> allSolutions(Term term) {
		return engine.allSolutions(term);
	}
	
	public boolean hasSolution(String queryString) {
		return engine.hasSolution(asTerm(queryString));
	}
	
	public Map<String, Term> oneSolution(String queryString) {
		return engine.oneSolution(asTerm(queryString));
	}
	
	public List<Map<String, Term>> allSolutions(String queryString) {
		return engine.allSolutions(asTerm(queryString));
	}
	
	public String currentPrologFlag(String flagName) {
		return engine.currentPrologFlag(flagName);
	}
	
	public String prologDialect() {
		return engine.prologDialect();
	}
	
	public boolean cd(String path) {
		Compound compound = Compound.newCompound("cd", new Atom(path));
		return hasSolution(compound);
	}

	
	/**
	 * Answers an array of anonymous logic variables
	 * @param n the number of variables in the array
	 * @return
	 */
	public static List<Variable> anonymousVariables(int n) {
		List<Variable> variablesList = new ArrayList<>();
		for(int i=0; i<n; i++) {
			variablesList.add(Variable.ANONYMOUS_VAR);
			//variablesList.add(engine.getAnonymousVariable());
		}
		return variablesList;
	}
	


	public boolean isBinaryOperator(String op) {
		return hasSolution("Op='" + op + "', current_op(_, Type, Op), atom_chars(Type, Chars), Chars=[_, f, _]");
	}
	
	public boolean isUnaryOperator(String op) {
		return hasSolution("Op='" + op + "', current_op(_, Type, Op), atom_chars(Type, Chars), Chars=[f, _]");
	}
	
	public boolean flushOutput() {
		return engine.flushOutput();
	}
	
	public Term termsToSequence(List<Term> terms) {
		String sequenceString = termsToTextSequence(terms);
		return asTerm(sequenceString);
	}
	
	public String termsToTextSequence(List<Term> terms) {
		String sequenceString = "";
		for(int i = 0; i<terms.size(); i++) {
			sequenceString += terms.get(i).toString();
			if(i<terms.size()-1)
				sequenceString += ", ";
		}
		return sequenceString;
	}
	
	public List<Term> sequenceAsTerms(Term termSequence) {
		int len = sequenceLength(termSequence);
		Term[] ts = new Term[len];
		for (int i = 0; i < len; i++) {
			if(i<len-1) {
				ts[i] = termSequence.arg(1);
				termSequence = termSequence.arg(2);
			} else
				ts[i] = termSequence;
		}
		return Arrays.asList(ts);
	} 

	public int sequenceLength(Term sequence) {
		int length = 1;
		if(sequence.isCompound()) {
			if(sequence.hasFunctor(",", 2))
				length = 1 + sequenceLength(sequence.arg(2));
		}
		return length;
	}
	
	
	
	
	//LOGTALK methods

	
	public boolean logtalkLoad(String... resources) {
		return logtalkLoad(asTerms(Arrays.asList(resources)));
	}
	
	public boolean logtalkLoad(List<Term> resourceTerms) {
		return allSucceed(forAllApplyFunctor("logtalk_load", resourceTerms));
	}
	
	public boolean setLogtalkFlag(LogtalkFlag flag, String value) {
		return hasSolution(newCompound("set_logtalk_flag", new Atom(flag.toString()), new Atom(value)));
	}
	
	@LMethod(name="current_object", args = {"LogtalkObject"})
	public List<Term> currentLogtalkObjects() {
		List<Term> currentObjects = new ArrayList<>();
		Variable logtalkObjectVar = new Variable("LogtalkObject");
		Compound compound = Compound.newCompound("current_object", logtalkObjectVar);
		for(Map<String, Term> solution : allSolutions(compound)) {
			currentObjects.add(solution.get(logtalkObjectVar.name()));
		}
		return currentObjects;
	}
	
	/**
	 * 
	 * @param object
	 * @return a list of arities of all the Logtalk objects in the logic side having as name the parameter of the Java method
	 */
	//currently assuming that the cardinalities of the objects in the logtalk side are returned ordered from the lowest to the highest
	public List<Integer> numberParametersLogtalkObject(String object) {
		List<Term> currentObjects = currentLogtalkObjects();
		List<Integer> numberParams = new ArrayList<>();
		for(Term currentObject: currentObjects) {
			if(currentObject instanceof Atom) {
				Atom atom = (Atom)currentObject;
				if(atom.name().equals(object))
					numberParams.add(0);
			} else if(currentObject instanceof Compound) {
				Compound compound = (Compound)currentObject;
				if(compound.name().equals(object))
					numberParams.add(compound.arity());
			}
		}
		return numberParams;
	}
	
	/*
	public static boolean usePrologNativeModule(String moduleName) {
		//e.g., lists, charsio
		 Query useModule = new Query(surround(surround(moduleName, "library"), "use_module"));
		 return useModule.hasSolution();
	}
	*/

/*
	public String[] solutionsForVars(Hashtable[] solutions, Variable var) {
		String[][] allVarSolutionsAux = solutionsForVars(solutions, new Variable[] {var});
		String[] allVarSolutions = new String[allVarSolutionsAux.length];
		
		for(int i=0; i<allVarSolutions.length; i++) {
			allVarSolutions[i] = allVarSolutionsAux[i][0];
		}
		return allVarSolutions;
	}
	

	public String[][] solutionsForVars(Hashtable[] solutions, Variable[] vars) {
		int numberOfSolutions = solutions.length;
		int numberOfVars = vars.length;
		String[][] solutionsTable = new String[numberOfSolutions][numberOfVars];
		
		for(int i = 0; i<numberOfSolutions; i++) 
			for(int j = 0; j<numberOfVars; j++)
				solutionsTable[i][j] = solutionForVar(solutions[i], vars[j]);
		
		return solutionsTable;
	}
	
	protected String solutionForVar(Hashtable solution, Variable var) {
		return solution.get(var.toString()).toString();
	}
	
	protected String[] unquote(String quotedStrings[]) {
		String[] unquotedStrings = new String[quotedStrings.length];
		for(int i = 0; i<quotedStrings.length; i++) {
			unquotedStrings[i] = unquote(quotedStrings[i]);
		}
		return unquotedStrings;
	}
	
	protected static String unquote(String s) {
		return s.substring(1, s.length()-1);
	}
	*/
	
	
	public static String toString(Term term) {
		if(term.isInteger())
			return ""+((IntegerTerm)term).longValue();
		else if(term.isFloat())
			return ""+((FloatTerm)term).doubleValue();
		else if(term.isAtom())
			return ((Atom)term).name();
		else
			return term.toString();
	}
	
	public static int toInt(Term term) {
		return (int) toLong(term);
	}
	
	public static long toLong(Term term) {
		if(term.isInteger())
			return ((IntegerTerm)term).longValue();
		else if(term.isFloat())
			return (long) toDouble(term);
		else if(term.isAtom())
			return Long.valueOf(((Atom)term).name());
		else
			throw new LException("Impossible to convert the term " + term + " to a long");
	}
	
	public static float toFloat(Term term) {
		return (float) toDouble(term);
	}
	
	public static double toDouble(Term term) {
		if(term.isFloat())
			return ((FloatTerm)term).doubleValue();
		else if(term.isAtom())
			return Double.valueOf(((Atom)term).name());
		else
			throw new LException("Impossible to convert the term " + term + " to a double");
	}
	
	public static Number toNumber(Term term) {
		if(term.isInteger())
			return toLong(term);
		if(term.isFloat())
			return toDouble(term);
		else if(term.isAtom())
			return Double.valueOf(((Atom)term).name());
		else
			throw new LException("Impossible to convert the term " + term + " to a number");
	}
	
	public static List<Term> getChildren(Term term) {
		if(term.isCompound()) {
			if(term.isList())
				return listToTerms(term);
			else
				return term.args();
		} else
			return Collections.emptyList();
	}

}
