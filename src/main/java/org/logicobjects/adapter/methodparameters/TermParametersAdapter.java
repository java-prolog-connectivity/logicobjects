package org.logicobjects.adapter.methodparameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jpl.Term;

import org.logicobjects.adapter.LogicObjectAdapter;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.core.LogicEngine;

/**
 * 
 * @author sergioc78
 *
 */
public class TermParametersAdapter extends ParametersAdapter {

	private Object targetObject;
	
	private static final String THIS_OBJECT = "this";
	
	private static final String INSTANCE_VAR_PREFIX = "@";
	private static final String INSTANCE_VAR_PREFIX_REX = "\\@";
	
	private static final String PARAMETERS_PREFIX = "$";
	private static final String PARAMETERS_PREFIX_REX = "\\$";
	
	private static final String ALL_PARAMS_SUFIX = "*";
	private static final String ALL_PARAMS_SUFIX_REX = "\\*";
	
	private static final String PARAM_SEPARATOR = "~~~";
	
	public static final String JAVA_NAME = "([a-zA-Z_\\$][\\w\\$]*)";
	
	/*
	private static final String paramSymbolAsRex(String symbol) {
		String sufix = symbol.substring(1);
		if(sufix.equals(ALL_PARAMS_SUFIX))
			sufix = ALL_PARAMS_SUFIX_REX;
		return PARAMETERS_PREFIX_REX+sufix;
	}
	*/
	public TermParametersAdapter(Object targetObject) {
		this.targetObject = targetObject;
	}
	
	@Override
	public Object[] adapt(Object[] oldParams) {
		LogicEngine engine = LogicEngine.getDefault();

		String concatenatedParams = concatenateParams(); //concatenates the adapter parameters
		
		/**
		 * Substitute parameters symbols from their values in the method parameters
		 * $1 corresponds to the first parameter
		 * $* corresponds to all the parameters separated by a ','
		 */
		String replacedConcatenatedParams = replaceParameters(concatenatedParams, oldParams); 
		
		//once the params symbols have been replaced, we split them again
		Object[] newParamStrings = replacedConcatenatedParams.split(PARAM_SEPARATOR);
		
		//we convert the string representation of every param in a term
		List<Term> newTermParams = new ArrayList<Term>();
		for(Object stringTerm : newParamStrings) {
			newTermParams.add(engine.textToTerm(stringTerm.toString()));
		}
		return newTermParams.toArray(new Term[] {});
	}

	/**
	 * 
	 * @return a String with all the adapter parameters. Values are separated by PARAM_SEPARATOR
	 */
	private String concatenateParams() {
		Object[] params = getParameters();
		String concatenatedParams = "";
		for(int i=0; i<params.length; i++) {
			concatenatedParams+=params[i];
			if(i<params.length-1)
				concatenatedParams+=PARAM_SEPARATOR;
		}
		return concatenatedParams;
	}
	
	/**
	 * 
	 * @param termString a string with all the new params concatenated
	 * @param params the method params
	 * @return
	 */
	public String replaceParameters(String termString, Object[] params) {
		LogicEngine engine = LogicEngine.getDefault();
		
		Set<String> setSymbols = getParametersSymbols(termString); //find out which are the parameters symbols referred in the parametersString

		//Map<String, Term> parametersDictionary = asTermParametersMap(setSymbols, params);
		Map<String, String> parametersDictionary = asReplacementMap(params, setSymbols);
		
		for(String symbol : setSymbols) {			
			String termObjectString = parametersDictionary.get(symbol);
			//termString=termString.replaceAll(paramSymbolAsRex(paramVar), termObjectString);
			termString=termString.replaceAll(Pattern.quote(symbol), termObjectString);
		}
		
		return termString;
	}
	
	/**
	 * 
	 * @param concatenatedParams
	 * @return A set of symbol params referenced in the string sent as a parameter
	 */
	private Set<String> getParametersSymbols(String concatenatedParams) {
		Set<String> setSymbols = new HashSet<String>();
		//Pattern pattern = Pattern.compile(PARAMETERS_PREFIX_REX+"[\\d]+");
		Pattern pattern = Pattern.compile("("+PARAMETERS_PREFIX_REX+"(\\d+|"+ALL_PARAMS_SUFIX_REX+"))|"+INSTANCE_VAR_PREFIX_REX+JAVA_NAME);
		//Pattern pattern = Pattern.compile("("+PARAMETERS_PREFIX_REX+"(\\d+|"+ALL_PARAMS_SUFIX_REX+"))");
		Matcher findingMatcher = pattern.matcher(concatenatedParams);
		while(findingMatcher.find()) {
			String match = findingMatcher.group();
			setSymbols.add(match);
		}
		return setSymbols;
	}

	/**
	 * Builds a dictionary of symbol params to values
	 * @param parameters
	 * @return
	 */
	private Map<String, String> asReplacementMap(Object[] parameters, Set<String> setSymbols) {
		Map<String, String> dictionary = new HashMap<String, String>();
		if(parameters.length > 0) {
			LogicEngine engine = LogicEngine.getDefault();
			List<Term> listTerms = new ArrayList<Term>();
			for(int i = 0; i<parameters.length; i++) {
				Term termParam = ObjectToTermAdapter.asTerm(parameters[i]);
				if(engine.nonAnonymousVariablesNames(termParam).size() > 0)
					throw new RuntimeException("Parameter objects cannot contain free non-anonymous variables: "+termParam);//in order to avoid name collisions
				
				String paramName = PARAMETERS_PREFIX+(i+1);
				dictionary.put(paramName, termParam.toString());
				listTerms.add(termParam);
			}
			dictionary.put(PARAMETERS_PREFIX+ALL_PARAMS_SUFIX, engine.termListToTextSequence(listTerms));
		}
		for(String setSymbol : setSymbols) {
			if(setSymbol.substring(0, 1).equals(INSTANCE_VAR_PREFIX)) {
				String instanceVarName = setSymbol.substring(1);
				Term instanceVarAsTerm;
				if(instanceVarName.equals(THIS_OBJECT)) {
					instanceVarAsTerm = new ObjectToTermAdapter().adapt(targetObject);
				} else {
					instanceVarAsTerm = LogicObjectAdapter.fieldAsTerm(targetObject, instanceVarName); //TODO this method should be in ObjectToTermAdapter
				}
				
				dictionary.put(setSymbol, instanceVarAsTerm.toString());
			}
		}
		return dictionary;
	}
	
	/*
	private static Map<String, Term> asTermParametersMap(Set<String> requiredParamNames, Object[] parameters) {
		Map<String, Term> dictionary = new HashMap<String, Term>();
		for(String paramName : requiredParamNames) {
			int indexParam = Integer.valueOf(paramName.substring(PARAMETERS_PREFIX.length()))-1;
			Term termParam = ObjectToTermAdapter.asTerm(parameters[indexParam]);
			dictionary.put(paramName, termParam);	
		}
		return dictionary;
	}
	*/
	
	
	
}

