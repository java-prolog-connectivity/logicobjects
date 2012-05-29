package org.logicobjects.instrumentation;

/**
 * Symbols used to express parameters:
 * 
 * (Term symbols)
 * $NUMBER : a java method parameter as a term (e.g., '$1' means the first parameter)
 * $$ : all the java method parameters as terms. The parameters are separated by a ','
 * @this : the object declaring the method as term
 * @propertyName : a bean property of the object as term
 * 
 * (Java object symbols)
 * ! : a suffix to the previous symbols. If added, the object will not be transformed to a term, but will be passed to Logtalk as a java  object
 * & : same as '!', but with delayed evaluation
 * @author sergioc78
 *
 */
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jpl.Term;

import org.logicobjects.adapter.LogicObjectAdapter;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.core.AbstractLogicMethod;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicMethod;
import org.logicobjects.core.RawLogicQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auxiliary class providing operations for parsing logic method parameters
 * @author sergioc78
 *
 */
public abstract class AbstractLogicMethodParser<LM extends AbstractLogicMethod> {
	private static Logger logger = LoggerFactory.getLogger(AbstractLogicMethodParser.class);

	public static final String generatedMethodsPrefix = "$logicobjects_";
	
	public static final String BEGIN_JAVA_EXPRESSION = "/{";
	public static final String END_JAVA_EXPRESSION = "/}";
	
	
	public static final String INSTANCE_PROPERTY_PREFIX = "@";
	//private static final String INSTANCE_PROPERTY_PREFIX_REX = "\\@";
	
	public static final String PARAMETERS_PREFIX = "$";
	//private static final String PARAMETERS_PREFIX_REX = "\\$";
	
	public static final String THIS_SUFFIX = "0";
	
	public static final String THIS_SYMBOL = PARAMETERS_PREFIX + THIS_SUFFIX;
	
	public static final String ALL_PARAMS_SUFFIX = "$";
	//private static final String ALL_PARAMS_SUFFIX_REX = "\\*";
	
	public static final String ALL_PARAMS_SYMBOL = PARAMETERS_PREFIX + ALL_PARAMS_SUFFIX;
	
	public static final String JAVA_NAME_REX = "([a-zA-Z_\\$][\\w\\$]*)";
	
	public static final String HIDDEN_EXPRESSION_PREFIX = "~HIDDEN_EXPRESSION~_";
	
	/*
	 * the question mark is to specify a reluctant quantifier, so 'any' characters (the '.') will occur the minimum possible amount of times
	 */
	public static final String DELIMITED_JAVA_REX = Pattern.quote(BEGIN_JAVA_EXPRESSION) + "(.*?)" + Pattern.quote(END_JAVA_EXPRESSION);
	
	public static final String PARAMETERS_SEPARATOR = "~~~";
	
	public static final String PARAMETERS_TAG = "~PARAMETERS~";
	
	public static final String QUERY_TAG = "~QUERY~";
	
	public static final String RETURN_TAG = "~RET~";

	

	
	
	private LM logicMethod;
	private String allLogicStrings;
	private List<String> foundSymbols;
	private List<String> expressions;
	private Map<String, String> expressionsReplacementMap;
	private String eachSolutionValue;

	private ParsingData unparsedData;
	
	public static AbstractLogicMethodParser create(Method method) {
		return new AbstractLogicMethodParser(method) {};
		/*
		if(LogicMethod.isLogicMethod(method))
			return new LogicMethodParser(method);
		else
			return new RawQueryParser(method);
		*/
	}
	
	AbstractLogicMethodParser(Method method) {
		logicMethod = (LM) AbstractLogicMethod.create(method);
		unparsedData = logicMethod.getDataToParse();
		preParsing();
	}

	public AbstractLogicMethodParser preParsing() {
		String paramsLogicString = concatenateTokens(getParametersToParse());
		allLogicStrings = QUERY_TAG + getQueryToParse() + PARAMETERS_TAG + paramsLogicString + RETURN_TAG + getSolutionToParse();
		foundSymbols = getAllSymbols(allLogicStrings);
		expressions = getJavaExpressions(allLogicStrings);
		expressionsReplacementMap = expressionsReplacementMap(expressions);
		return this;
	}


	public String getQueryToParse() {
		if(unparsedData.getQueryString() == null)
			return "";
		else
			return unparsedData.getQueryString();
	}
	
	public String[] getParametersToParse() {
		if(unparsedData.getParameters() == null)
			return new String[]{};
		else
			return unparsedData.getParameters();
	}
	
	public String getSolutionToParse() {
		if(unparsedData.getSolutionString() == null)
			return "";
		else
			return unparsedData.getSolutionString();
	}
	
	public LM getLogicMethod() {
		return logicMethod;
	}

	public Method getMethod() {
		return logicMethod.getWrappedMethod();
	}



	public ParsingData parsedData(Object targetObject, Object[] oldParams) {
		String allLogicStringsProcessed = replaceSymbolsAndExpressions(allLogicStrings, foundSymbols, expressionsReplacementMap, targetObject, oldParams);
		ParsingData parsedData = decomposeLogicString(allLogicStringsProcessed);
		return parsedData;
	}

	public static ParsingData decomposeLogicString(String allLogicStringsProcessed) {
		ParsingData parsedData = new ParsingData();
		Pattern pattern = Pattern.compile(Pattern.quote(QUERY_TAG)+"(.*)"+Pattern.quote(PARAMETERS_TAG)+"(.*)"+Pattern.quote(RETURN_TAG)+"(.*)");
		Matcher matcher = pattern.matcher(allLogicStringsProcessed);
		matcher.find();
		String queryString = matcher.group(1);
		if(!queryString.isEmpty())
			parsedData.setQueryString(queryString);
		String params = matcher.group(2);
		if(!params.isEmpty())
			parsedData.setParameters(splitConcatenatedTokens(params));
		String result = matcher.group(3);
		if(!result.isEmpty())
			parsedData.setSolutionString(result);
		return parsedData;
	}
	
	/*
	public String[] resolveInputTokens(Object targetObject, Object[] oldParams) {
		String inputString = allLogicStrings.split(RETURN_SEPARATOR)[0];
		inputString = replaceSymbolsAndExpressions(inputString, foundSymbols, expressionsReplacementMap, targetObject, oldParams);
		return splitConcatenatedTokens(inputString);
	}
	
	public String resolveEachSolutionValue(Object targetObject, Object[] oldParams) {
		if(eachSolutionValue == null)
			return null;
		String solvedEachSolutionValue = replaceSymbolsAndExpressions(eachSolutionValue, foundSymbols, expressionsReplacementMap, targetObject, oldParams);
		return solvedEachSolutionValue;
	}
*/
	private static String concatenateTokens(Object[] tokens) {
		String concatenatedParams = "";
		for(int i=0; i<tokens.length; i++) {
			concatenatedParams+=tokens[i];
			if(i<tokens.length-1)
				concatenatedParams+=PARAMETERS_SEPARATOR;
		}
		return concatenatedParams;
	}
	
	private static String[] splitConcatenatedTokens(String tokensString) {
		return tokensString.split(PARAMETERS_SEPARATOR);
	}
	
	
	
	/**
	 * 
	 * @param pos a 1-based index of the java parameter
	 * @return the parameter symbol representation
	 */
	public static String parameterSymbol(int pos) {
		return PARAMETERS_PREFIX+pos;
	}
	
	/**
	 * 
	 * @param symbol
	 * @return true if @symbol is a valid instance property symbol
	 */
	public static boolean isInstancePropertySymbol(String symbol) {
		if(symbol==null)
			return false;
		Pattern pattern = Pattern.compile(Pattern.quote(INSTANCE_PROPERTY_PREFIX) + JAVA_NAME_REX);
		Matcher findingMatcher = pattern.matcher(symbol);
		return findingMatcher.matches();
		//return symbol.substring(0, 1).equals(INSTANCE_PROPERTY_PREFIX);
	}
	
	private static String getPropertyName(String symbol) {
		return symbol.substring(INSTANCE_PROPERTY_PREFIX.length());
	}
	
	
	/**
	 * 
	 * @param concatenatedParams
	 * @return A list of symbol params referenced in the string sent as a parameter
	 */
	public static List<String> getAllSymbols(String concatenatedParams) {
		/**
		 * A Set is used to avoid duplicates
		 * LinkedHashSet preserves the insertion order
		 */
		concatenatedParams = concatenatedParams.replaceAll(DELIMITED_JAVA_REX, ""); //ignore symbols in java Expressions
		Set<String> symbolsSet = new LinkedHashSet<String>();
		Pattern pattern = Pattern.compile("("+Pattern.quote(PARAMETERS_PREFIX)+"(\\d+|"+Pattern.quote(ALL_PARAMS_SUFFIX)+"))|"+Pattern.quote(INSTANCE_PROPERTY_PREFIX)+JAVA_NAME_REX);
		Matcher findingMatcher = pattern.matcher(concatenatedParams);
		while(findingMatcher.find()) {
			String match = findingMatcher.group();
			symbolsSet.add(match);
		}
		return new ArrayList<String>(symbolsSet);
	}


	/**
	 * Extract the expression value from a delimited expression
	 * @param delimitedExpression
	 * @return
	 */
	public static String getExpressionValue(String delimitedExpression) {
		Pattern pattern = Pattern.compile(DELIMITED_JAVA_REX);
		Matcher findingMatcher = pattern.matcher(delimitedExpression);
		findingMatcher.find();
		return findingMatcher.group(1);
	}
	
	/**
	 * 
	 * @param expression
	 * @return true if expression is a valid expression (currently the check is very basic, just verify that the expression once normalized is not null)
	 */
	public static boolean isValidJavaExpression(String expression) {
		return normalizeExpression(expression) != null;
	}
	
	/**
	 * Currently this method just trims the expression sent as parameter, and delete any sequence of ";" or blanck spaces at the end of the expression
	 * @param expression is the original Java expression to be normalized
	 * @return the normalized expression, null if once normalized the expression was empty
	 */
	public static String normalizeExpression(String expression) {
		if(expression == null)
			return null;
		expression = expression.trim();
		if(expression.isEmpty())
			return null;
		if(expression.substring(expression.length()-1).equals(";")) {
			Pattern pattern = Pattern.compile("(.*?)([\\s;]+)$");
			Matcher findingMatcher = pattern.matcher(expression);
			if(!findingMatcher.find())
				return null;
			expression = findingMatcher.group(1);
		}
		if(expression.isEmpty())
			return null;
		return expression;
	}
	
	
	
	/**
	 * 
	 * @param expression a delimited string with a java expression on it
	 * @return a list with all the java expressions found, in the same order that they were located. No duplicates are included
	 */
	public static List<String> getJavaExpressions(String expression) {
		/**
		 * A Set is used to avoid duplicates
		 * LinkedHashSet preserves the insertion order
		 */
		Set<String> javaExpressionsSet = new LinkedHashSet<String>(); 

		Pattern pattern = Pattern.compile(DELIMITED_JAVA_REX);
		Matcher findingMatcher = pattern.matcher(expression);
		while(findingMatcher.find()) {
			String match = findingMatcher.group();
			javaExpressionsSet.add(match);
		}
		return new ArrayList<String>(javaExpressionsSet);
	}

	/**
	 * This method answers the method name returning a Java expression declared in position @position at the original logic method
	 * The name of the method returning the expression is based on the original method name (obtained with: method.toGenericString())
	 * Note that this name depend on the class where the method is implemented
	 * At the time the code is generated, the method used to generate this name belongs to the abstract logic class
	 * At the time the method is invoked, the method used to generate this name belongs to the NON-abstract generated class (with suffix GENERATED_CLASS_SUFFIX)
	 * For that reason, this method attempts to return the same result at both times by means of:
	 * - dropping any abstract keyword from the method name
	 * - dropping the GENERATED_CLASS_SUFFIX from the method name
	 * @param method
	 * @param position
	 * @return
	 */
	private static String methodNameForExpression(Method method, int position) {
		String normalizedMethodName = method.toGenericString();
		normalizedMethodName = normalizedMethodName.replaceAll(Pattern.quote(LogicObjectInstrumentation.GENERATED_CLASS_SUFFIX), "");
		normalizedMethodName = normalizedMethodName.replaceAll("<.*?>", ""); //suppress generics information from the method name
		normalizedMethodName = normalizedMethodName.replaceAll(" abstract ", "_");
		normalizedMethodName = normalizedMethodName.replaceAll("\\(|\\)", "_");
		normalizedMethodName = normalizedMethodName.replaceAll(" |\\.|,", "_");
		return generatedMethodsPrefix + normalizedMethodName + "_exp" + position;
	}
	
	
	private String methodNameForExpression(int position) {
		return methodNameForExpression(getMethod(), position);
	}


	public static String getHiddenExpressionName(int index) {
		return HIDDEN_EXPRESSION_PREFIX + (index + 1);
	}

	private String hideExpressions(String logicString, List<String> expressions) {
		for(int i=0; i<expressions.size(); i++) {
			logicString = logicString.replaceAll(Pattern.quote(expressions.get(i)), Matcher.quoteReplacement(getHiddenExpressionName(i)));
		}
		return logicString;
	}
	
	private String restoreExpressions(String logicString, List<String> expressions) {
		for(int i=0; i<expressions.size(); i++) {
			logicString = logicString.replaceAll(Pattern.quote(getHiddenExpressionName(i)) , Matcher.quoteReplacement(expressions.get(i)));
		}
		return logicString;
	}
	
	/**
	 * 
	 * @param logicString a string with all the new params concatenated
	 * @param oldParams the java method params
	 * @return
	 */
	private String replaceSymbolsAndExpressions(String logicString, List<String> setSymbols, Map<String, String> expressionsMap, Object targetObject, Object[] oldParams) {
		Map<String, String> symbolsMap = symbolsReplacementMap(targetObject, oldParams, setSymbols);

		
		List<String> expressions = new ArrayList<String>(expressionsMap.keySet());
		logicString = hideExpressions(logicString, expressions);
		
		//replacing symbols
		for(String symbol : setSymbols) {			
			String termObjectString = symbolsMap.get(symbol);
			termObjectString = Matcher.quoteReplacement(termObjectString);//this is necessary if the String contains the symbol "$". Otherwise it will be interpreted as a group in the regular expression
			logicString=logicString.replaceAll(Pattern.quote(symbol), termObjectString);
		}
		
		
		logicString = restoreExpressions(logicString, expressions);
		
		//replacing java expressions for the result of a method invocation
		for(Entry<String, String> entry : expressionsMap.entrySet()) {
			try {
				String replacementValue;
				String methodName = entry.getValue();
				if(methodName == null || methodName.isEmpty())
					replacementValue = "";
				else {
					Method helperMethod = targetObject.getClass().getMethod(methodName,logicMethod.getWrappedMethod().getParameterTypes());
					Object expressionResult = helperMethod.invoke(targetObject, oldParams); //result contains the value of the java expression
					Term expressionAsTerm = ObjectToTermAdapter.asTerm(expressionResult);
					replacementValue = expressionAsTerm.toString();
				}
				String delimitedJavaExpression = Pattern.quote(entry.getKey());
				replacementValue = Matcher.quoteReplacement(replacementValue); //this is necessary if the String contains the symbol "$". Otherwise it will be interpreted as a group in the regular expression
				logicString=logicString.replaceAll(delimitedJavaExpression, replacementValue);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return logicString;
	}


	/**
	 * Builds a dictionary of symbol params to values
	 * @param parameters the java method parameters
	 * @return
	 */
	private Map<String, String> symbolsReplacementMap(Object targetObject, Object[] parameters, List<String> symbols) {
		Map<String, String> dictionary = new HashMap<String, String>();
		if(parameters.length > 0) {
			LogicEngine engine = LogicEngine.getDefault();
			List<Term> listTerms = new ArrayList<Term>();
			boolean allParamsRequired = symbols.contains(AbstractLogicMethodParser.ALL_PARAMS_SYMBOL);
			for(int i = 0; i<parameters.length; i++) {
				String paramName = AbstractLogicMethodParser.parameterSymbol(i+1);
				if(allParamsRequired || symbols.contains(paramName)) {
					Term termParam = ObjectToTermAdapter.asTerm(parameters[i]);
					if(engine.nonAnonymousVariablesNames(termParam).size() > 0)
						throw new RuntimeException("Parameter objects cannot contain free non-anonymous variables: "+termParam);//in order to avoid name collisions
					dictionary.put(paramName, termParam.toString());
					listTerms.add(termParam);
				}
			}
			if(allParamsRequired)
				dictionary.put(AbstractLogicMethodParser.ALL_PARAMS_SYMBOL, engine.termListToTextSequence(listTerms));
		}
		if(symbols.contains(AbstractLogicMethodParser.THIS_SYMBOL)) {
			Term thisAsTerm = new ObjectToTermAdapter().adapt(targetObject);
			dictionary.put(AbstractLogicMethodParser.THIS_SYMBOL, thisAsTerm.toString());
		}
		
		for(String setSymbol : symbols) {
			if(AbstractLogicMethodParser.isInstancePropertySymbol(setSymbol)) {
				String instanceVarName = AbstractLogicMethodParser.getPropertyName(setSymbol);
				Term instanceVarAsTerm = instanceVarAsTerm = LogicObjectAdapter.fieldAsTerm(targetObject, instanceVarName); //TODO this method should be in ObjectToTermAdapter
				dictionary.put(setSymbol, instanceVarAsTerm.toString());
			}
		}
		return dictionary;
	}
	
	private Map<String, String> expressionsReplacementMap(List<String> delimitedExpressions) {		
		Map<String, String> expressionsReplacementMap = new HashMap<String, String>();
		for(int i = 0; i<delimitedExpressions.size(); i++) {
			String delimitedExpression = delimitedExpressions.get(i);
			String expression = AbstractLogicMethodParser.getExpressionValue(delimitedExpression);
			String substitutionValue;
			if(AbstractLogicMethodParser.isValidJavaExpression(expression)) {
				substitutionValue = methodNameForExpression(i + 1);//i+1 to work with a 1-based index
			} else {
				logger.warn("The expression: " + delimitedExpression + "in the method "+ getMethod().toGenericString()+" is not valid. It will be ignored.");
				substitutionValue = "";
			}
			expressionsReplacementMap.put(delimitedExpression, substitutionValue);
		}
		return expressionsReplacementMap;
	}

	
	public Map<String, String> generatedMethodsMap() {
		Map<String, String> generatedMethodsMap = new HashMap<String, String>();
		for(Entry<String, String> entry : expressionsReplacementMap.entrySet()) {
			String expression = entry.getKey();
			expression = AbstractLogicMethodParser.getExpressionValue(expression);
			if(AbstractLogicMethodParser.isValidJavaExpression(expression)) {
				expression = AbstractLogicMethodParser.normalizeExpression(expression);
				String methodName = entry.getValue();
				generatedMethodsMap.put(methodName, expression);
			}
		}
		return generatedMethodsMap;
	}
	

	/*
	public Map<String, String> generatedMethodsMap() {
		Map<String, String> parametersExpressionsMethodsMap, returnExpressionMethodsMap, allExpressionMethodsMap;
		parametersExpressionsMethodsMap = findExpressionHelperMethods(expressionReplacementMap_parameters);
		
		String returnString = logicMethod.getEachSolutionValue();
		if(returnString != null) {
			returnExpressionMethodsMap = findExpressionHelperMethods(expressionReplacementMap_returnValue);
		} else {
			returnExpressionMethodsMap = new HashMap<String, String>();
		}
		allExpressionMethodsMap = parametersExpressionsMethodsMap;
		allExpressionMethodsMap.putAll(returnExpressionMethodsMap);
		return allExpressionMethodsMap;
	}
*/
	
}


