package org.logicobjects.instrumentation;

/**
 * Symbols used to express method arguments:
 * 
 * (Term symbols)
 * $NUMBER : a java method argument as a term (e.g., '$1' means the first argument)
 * $$ : all the java method arguments as terms. The arguments are separated by a ','
 * @this : the object declaring the method as term
 * @propertyName : a bean property of the object as term
 * 
 * (Java object symbols)
 * ! : a suffix to the previous symbols. If added, the object will not be transformed to a term, but will be passed to Logtalk as a java  object
 * & : same as '!', but with delayed evaluation
 * @author scastro
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
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.core.AbstractLogicMethod;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auxiliary class providing operations for parsing logic method arguments
 * @author scastro
 *
 */
public abstract class AbstractLogicMethodParser<LM extends AbstractLogicMethod> {
	private static Logger logger = LoggerFactory.getLogger(AbstractLogicMethodParser.class);

	public static final String GENERATED_METHOD_PREFIX = "$logicobjects_";
	
	public static final String IMMEDIATE_EXP_EVALUATION_SYMBOL = "$";
	public static final String DEFERRED_EXP_EVALUATION_SYMBOL = "#";
	
	public static final String BEGIN_JAVA_EXPRESSION_BLOCK = "{";
	public static final String END_JAVA_EXPRESSION_BLOCK = "}";
	
	public static final String BEGIN_IMMEDIATE_JAVA_EXPRESSION = IMMEDIATE_EXP_EVALUATION_SYMBOL + BEGIN_JAVA_EXPRESSION_BLOCK;
	public static final String BEGIN_DEFERRED_JAVA_EXPRESSION = DEFERRED_EXP_EVALUATION_SYMBOL + BEGIN_JAVA_EXPRESSION_BLOCK;
	
	
	//public static final String BEGIN_JAVA_EXPRESSION = "/{";
	//public static final String END_JAVA_EXPRESSION = "/}";
	
	
	
	public static final String INSTANCE_PROPERTY_PREFIX = "@";
	
	public static final String ARGUMENT_PREFIX = "$";
	
	public static final String THIS_SUFFIX = "0";
	
	public static final String THIS_SYMBOL = ARGUMENT_PREFIX + THIS_SUFFIX;
	
	public static final String ALL_ARGUMENTS_SUFFIX = "$";
	
	public static final String ALL_ARGUMENTS_SYMBOL = ARGUMENT_PREFIX + ALL_ARGUMENTS_SUFFIX;
	
	public static final String JAVA_NAME_REX = "([a-zA-Z_\\$][\\w\\$]*)";
	
	public static final String HIDDEN_EXPRESSION_PREFIX = "~HIDDEN_EXPRESSION~_";
	
	/*
	 * the question mark is to specify a reluctant quantifier, so 'any' characters (the '.') will occur the minimum possible amount of times
	 */
	//public static final String DELIMITED_JAVA_REX = Pattern.quote(BEGIN_JAVA_EXPRESSION) + "(.*?)" + Pattern.quote(END_JAVA_EXPRESSION);
	
	public static final String ARGUMENTS_SEPARATOR = "~~~";
	
	public static final String ARGUMENTS_TAG = "~ARGUMENTS~";
	
	public static final String QUERY_TAG = "~QUERY~";
	
	public static final String RETURN_TAG = "~RET~";

	
	//private Method method;  
	
	//THE FOLLOWING VALUES ARE CALCULATED WHEN PARSING
	private LM logicMethod;
	//private ParsingData unparsedData;
	private String allLogicStrings;
	private List<String> expressions;
	private Map<String, String> expressionsReplacementMap;
	private List<String> foundSymbols;
	
	
	public static AbstractLogicMethodParser create(Method method) {
		return new AbstractLogicMethodParser(method) {};
	}
	
	public static AbstractLogicMethodParser create(AbstractLogicMethod logicMethod) {
		return new AbstractLogicMethodParser(logicMethod) {};
	}
	
	AbstractLogicMethodParser(Method method) {
		this((LM) AbstractLogicMethod.create(method));
	}
	
	AbstractLogicMethodParser(LM logicMethod) {
		this.logicMethod = logicMethod;
	}

	public AbstractLogicMethodParser parse() {
		LogicMethodParsingData unparsedData = logicMethod.getDataToParse(); //the data to parse depends on the kind of logic method that was instantiated in a previous step
		String queryToParse = asNotNullString(unparsedData.getQueryString());
		String unparsedLogicArgumentsString = concatenateMethodArguments(asNotNullStringArray(unparsedData.getMethodArguments()));
		String solutionToParse = asNotNullString(unparsedData.getSolutionString());
		allLogicStrings = QUERY_TAG + queryToParse + ARGUMENTS_TAG + unparsedLogicArgumentsString + RETURN_TAG + solutionToParse;
		expressions = getJavaExpressions(allLogicStrings); //gather all java expressions
		expressionsReplacementMap = expressionsReplacementMap(expressions);
		foundSymbols = getAllSymbols(allLogicStrings);
		return this;
	}


	private String asNotNullString(String s) {
		return s!=null?s:"";
	}
	
	private String[] asNotNullStringArray(String[] a) {
		return a!=null?a:new String[] {};
	}
	/*
	public String getQueryToParse() {
		return asNotNullString(unparsedData.getQueryString());
	}
	
	public String getUnparsedLogicParamsString() {
		return concatenateParameters(getParametersToParse());
	}
	
	public String[] getParametersToParse() {
		return asNotNullStringArray(unparsedData.getParameters());
	}
	
	public String getSolutionToParse() {
		return asNotNullString(unparsedData.getSolutionString());
	}
	*/
	public LM getLogicMethod() {
		return logicMethod;
	}

	public Method getMethod() {
		return logicMethod.getWrappedMethod();
	}


	public ParsedLogicMethod parsedLogicMethod(Object targetObject, Object[] arguments) {
		LogicMethodParsingData parsedData = parsedData(targetObject, arguments);
		return new ParsedLogicMethod(getLogicMethod(), targetObject, arguments, parsedData);
	}
	
	/**
	 * Given a context data (a concrete instance receiving this method invocation and the method arguments) find out general parsing data of a logic method
	 * @param targetObject
	 * @param oldArguments
	 * @return
	 */
	private LogicMethodParsingData parsedData(Object targetObject, Object[] oldArguments) {
		String allLogicStringsProcessed = replaceSymbolsAndExpressions(allLogicStrings, foundSymbols, expressionsReplacementMap, targetObject, oldArguments);
		LogicMethodParsingData parsedData = decomposeLogicString(allLogicStringsProcessed);
		return parsedData;
	}

	/**
	 * This method is public just for testing purposes
	 * @param allLogicStringsProcessed
	 * @return
	 */
	public static LogicMethodParsingData decomposeLogicString(String allLogicStringsProcessed) {
		LogicMethodParsingData parsedData = new LogicMethodParsingData();
		Pattern pattern = Pattern.compile(Pattern.quote(QUERY_TAG)+"(.*)"+Pattern.quote(ARGUMENTS_TAG)+"(.*)"+Pattern.quote(RETURN_TAG)+"(.*)");
		Matcher matcher = pattern.matcher(allLogicStringsProcessed);
		matcher.find();
		String queryString = matcher.group(1);
		if(!queryString.isEmpty())
			parsedData.setQueryString(queryString);
		String arguments = matcher.group(2);
		if(!arguments.isEmpty())
			parsedData.setMethodArguments(splitConcatenatedTokens(arguments));
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
	private static String concatenateMethodArguments(Object[] args) {
		StringBuilder concatenatedArgs = new StringBuilder();
		for(int i=0; i<args.length; i++) {
			concatenatedArgs.append(args[i]);
			if(i<args.length-1)
				concatenatedArgs.append(ARGUMENTS_SEPARATOR);
		}
		return concatenatedArgs.toString();
	}
	
	private static String[] splitConcatenatedTokens(String tokensString) {
		return tokensString.split(ARGUMENTS_SEPARATOR);
	}
	
	
	
	/**
	 * 
	 * @param pos a 1-based index of the java parameter
	 * @return the parameter symbol representation
	 */
	public static String methodArgumentSymbol(int pos) {
		return ARGUMENT_PREFIX+pos;
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
	
	
	public String suppressJavaExpressions(String s) {
		/**
		 * A Set is used to avoid duplicates
		 * LinkedHashSet preserves the insertion order
		 */
		for(String expression : expressions) {
			s.replaceAll(Pattern.quote(expression), ""); //ignore symbols in java Expressions
		}
		//s = concatenatedParams.replaceAll(DELIMITED_JAVA_REX, ""); //ignore symbols in java Expressions
		return s;
	}
	
	/**
	 * 
	 * @param concatenatedArguments
	 * @return A list of symbol params referenced in the string sent as a parameter
	 */
	public List<String> getAllSymbols(String concatenatedArguments) {
		concatenatedArguments = suppressJavaExpressions(concatenatedArguments);
		return scanSymbols(concatenatedArguments);
	}

	/**
	 * Created a separated method just to facilitate a bit testing
	 * @param s
	 * @return
	 */
	public static List<String> scanSymbols(String s) {
		Set<String> symbolsSet = new LinkedHashSet<String>();
		Pattern pattern = Pattern.compile("("+Pattern.quote(ARGUMENT_PREFIX)+"(\\d+|"+Pattern.quote(ALL_ARGUMENTS_SUFFIX)+"))|"+Pattern.quote(INSTANCE_PROPERTY_PREFIX)+JAVA_NAME_REX);
		Matcher findingMatcher = pattern.matcher(s);
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
		Pattern pattern = Pattern.compile("^(" + Pattern.quote(BEGIN_IMMEDIATE_JAVA_EXPRESSION) + "|" + Pattern.quote(BEGIN_DEFERRED_JAVA_EXPRESSION) + ")(.*)" + END_JAVA_EXPRESSION_BLOCK + "$");
		
		//Pattern pattern = Pattern.compile(DELIMITED_JAVA_REX);
		Matcher findingMatcher = pattern.matcher(delimitedExpression);
		findingMatcher.find();
		return findingMatcher.group(2);
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

		
		/*
		Pattern pattern = Pattern.compile(DELIMITED_JAVA_REX);
		Matcher findingMatcher = pattern.matcher(expression);
		while(findingMatcher.find()) {
			String match = findingMatcher.group();
			javaExpressionsSet.add(match);
		}
		*/
		
		List<MatchResult> matchResults = getJavaExpressionMatches(expression);
		for(MatchResult matchResult : matchResults) {
			javaExpressionsSet.add(matchResult.group());
		}
		
		
		return new ArrayList<String>(javaExpressionsSet);
	}

	private static List<MatchResult> getJavaExpressionMatches(String expression) {
		List<MatchResult> expressionMatches = new ArrayList<MatchResult>();
		Pattern pattern = Pattern.compile( Pattern.quote(BEGIN_IMMEDIATE_JAVA_EXPRESSION) + "|" + Pattern.quote(BEGIN_DEFERRED_JAVA_EXPRESSION) );
		Matcher matcher = pattern.matcher(expression);
		while(matcher.find()) {
			int start = matcher.start();
			int end = findClosingBrace(expression, start + 2); //the starting delimiter (two characters long) is not included in the expression to search
			if(end == -1)
				throw new RuntimeException("Impossible to find balanced closing brace in expression");
			expressionMatches.add(new SimpleMatchResult(expression, start, end));
		}
		return expressionMatches;
	}
	
	/**
	 * 
	 * @param expression is a String containing the Java expression to search for a closing brace
	 * @param start is the start of the expression (immediately after the starting delimiter)
	 * @return the index of the (balanced) closing brace
	 */
	private static int findClosingBrace(String expression, int start) {
		if(start >= expression.length())
			return -1;
		int openingBraceCounters = 0;
		for(int i = start; i < expression.length(); i++) {
			String s = expression.substring(i, i + 1);
			if(s.equalsIgnoreCase(END_JAVA_EXPRESSION_BLOCK)) {
				if(openingBraceCounters == 0)
					return i;
				else
					openingBraceCounters--;
			} else {
				if(s.equalsIgnoreCase(BEGIN_JAVA_EXPRESSION_BLOCK))
					openingBraceCounters++;
			}		
		}
		return -1;
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
		return GENERATED_METHOD_PREFIX + normalizedMethodName + "_exp" + position;
	}
	
	/**
	 * 
	 * @param position the method name is a function of an expression position
	 * @return a method name which return value will replace an expression (returns only the method simple name, parentheses are not included)
	 */
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
	 * @param oldArguments the java method params
	 * @return
	 */
	private String replaceSymbolsAndExpressions(String logicString, List<String> setSymbols, Map<String, String> expressionsMap, Object targetObject, Object[] oldArguments) {
		Map<String, String> symbolsMap = symbolsReplacementMap(targetObject, oldArguments, setSymbols);

		
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
					Object expressionResult = helperMethod.invoke(targetObject, oldArguments); //result contains the value of the java expression
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
	 * @param args the java method parameters
	 * @return
	 */
	private Map<String, String> symbolsReplacementMap(Object targetObject, Object[] args, List<String> symbols) {
		Map<String, String> dictionary = new HashMap<String, String>();
		if(args.length > 0) {
			LogicEngine engine = LogicEngine.getDefault();
			List<Term> listTerms = new ArrayList<Term>();
			boolean allArgumentsRequired = symbols.contains(AbstractLogicMethodParser.ALL_ARGUMENTS_SYMBOL);
			for(int i = 0; i<args.length; i++) {
				String paramName = AbstractLogicMethodParser.methodArgumentSymbol(i+1);
				if(allArgumentsRequired || symbols.contains(paramName)) {
					Term termArgument = ObjectToTermAdapter.asTerm(args[i]);
					if(engine.nonAnonymousVariablesNames(termArgument).size() > 0)
						throw new RuntimeException("Argument objects cannot contain free non-anonymous variables: "+termArgument);//in order to avoid name collisions
					dictionary.put(paramName, termArgument.toString());
					listTerms.add(termArgument);
				}
			}
			if(allArgumentsRequired)
				dictionary.put(AbstractLogicMethodParser.ALL_ARGUMENTS_SYMBOL, engine.termListToTextSequence(listTerms));
		}
		if(symbols.contains(AbstractLogicMethodParser.THIS_SYMBOL)) {
			Term thisAsTerm = new ObjectToTermAdapter().adapt(targetObject);
			dictionary.put(AbstractLogicMethodParser.THIS_SYMBOL, thisAsTerm.toString());
		}
		
		for(String setSymbol : symbols) {
			if(AbstractLogicMethodParser.isInstancePropertySymbol(setSymbol)) {
				String instanceVarName = AbstractLogicMethodParser.getPropertyName(setSymbol);
				Term instanceVarAsTerm = instanceVarAsTerm = LogicObject.propertyAsTerm(targetObject, instanceVarName);//TODO problem !!!
				dictionary.put(setSymbol, instanceVarAsTerm.toString());
			}
		}
		return dictionary;
	}
	
	/**
	 * 
	 * @param delimitedExpressions all the expressions found. These expressions include delimiter characters
	 * @return a map mapping "delimited" expressions (including delimiters characters) to method names
	 */
	private Map<String, String> expressionsReplacementMap(List<String> delimitedExpressions) {		
		Map<String, String> expressionsReplacementMap = new HashMap<String, String>();
		for(int i = 0; i<delimitedExpressions.size(); i++) {
			String delimitedExpression = delimitedExpressions.get(i);
			String expression = AbstractLogicMethodParser.getExpressionValue(delimitedExpression);  //suppress the delimiter characters
			String substitutionValue;
			if(AbstractLogicMethodParser.isValidJavaExpression(expression)) {
				substitutionValue = methodNameForExpression(i + 1); //calculate a method name for this expression given the expression position. Using as index i+1 to work with 1-based index. Later the expression will be replaced by a call to this method
			} else {
				logger.warn("The expression: " + delimitedExpression + "in the method "+ getMethod().toGenericString()+" is not valid. It will be ignored.");
				substitutionValue = "";  //the expression will be replaced by an empty string
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


