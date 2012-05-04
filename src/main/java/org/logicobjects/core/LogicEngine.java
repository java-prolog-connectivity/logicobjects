package org.logicobjects.core;

import static org.logicobjects.core.flags.PrologFlag.DIALECT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jpl.Atom;
import jpl.Compound;
import jpl.JPL;
import jpl.Query;
import jpl.Term;
import jpl.Util;
import jpl.Variable;

import org.logicobjects.adapter.ObjectToTermException;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.core.flags.LogtalkFlag;
import org.logicobjects.util.LogicObjectsPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * A LogicObjects engine
 * There is just one engine by process (this is constrained by JPL)
 */
@LObject(name = "user") //the LogicEngine is itself a logic object. That is the reason of the existence of a BootstrapLogicEngine
public abstract class LogicEngine {

	private static Logger logger = LoggerFactory.getLogger(LogicEngine.class);
	
	//CONSTANTS
	public static Term TRUE_TERM = new Atom("true");
	public static Term FALSE_TERM = new Atom("false");
	
	protected static final String VARIABLE_PREFIX = "LOGIC_OBJECTS_"; //prefix for generated framework variables 
	protected static final Variable anonymousVar = new Variable("_");

	//ENGINES AND PREFERENCES STATIC VARIABLES
	private static boolean bootstrapping;
	private static LogicEngine coreEngine;
	private static LogicEngine bootstrapEngine;
	private static LogicObjectsPreferences preferences;


	private String workingPath;
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//BOOTSTRAPPING
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	protected static LogicObjectsPreferences getPreferences() {
		return preferences;
	}
	
	public static void configure() {
		configure(new LogicObjectsPreferences());
	}
	
	/**
	 * The minimum configuration needed to load the framework in memory
	 * This method is static since it configures how the logic engine will be instantiated
	 */
	protected static void configure(LogicObjectsPreferences preferences) {
		JPL.setNativeLibraryDir(preferences.findOrDie(LogicObjectsPreferences.JPLPATH)); //configuring the JPL path according to preferences. So a Prolog engine can be started
	}
	
	public synchronized static void initialize(LogicObjectsPreferences newPreferences) {
		preferences = newPreferences;
		configure(preferences);
		
		//the following block of code attempts to change the existing Prolog engine configuration according to new preferences
		//this has not been possible to implement until now. Preferences are only applied if the logic engine has not yet been started.
		//Left the code instead of deleting it hoping to find something that works in the future.
		try {
			//if JPL.init() is not explicitly called, it will be invoked by JPL at the first operation needing the engine
			 //in theory this method answers true if the logic engine was not started before the call, and false otherwise.
			 //in practice, if the logic engine has already been started this will throw an unrecoverable exception
			 //something similar to this problem described here: http://stackoverflow.com/questions/9496617/java-error-native-library-already-loaded-in-another-classloader
			 boolean alreadyStarted = !JPL.init(); 
			if(alreadyStarted) {
				getDefault().halt(); //There is a serious bug with this method (see its comments). So it is not possible to restart the logic engine :(
				JPL.init();
				getDefault().resetPath();
			}
			
		} catch(Error e) {
			throw new RuntimeException(e); //the exception originated when attempting to initialize the logic engine when it was already started cannot be caught (ClassNotFound exception)
		}
		
	}

	/**
	 * The Bootrap engine does not trigger the initialization of Logtalk
	 * 
	 * @return The Bootstrap engine
	 */
	public synchronized static LogicEngine getBootstrapEngine() {
		if(bootstrapEngine == null)
			bootstrapEngine = new BootstrapLogicEngine();
		return bootstrapEngine;
	}

	public synchronized static LogicEngine getDefault() {
		if(bootstrapping) {
			return getBootstrapEngine();
		} else {
			if(coreEngine == null) {
				logger.info("Bootstrapping " + LogicObjectsPreferences.LOGIC_OBJECTS_NAME + " ... ");
				long startTime = System.nanoTime();
				bootstrapping = true;

				getBootstrapEngine().loadLogtalk();
				//getBootstrapEngine().setLogtalkFlag(LogtalkFlag.REPORT, LogtalkFlag.REPORT.OFF); //currently this is set in the settings.lgt file
				coreEngine = LogicObjectFactory.getDefault().create(LogicEngine.class);
				bootstrapping = false;
				bootstrapEngine = null;
				long endTime = System.nanoTime();
				long total = (endTime - startTime)/1000000;
				logger.info("Done in " + total + " milliseconds");
			}
			return coreEngine;
		}
	}

	/**
	 * The constructor should not be invoked by external classes
	 */
	protected LogicEngine() {
		if(getPreferences() == null)
			initialize(new LogicObjectsPreferences());
	}


	public boolean resetPath() {
		if(workingPath != null)
			return cd(workingPath);
		return true;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//METHODS NEEDED FOR THE BOOTSTRAPPING PROCESS AND FOR SUPPORTING THE ADAPTERS FUNCTIONALITY
//NONE OF THESE METHODS SHOULD BE INSTRUMENTED, TO PREVENT INFINITE LOOPS
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	
	public String prologDialect() {
		Variable varVersion = new Variable("VVersion");
		Query versionQuery = new Query("current_prolog_flag", new Term[] { new Atom(DIALECT), varVersion });
		Map solutions = versionQuery.oneSolution();
		return ((Atom)solutions.get(varVersion.name())).name();
	}
	
	/*
	 * Boostrap method
	 */
	public boolean loadLogtalk() {
		//System.out.println("PROLOG DIALECT: "+prologDialect());
		String scriptPath = getPreferences().logtalkIntegrationScript();
		//System.out.println("Loading Logtalk initialization script: "+ scriptPath);
		return ensureLoaded(scriptPath);
	}
	
	public boolean setLogtalkFlag(LogtalkFlag flag, String value) {
		Query setFlagQuery = new Query("set_logtalk_flag", new Term[] { new Atom(flag.toString()), new Atom(value) });
		return setFlagQuery.hasSolution();
	}
	
	public Term textToTerm(String text) {
		Term term = null;
		try {
			term = Util.textToTerm(text);
		} catch(Exception e) {
			throw new ObjectToTermException(text);
		}
		return term;
	}
	
	public List<String> nonAnonymousVariablesNames(Term term) {
		List<String> variablesNames = new ArrayList<String>();
		for(String varName : termVariablesNames(term)) {
			if(!varName.substring(0, 1).equals("_"))
				variablesNames.add(varName);
		}
		return variablesNames;
	}
	/*
	@LSolutionAdapter(adapter=SolutionToTermAdapter.class, args={"Names"})
	@RawQuery("atom_to_term('$1', _, Map), findall(Name, list::member(Name=_, Map), Names)")
	public abstract List<String> termVariablesNames(Term term);
	*/
	
	public List<String> variableNames(String s) {
		return termVariablesNames(textToTerm(s));
	}
	
	public List<String> termVariablesNames(Term term) {
		String varMappingVarName = VARIABLE_PREFIX+"VarMapping";
		Query query = new Query(new Compound("atom_to_term", new Term[] { new Atom(term.toString()), anonymousVar, new Variable(varMappingVarName)}));
		Map solution = query.oneSolution();
		Term varMappingTerm = (Term) solution.get(varMappingVarName);
		Term[] varBindings = Util.listToTermArray(varMappingTerm);
		List<String> varNames = new ArrayList<String>(); 
		for(Term binding : varBindings) {
			Atom varNameAtom = (Atom) ((Compound)binding).arg(1);
			varNames.add(varNameAtom.name());
		}
		return varNames;
	}
	/*
	private Term[] atomToTerm(String atom) {
		String varMappingVarName = VARIABLE_PREFIX+"VarMapping";
		String termVarName = VARIABLE_PREFIX+"Term";
		Query query = new Query(new Compound("atom_to_term", new Term[] { new Atom(term.toString()), new Variable(termVarName), new Variable(varMappingVarName)}));
		Map solution = query.oneSolution();
		Term varMappingTerm = (Term) solution.get(varMappingVarName);
		Term[] varBindings = Util.listToTermArray(varMappingTerm);
		return null;
	}
	*/
	
	/*
	 * TODO
	 * change this method so it will return the text representation of a Term according to how Prolog will output the term in a console
	 * Also, variable names should be preserved
	 * Example: the String representation of the term ','(A,B) should be "A, B"
	 * The current implementation does not preserve variable names
	 */
	public String termToText(Term term) {
		String varAtomName = VARIABLE_PREFIX+"Atom";
		Query query = new Query(new Compound("term_to_atom", new Term[] { term, new Variable(varAtomName)}));
		Map solution = query.oneSolution();
		Atom varMappingTerm = (Atom) solution.get(varAtomName);
		return varMappingTerm.name();
	}
	
	//WARNING: apparently there is a bug in JPL that makes the Java process dye at this point.
	@Deprecated
	public boolean halt() {
		logger.info("Shutting down the prolog engine ...");
		boolean result = new Query("halt").hasSolution();
		if(result)
			logger.info("The prolog engine has been shut down.");
		else
			logger.warn("Impossible to shut down the prolog engine.");
		return result;
	}
	
	public boolean ensureLoaded(String module) {
		Term term;
		try {
			term = textToTerm(module);
		} catch(Exception e) {
			term = new Atom(module);
		}
		return ensureLoaded(term);
	}
	
	/*@LSolutionAdapter(adapter=HasSolutionAdapter.class)
	@LMethod(name="flush_output")*/
	public boolean flushOutput() {
		return new Query("flush_output").hasSolution();
	}
	
	public boolean ensureLoaded(Term term) {
		return new Query("ensure_loaded", new Term[] { term }).hasSolution();
	}
	
	/*
	 * @return boolean indicating if all the loads were successful
	 */
	public boolean ensureLoaded(List<Term> terms) {
		boolean success = true;
		for(Term term : terms) {
			if (!ensureLoaded(term) )
				success = false;
		}
		return success;
	}
	
	public boolean logtalkLoad(String module) {
		Term term;
		try {
			term = textToTerm(module);
		} catch(Exception e) {
			term = new Atom(module);
		}
		return logtalkLoad(term);
	}
	
	public boolean logtalkLoad(Term term) {
		return new Query("logtalk_load", new Term[] { term }).hasSolution();
	}
	/*
	public boolean logtalkLoad(String fileName, String loadingModifiers) {
		Query query = new Query("logtalk_load", new Term[] { new Atom(fileName), new Atom(loadingModifiers) });
		return query.hasSolution();
	}
	*/
	
	/*
	 * @return boolean indicating if all the loads were successful
	 */
	public boolean logtalkLoad(List<Term> terms) {
		boolean success = true;
		for(Term term : terms) {
			if (!logtalkLoad(term) )
				success = false;
		}
		return success;
	}
	
	public boolean cd(String newDirectory) {
		Query query = new Query("cd", new Term[] { new Atom(newDirectory) });
		try {
			if(query.hasSolution()) {
				logger.info("Changed Prolog path to: "+newDirectory);
				workingPath = newDirectory;
				return true;
			} else {
				logger.warn("Impossible to change path to: " + newDirectory);
				return false;
			}
			
		} catch(Exception e) {
			throw new RuntimeException("Impossible change to directory "+newDirectory);
		}
	}
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//LOGIC METHODS THAT FOR CONVENIENT REASONS HAVE BEEN IMPLEMENTED INSTEAD OF INSTRUMENTED
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public boolean isList(Term term) {
		if (term instanceof Compound) {
			String termName = ((Compound)term).name();
			return (termName.equals(".") || termName.equals("[]"));
		}
		return false;
	}

	public boolean isUnification(Term term) {
		if (term instanceof Compound) {
			return ((Compound) term).name().equals("=");
		}
		return false;
	}
	
	public boolean bound(Term term) {
		return termVariablesNames(term).size() == 0;
	}
	
	public Term termListToSequence(List<Term> termList) {
		String sequenceString = termListToTextSequence(termList);
		return textToTerm(sequenceString);
	}
	
	public String termListToTextSequence(List<Term> termList) {
		Term[] terms = termList.toArray(new Term[]{});
		String sequenceString = "";
		for(int i = 0; i<terms.length; i++) {
			sequenceString += terms[i].toString();
			if(i<terms.length-1)
				sequenceString += ", ";
		}
		return sequenceString;
	}
	
	public List<Term> sequenceToTermList(Term termSequence) {
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
		if (sequence.hasFunctor(",", 2)) {
			return 1 + sequenceLength(sequence.arg(2));
		} else {
			return 1;
		} 
	}
	
	public static boolean isBinaryOperator(String op) {
		Atom atomOp = new Atom(op);
		Query query = new Query("Op='?', current_op(_, Type, Op), atom_chars(Type, Chars), Chars=[_, f, _]", new Term[] {atomOp});
		return query.hasSolution();
	}
	
	public static boolean isUnaryOperator(String op) {
		Atom atomOp = new Atom(op);
		Query query = new Query("Op='?', current_op(_, Type, Op), atom_chars(Type, Chars), Chars=[f, _]", new Term[] {atomOp});
		return query.hasSolution();
	}
	
	
	public static Term[] getChildren(Term term) {
		if(term instanceof Compound) {
			if(LogicEngine.getDefault().isList(term))
				return Util.listToTermArray(term);
			else
				return term.args();
		} else
			return new Term[] {};
	}
	

	
	public static void main(String[] args) {
		System.out.println("***********************************");
		LogicEngine eng = getDefault();
		String t = "intensional_set(set1_2, '', '.'(var1, []), '.'(A, []), '::(list, member(A, \'.\'(1, \'.\'(2, []))))')";
		System.out.println(eng.textToTerm(t));
		try {
			eng.halt();
		} catch(Exception e) {
			System.out.println(e);
		}
		
		System.out.println("***********************************");
		//System.out.println(eng.textToTerm(t));
		System.out.println("***********************************");
		
		/*
		List<Term> termList = Arrays.asList(e.textToTerm("hola(Z,X,Y,s(Z))"), e.textToTerm("(a,b)"));
		System.out.println("termListToTextSequence: "+ e.termListToTextSequence(termList));
		Term term = e.termListToSequence(termList);
		System.out.println(((Compound)term).name());
		
		//Term term = e.textToTerm("A,B,(C,D)");
		
		System.out.println("term to text: "+e.termToText(term));
		Compound c = new Compound("x", new Term[] {term});
		System.out.println(c);
		System.out.println("Compound to text: "+e.termToText(c));
*/

	}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//INSTRUMENTED METHODS
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@LSolution("FlagValue")
	@LMethod(name="current_prolog_flag", params={"$1", "FlagValue"})
	public abstract String currentPrologFlag(String flagName);

}


