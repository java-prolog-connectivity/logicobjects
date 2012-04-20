package org.logicobjects.adapter.methodparameters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.logicobjects.instrumentation.LogicObjectInstrumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auxiliary class providing operations for parsing logic method parameters
 * @author sergioc78
 *
 */
public class ParametersParser {
	private static Logger logger = LoggerFactory.getLogger(ParametersParser.class);
	
	private static final String PARAM_SEPARATOR = "~~~";
	
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
	
	private static final String PARAMETER_JAVA_REX = Pattern.quote(BEGIN_JAVA_EXPRESSION) + "(.*?)" + Pattern.quote(END_JAVA_EXPRESSION);
	
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
	
	public static String getPropertyName(String symbol) {
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
		Set<String> symbolsSet = new LinkedHashSet<String>();
		//Pattern pattern = Pattern.compile("("+PARAMETERS_PREFIX_REX+"(\\d+|"+ALL_PARAMS_SUFIX_REX+"))|"+INSTANCE_VAR_PREFIX_REX+JAVA_NAME);
		Pattern pattern = Pattern.compile("("+Pattern.quote(PARAMETERS_PREFIX)+"(\\d+|"+Pattern.quote(ALL_PARAMS_SUFFIX)+"))|"+Pattern.quote(INSTANCE_PROPERTY_PREFIX)+JAVA_NAME_REX);
		Matcher findingMatcher = pattern.matcher(concatenatedParams);
		while(findingMatcher.find()) {
			String match = findingMatcher.group();
			symbolsSet.add(match);
		}
		return new ArrayList<String>(symbolsSet);
	}
	

	public static String getExpressionValue(String delimitedExpression) {
		Pattern pattern = Pattern.compile(PARAMETER_JAVA_REX);
		Matcher findingMatcher = pattern.matcher(delimitedExpression);
		findingMatcher.find();
		return findingMatcher.group(1);
	}
	
	/**
	 * 
	 * @param expression
	 * @return true if expression is a valid expression
	 */
	public static boolean isValidExpression(String expression) {
		return normalizeExpression(expression) != null;
	}
	
	
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
	 * @param paramString a string with java expression on it
	 * @return a list with all the java expressions found, in the same order that they were located. No duplicates are included
	 */
	public static List<String> getJavaExpressions(String paramString) {
		/**
		 * A Set is used to avoid duplicates
		 * LinkedHashSet preserves the insertion order
		 */
		Set<String> javaExpressionsSet = new LinkedHashSet<String>(); 
		/*
		 * the question mark is to specify a reluctant quantifier, so 'any' characters (the '.') will occur the minimum possible amount of times
		 */
		Pattern pattern = Pattern.compile(PARAMETER_JAVA_REX);
		Matcher findingMatcher = pattern.matcher(paramString);
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
	public static String methodNameForExpression(Method method, int position) {
		String normalizedMethodName = method.toGenericString();
		normalizedMethodName = normalizedMethodName.replaceAll(Pattern.quote(LogicObjectInstrumentation.GENERATED_CLASS_SUFFIX), "");
		normalizedMethodName = normalizedMethodName.replaceAll("<.*>", ""); //suppress generics information from the method name
		normalizedMethodName = normalizedMethodName.replaceAll(" abstract ", "_");
		normalizedMethodName = normalizedMethodName.replaceAll("\\(|\\)", "");
		normalizedMethodName = normalizedMethodName.replaceAll(" |\\.", "_");
		return generatedMethodsPrefix + normalizedMethodName + "_exp" + position;
	}
	
	
	
	public static String concatenateParams(Object[] params) {
		String concatenatedParams = "";
		for(int i=0; i<params.length; i++) {
			concatenatedParams+=params[i];
			if(i<params.length-1)
				concatenatedParams+=PARAM_SEPARATOR;
		}
		return concatenatedParams;
	}
	
	public static Object[] splitConcatenatedParams(String paramsString) {
		return paramsString.split(PARAM_SEPARATOR);
	}
	

}
