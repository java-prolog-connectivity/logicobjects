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

import org.jpc.engine.prolog.AbstractPrologEngine;
import org.jpc.engine.prolog.driver.AbstractPrologEngineDriver;
import org.jpc.term.AbstractTerm;
import org.jpc.util.PrologUtil;
import org.logicobjects.LogicObjects;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.core.LogicRoutine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auxiliary class providing operations for parsing logic method arguments
 * @author scastro
 *
 */
public abstract class LogicMethodParser<LM extends LogicRoutine> extends AbstractParser {
	private static Logger logger = LoggerFactory.getLogger(LogicMethodParser.class);

	public static final String GENERATED_METHOD_PREFIX = "$logicobjects_";
	
	//WHY THESE NAMES?: http://scala-programming-language.1934581.n4.nabble.com/Why-quot-by-name-quot-parameters-are-called-this-way-td1944598.html
	public static final String VALUE_EXP_EVALUATION_SYMBOL = "$";
	public static final String DEFERRED_EXP_EVALUATION_SYMBOL = "#";
	
	public static final String BEGIN_JAVA_EXPRESSION_BLOCK = "{";
	public static final String END_JAVA_EXPRESSION_BLOCK = "}";
	
	public static final String BEGIN_JAVA_VALUE_EXP = VALUE_EXP_EVALUATION_SYMBOL + BEGIN_JAVA_EXPRESSION_BLOCK;
	public static final String BEGIN_JAVA_DEFERRED_EXP = DEFERRED_EXP_EVALUATION_SYMBOL + BEGIN_JAVA_EXPRESSION_BLOCK;
	
	
	public static final String ARGUMENT_PREFIX = "$";
	
	public static final String THIS_METHOD_ARGUMENT_SUFFIX = "0";
	
	public static final String THIS_METHOD_ARGUMENT_SYMBOL = ARGUMENT_PREFIX + THIS_METHOD_ARGUMENT_SUFFIX;
	
	public static final String ALL_ARGUMENTS_SUFFIX = "$";
	
	public static final String ALL_ARGUMENTS_SYMBOL = ARGUMENT_PREFIX + ALL_ARGUMENTS_SUFFIX;
	
	
	/**
	 * substitution markers for expressions.
	 * The idea is that java expressions could be temporarily removed while replacing term symbols, and after the replacement they have to be added again
	 */
	public static final String HIDDEN_EXPRESSION_PREFIX = "~HIDDEN_EXPRESSION~_";
	
	public static final String QUERY_TAG = "~QUERY~";
	
	public static final String METHOD_ARGUMENTS_TAG = "~METHOD_ARGUMENTS~";
	
	public static final String RETURN_TAG = "~RET~";

	
	//private Method method;  
	private LM logicMethod;
	
	//THE FOLLOWING VALUES ARE CALCULATED WHEN PARSING
	private List<String> expressions;
	private Map<String, String> expressionsReplacementMap;

	//protected LogicEngineConfiguration logicEngineConfig;
	protected PrologUtil logicUtil;
	
	//TODO the idea of the factory is to be able to look in a cache if a method has already been parsed, to do some day when having time ...
	public static LogicMethodParser create(Method method) {
		return new LogicMethodParser(method) {};
	}
	
	public static LogicMethodParser create(LogicRoutine logicMethod) {
		return new LogicMethodParser(logicMethod) {};
	}
	
	LogicMethodParser(Method method) {
		this((LM) LogicRoutine.create(method));
	}
	
	LogicMethodParser(LM logicMethod) {
		this.logicMethod = logicMethod;
		this.logicUtil = LogicObjects.getLogicUtilFor(logicMethod.getWrappedMethod().getDeclaringClass());
	}

	@Override
	public LogicMethodParser parse() {
		LogicMethodParsingData unparsedData = logicMethod.getDataToParse(); //the data to parse depends on the kind of logic method that was instantiated in a previous step
		String unparsedQuery = asNotNullString(unparsedData.getQueryString());
		String unparsedLogicArgumentsString = concatenateTokens(asNotNullStringList(unparsedData.getMethodArguments()));
		String unparsedSolution = asNotNullString(unparsedData.getSolutionString());
		allLogicStrings = QUERY_TAG + unparsedQuery + METHOD_ARGUMENTS_TAG + unparsedLogicArgumentsString + RETURN_TAG + unparsedSolution;
		expressions = getJavaExpressions(allLogicStrings); //gather all java expressions
		expressionsReplacementMap = expressionsReplacementMap(expressions);
		foundSymbols = getAllMethodSymbols(allLogicStrings);
		return this;
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


	public ParsedLogicMethod parsedLogicMethod(Object targetObject, List arguments) {
		LogicMethodParsingData parsedData = parsedData(targetObject, arguments);
		return new ParsedLogicMethod(getLogicMethod(), targetObject, arguments, parsedData);
	}
	
	/**
	 * Given a context data (a concrete instance receiving this method invocation and the method arguments) find out general parsing data of a logic method
	 * @param targetObject
	 * @param oldArguments
	 * @return
	 */
	private LogicMethodParsingData parsedData(Object targetObject, List oldArguments) {
		String allLogicStringsProcessed = replaceSymbolsAndExpressions(allLogicStrings, foundSymbols, expressionsReplacementMap, targetObject, oldArguments);
		LogicMethodParsingData parsedData = decomposeLogicString(allLogicStringsProcessed);
		return parsedData;
	}

	/**
	 * This method is public for testing purposes
	 * @param allLogicStringsProcessed
	 * @return
	 */
	public static LogicMethodParsingData decomposeLogicString(String allLogicStringsProcessed) {
		LogicMethodParsingData parsedData = new LogicMethodParsingData();
		Pattern pattern = Pattern.compile(Pattern.quote(QUERY_TAG)+"(.*)"+Pattern.quote(METHOD_ARGUMENTS_TAG)+"(.*)"+Pattern.quote(RETURN_TAG)+"(.*)");
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

	
	
	
	/**
	 * 
	 * @param pos a 1-based index of the java parameter
	 * @return the parameter symbol representation
	 */
	public static String methodArgumentSymbol(int pos) {
		return ARGUMENT_PREFIX+pos;
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
	public List<String> getAllMethodSymbols(String concatenatedArguments) {
		concatenatedArguments = suppressJavaExpressions(concatenatedArguments);
		return scanMethodSymbols(concatenatedArguments);
	}

	public static List<String> scanMethodSymbols(String s) {
		return scanSymbols(s, "("+Pattern.quote(ARGUMENT_PREFIX)+"(\\d+|"+Pattern.quote(ALL_ARGUMENTS_SUFFIX)+"))|"+Pattern.quote(INSTANCE_PROPERTY_PREFIX)+JAVA_NAME_REX);
	}
	
	


	/**
	 * Extract the expression value from a delimited expression
	 * @param delimitedExpression
	 * @return
	 */
	public static String getExpressionValue(String delimitedExpression) {
		Pattern pattern = Pattern.compile("^(" + Pattern.quote(BEGIN_JAVA_VALUE_EXP) + "|" + Pattern.quote(BEGIN_JAVA_DEFERRED_EXP) + ")(.*)" + END_JAVA_EXPRESSION_BLOCK + "$");
		
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
		Pattern pattern = Pattern.compile( Pattern.quote(BEGIN_JAVA_VALUE_EXP) + "|" + Pattern.quote(BEGIN_JAVA_DEFERRED_EXP) );
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
	private String replaceSymbolsAndExpressions(String logicString, List<String> setSymbols, Map<String, String> expressionsMap, Object targetObject, List oldArguments) {
		List<String> expressions = new ArrayList<String>(expressionsMap.keySet());
		logicString = hideExpressions(logicString, expressions);
		
		Map<String, String> symbolsMap = symbolsReplacementMap(targetObject, oldArguments, setSymbols); //obtaining a map with the symbols to replace
		logicString = replaceSymbols(logicString, symbolsMap); //replacing symbols
		
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
					Object expressionResult = helperMethod.invoke(targetObject, (Object[])oldArguments.toArray()); //result contains the value of the java expression
					AbstractTerm expressionAsTerm = ObjectToTermAdapter.asTerm(expressionResult);
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
	private Map<String, String> symbolsReplacementMap(Object targetObject, List args, List<String> symbols) {
		Map<String, String> dictionary = propertiesSymbolsReplacementMap(targetObject, symbols);
		if(!args.isEmpty()) {
			List<AbstractTerm> listTerms = new ArrayList<AbstractTerm>();
			boolean allArgumentsRequired = symbols.contains(LogicMethodParser.ALL_ARGUMENTS_SYMBOL);
			for(int i = 0; i<args.size(); i++) {
				String paramName = LogicMethodParser.methodArgumentSymbol(i+1);
				if(allArgumentsRequired || symbols.contains(paramName)) {
					AbstractTerm termArgument = ObjectToTermAdapter.asTerm(args.get(i));
					if(!termArgument.getNamedVariablesNames().isEmpty())
						throw new RuntimeException("Argument objects cannot contain free non-anonymous variables: "+termArgument);//in order to avoid name collisions
					dictionary.put(paramName, termArgument.toString());
					listTerms.add(termArgument);
				}
			}
			if(allArgumentsRequired)
				dictionary.put(LogicMethodParser.ALL_ARGUMENTS_SYMBOL, logicUtil.termSequenceToString(listTerms));
		}
		if(symbols.contains(LogicMethodParser.THIS_METHOD_ARGUMENT_SYMBOL)) { //the symbol $0 was found
			String thisAsTermString = dictionary.get(THIS_INSTANCE_PROPERTY_SYMBOL); //find if the synonym @this has already been translated
			if(thisAsTermString == null) //if the synonym does not exist, calculate the translation as term of this, otherwise reuse the existing translation
				thisAsTermString = new ObjectToTermAdapter().adapt(targetObject).toString();
			dictionary.put(LogicMethodParser.THIS_METHOD_ARGUMENT_SYMBOL, thisAsTermString);
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
			String expression = LogicMethodParser.getExpressionValue(delimitedExpression);  //suppress the delimiter characters
			String substitutionValue;
			if(LogicMethodParser.isValidJavaExpression(expression)) {
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
			expression = LogicMethodParser.getExpressionValue(expression);
			if(LogicMethodParser.isValidJavaExpression(expression)) {
				expression = LogicMethodParser.normalizeExpression(expression);
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


