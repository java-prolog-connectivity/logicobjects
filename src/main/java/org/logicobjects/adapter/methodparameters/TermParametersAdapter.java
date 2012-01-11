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

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.core.LogicEngine;

public class TermParametersAdapter extends ParametersAdapter {

	private static final String PARAMETERS_PREFIX = "$";
	
	private static final String paramSymbolAsRex(String symbol) {
		return "\\"+symbol;
	}
	
	@Override
	public Object[] adapt(Object[] oldParams) {
		
		LogicEngine engine = LogicEngine.getDefault();
		
		Object[] newParamStrings = getParameters();
		String concatenatedParams = "";
		final String separator = "~~~";
		for(int i=0; i<newParamStrings.length; i++) {
			concatenatedParams+=newParamStrings[i];
			if(i<newParamStrings.length-1)
				concatenatedParams+=separator;
		}

		concatenatedParams = replaceParameters(concatenatedParams, oldParams);

		
		
		
		newParamStrings = concatenatedParams.split(separator);

		
		List<Term> newTermParams = new ArrayList<Term>();
		for(Object stringTerm : newParamStrings) {
			newTermParams.add(engine.textToTerm(stringTerm.toString()));
		}
		return newTermParams.toArray(new Term[] {});
	}

	
	
	
	public static String replaceParameters(String termString, Object[] params) {
		LogicEngine engine = LogicEngine.getDefault();
		
		Set<String> setSymbols = getParametersSymbols(termString);

		//Map<String, Term> parametersDictionary = asTermParametersMap(setSymbols, params);
		Map<String, String> parametersDictionary = asReplacementMap(params);
		
		for(String paramVar : setSymbols) {			
			String termObjectString = parametersDictionary.get(paramVar);
			termString=termString.replaceAll(paramSymbolAsRex(paramVar), termObjectString);
		}
		
		return termString;
	}
	
	
	public static Set<String> getParametersSymbols(String concatenatedParams) {
		Set<String> setSymbols = new HashSet<String>();
		Pattern pattern = Pattern.compile("\\"+PARAMETERS_PREFIX+"[\\d]+"); //warning: weak implementation: this assumes that PARAMETERS_PREFIX needs to be escaped
		Matcher findingMatcher = pattern.matcher(concatenatedParams);
		while(findingMatcher.find()) {
			String match = findingMatcher.group();
			setSymbols.add(match);
		}
		return setSymbols;
	}


	private static Map<String, String> asReplacementMap(Object[] parameters) {
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
			dictionary.put(PARAMETERS_PREFIX+"0", engine.termListToTextSequence(listTerms));
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
	
	/*
	public static void main(String[] args) {
		LogicEngine e = new BootstrapLogicEngine();
		Term termSequence = Util.textToTerm("A=B, B=C, 1=D");
		System.out.println(e.sequenceLength(termSequence));
		
		Term[] terms = e.sequenceToTermArray(termSequence);
		for(Term t : terms) {
			System.out.println(t.toString());
		}
	}
*/
	
}

