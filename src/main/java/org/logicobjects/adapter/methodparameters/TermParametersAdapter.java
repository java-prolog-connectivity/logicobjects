package org.logicobjects.adapter.methodparameters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import jpl.Term;

import org.logicobjects.adapter.LogicObjectAdapter;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.core.LogicEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Symbols used to express parameters:
 * 
 * (Term symbols)
 * $NUMBER : a java method parameter as a term (e.g., '$1' means the first parameter)
 * $* : all the java method parameters as terms
 * @this : the object declaring the method as term
 * @propertyName : a bean property of the object as term
 * 
 * (Java object symbols)
 * ! : a suffix to the previous symbols. If added, the object will not be transformed to a term, but will be passed to Logtalk as a java  object
 * & : same as '!', but with delayed evaluation
 * @author sergioc78
 *
 */
public class TermParametersAdapter extends ParametersAdapter {
	private static Logger logger = LoggerFactory.getLogger(TermParametersAdapter.class);

	private Object targetObject;
	private Method targetMethod;

	/*
	private static final String paramSymbolAsRex(String symbol) {
		String sufix = symbol.substring(1);
		if(sufix.equals(ALL_PARAMS_SUFIX))
			sufix = ALL_PARAMS_SUFIX_REX;
		return PARAMETERS_PREFIX_REX+sufix;
	}
	*/
	public TermParametersAdapter(Object targetObject, Method targetMethod) {
		this.targetObject = targetObject;
		this.targetMethod = targetMethod;
	}
	
	@Override
	public Object[] adapt(Object[] oldParams) {
		LogicEngine engine = LogicEngine.getDefault();

		String concatenatedParams = ParametersParser.concatenateParams(getParameters()); //concatenates the adapter parameters
		
		/**
		 * Substitute parameters symbols from their values in the method parameters
		 * $1 corresponds to the first parameter
		 * $* corresponds to all the parameters separated by a ','
		 */
		String replacedConcatenatedParams = replaceSymbolsAndExpressions(concatenatedParams, oldParams); 
		
		//once the params symbols have been replaced, we split them again
		Object[] newParamStrings = ParametersParser.splitConcatenatedParams(replacedConcatenatedParams);
		
		//we convert the string representation of every param in a term
		List<Term> newTermParams = new ArrayList<Term>();
		for(Object stringTerm : newParamStrings) {
			newTermParams.add(engine.textToTerm(stringTerm.toString()));
		}
		return newTermParams.toArray(new Term[] {});
	}


	/**
	 * 
	 * @param termString a string with all the new params concatenated
	 * @param oldParams the java method params
	 * @return
	 */
	public String replaceSymbolsAndExpressions(String termString, Object[] oldParams) {
		List<String> setSymbols = ParametersParser.getAllSymbols(termString); //find out which are the parameters symbols referred in the parametersString

		Map<String, String> symbolsMap = symbolsReplacementMap(oldParams, setSymbols);
		Map<String, String> expressionsMap = expressionsReplacementMap(termString);

		//replacing symbols
		for(String symbol : setSymbols) {			
			String termObjectString = symbolsMap.get(symbol);
			termString=termString.replaceAll(Pattern.quote(symbol), termObjectString);
		}
		//replacing java expressions for the result of a method invocation
		for(Entry<String, String> entry : expressionsMap.entrySet()) {
			try {
				String replacementValue;
				String methodName = entry.getValue();
				if(methodName == null || methodName.isEmpty())
					replacementValue = "";
				else {
					Method method = targetObject.getClass().getMethod(methodName);
					Object expressionResult = method.invoke(targetObject); //result contains the value of the java expression
					Term expressionAsTerm = ObjectToTermAdapter.asTerm(expressionResult);
					replacementValue = expressionAsTerm.toString();
				}
				termString=termString.replaceAll(Pattern.quote(entry.getKey()), replacementValue);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return termString;
	}


	/**
	 * Builds a dictionary of symbol params to values
	 * @param parameters the java method parameters
	 * @return
	 */
	private Map<String, String> symbolsReplacementMap(Object[] parameters, List<String> setSymbols) {
		Map<String, String> dictionary = new HashMap<String, String>();
		if(parameters.length > 0) {
			LogicEngine engine = LogicEngine.getDefault();
			List<Term> listTerms = new ArrayList<Term>();
			boolean allParamsRequired = setSymbols.contains(ParametersParser.ALL_PARAMS_SYMBOL);
			for(int i = 0; i<parameters.length; i++) {
				String paramName = ParametersParser.parameterSymbol(i+1);
				if(allParamsRequired || setSymbols.contains(paramName)) {
					Term termParam = ObjectToTermAdapter.asTerm(parameters[i]);
					if(engine.nonAnonymousVariablesNames(termParam).size() > 0)
						throw new RuntimeException("Parameter objects cannot contain free non-anonymous variables: "+termParam);//in order to avoid name collisions
					dictionary.put(paramName, termParam.toString());
					listTerms.add(termParam);
				}
			}
			if(allParamsRequired)
				dictionary.put(ParametersParser.ALL_PARAMS_SYMBOL, engine.termListToTextSequence(listTerms));
		}
		if(setSymbols.contains(ParametersParser.THIS_SYMBOL)) {
			Term thisAsTerm = new ObjectToTermAdapter().adapt(targetObject);
			dictionary.put(ParametersParser.THIS_SYMBOL, thisAsTerm.toString());
		}
		
		for(String setSymbol : setSymbols) {
			if(ParametersParser.isInstancePropertySymbol(setSymbol)) {
				String instanceVarName = ParametersParser.getPropertyName(setSymbol);
				Term instanceVarAsTerm = instanceVarAsTerm = LogicObjectAdapter.fieldAsTerm(targetObject, instanceVarName); //TODO this method should be in ObjectToTermAdapter
				dictionary.put(setSymbol, instanceVarAsTerm.toString());
			}
		}
		return dictionary;
	}
	
	private Map<String, String> expressionsReplacementMap(String termString) {
		return expressionsReplacementMap(termString, targetMethod);
	}
	
	
	private static Map<String, String> expressionsReplacementMap(String termString, Method method) {
		Map<String, String> expressionsReplacementMap = new HashMap<String, String>();
		List<String> delimitedExpressions = ParametersParser.getJavaExpressions(termString);
		
		for(int i = 0; i<delimitedExpressions.size(); i++) {
			String delimitedExpression = delimitedExpressions.get(i);
			String expression = ParametersParser.getExpressionValue(delimitedExpression);
			String substitutionValue;
			if(ParametersParser.isValidExpression(expression)) {
				substitutionValue = ParametersParser.methodNameForExpression(method, i+1);//i+1 to work with a 1-based index
			} else {
				logger.warn("The expression: " + delimitedExpression + "in the method "+ method.toGenericString()+" is not valid. It will be ignored.");
				substitutionValue = "";
			}
			expressionsReplacementMap.put(delimitedExpression, substitutionValue);
		}
		return expressionsReplacementMap;
	}

	
	public static Map<String, String> generatedMethodsMap(Method method) {
		String queryString = null;
		LMethod aLMethod = method.getAnnotation(LMethod.class);
		if(aLMethod != null) {
			queryString = ParametersParser.concatenateParams(aLMethod.parameters());
		} else {
			LQuery aLQuery = method.getAnnotation(LQuery.class);
			if(aLQuery != null) {
				queryString = aLQuery.value();
			} else {
				throw new RuntimeException("The method " + method.getName() + "is not either a logic method or a logic query");
			}
		}
		
		Map<String, String> expressionsReplacementMap = expressionsReplacementMap(queryString, method);
		Map<String, String> generatedMethodsMap = new HashMap<String, String>();
		for(Entry<String, String> entry : expressionsReplacementMap.entrySet()) {
			String expression = entry.getKey();
			expression = ParametersParser.getExpressionValue(expression);
			if(ParametersParser.isValidExpression(expression)) {
				expression = ParametersParser.normalizeExpression(expression);
				String methodName = entry.getValue();
				generatedMethodsMap.put(methodName, expression);
			}
		}
		return generatedMethodsMap;
	}

}

