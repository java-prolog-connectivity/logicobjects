package org.logicobjects.util;


import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jpl.Term;
import jpl.Util;
import jpl.Variable;


/**
 * Utility class for interacting with a Prolog engine
 * @author sergioc78
 *
 */
public class LogicUtil {

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
			System.out.println(matched);
			String replacement = matched.substring(0, 1) + "_" + matched.substring(1);
			System.out.println("Start index: " + matcher.start());
			System.out.println(" End index: " + matcher.end() + " ");
			
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
			System.out.println(matched);
			String replacement = matched.substring(0, 1) + "_" + matched.substring(1);
			System.out.println("Start index: " + matcher.start());
			System.out.println(" End index: " + matcher.end() + " ");
			
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

			/*
			System.out.println(matched);
			System.out.println(replacement);
			System.out.println(matcher.group());
			System.out.print("Start index: " + matcher.start());
			System.out.print(" End index: " + matcher.end() + " ");
			*/
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	
	
	protected static String surround(String atom, String functor) {
		return functor+"("+atom+")";
	}
	/*
	public static boolean usePrologNativeModule(String moduleName) {
		//e.g., lists, charsio
		 Query useModule = new Query(surround(surround(moduleName, "library"), "use_module"));
		 return useModule.hasSolution();
	}
	*/

	
	public static String[] solutionsForVars(Hashtable[] solutions, Variable var) {
		String[][] allVarSolutionsAux = solutionsForVars(solutions, new Variable[] {var});
		String[] allVarSolutions = new String[allVarSolutionsAux.length];
		
		for(int i=0; i<allVarSolutions.length; i++) {
			allVarSolutions[i] = allVarSolutionsAux[i][0];
		}
		return allVarSolutions;
	}
	
	public static String[][] solutionsForVars(Hashtable[] solutions, Variable[] vars) {
		int numberOfSolutions = solutions.length;
		int numberOfVars = vars.length;
		String[][] solutionsTable = new String[numberOfSolutions][numberOfVars];
		
		for(int i = 0; i<numberOfSolutions; i++) 
			for(int j = 0; j<numberOfVars; j++)
				solutionsTable[i][j] = solutionForVar(solutions[i], vars[j]);
		
		return solutionsTable;
	}
	
	protected static String solutionForVar(Hashtable solution, Variable var) {
		return solution.get(var.toString()).toString();
	}
	
	protected static String[] unquote(String quotedStrings[]) {
		String[] unquotedStrings = new String[quotedStrings.length];
		for(int i = 0; i<quotedStrings.length; i++) {
			unquotedStrings[i] = unquote(quotedStrings[i]);
		}
		return unquotedStrings;
	}
	
	protected static String unquote(String s) {
		return s.substring(1, s.length()-1);
	}
	

	
	
	
	
	public static Term termArrayToList(Term[] terms) {
		return Util.termArrayToList(terms);
	}
	
	public static Term textToTerm(String text) {
		return Util.textToTerm(text);
	}
	
	public static Term stringArrayToList(String[] a) {
		return Util.stringArrayToList(a);
	}
	
	public static Term intArrayToList(int[] a) {
		return Util.intArrayToList(a);
	}
	
	public static int listToLength(Term t) {
		return Util.listToLength(t);
	}
	
	public static Term[] listToTermArray(Term t) {
		return Util.listToTermArray(t);
	}
	
	public static String[] atomListToStringArray(Term t) {
		return Util.atomListToStringArray(t);
	}
	
	public static void main(String[] args) {
		String prologName = "xml_uml_fast_translator";
		String javaName = "XML_UMLFastTranslator";
		
		System.out.println(prologNameToJava(prologName));
		System.out.println(prologObjectNameToJava(prologName));
		System.out.println(javaNameToProlog(javaName));
		System.out.println(javaClassNameToProlog(javaName));
	}
	
}
