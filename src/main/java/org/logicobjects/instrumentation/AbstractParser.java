package org.logicobjects.instrumentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jpl.Term;

import org.logicobjects.core.LogicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractParser {
	private static Logger logger = LoggerFactory.getLogger(AbstractParser.class);
	
	public static final String INSTANCE_PROPERTY_PREFIX = "@";
	public static final String THIS_INSTANCE_PROPERTY_SYMBOL = INSTANCE_PROPERTY_PREFIX + "this";
	public static final String JAVA_NAME_REX = "([a-zA-Z_\\$][\\w\\$]*)";
	
	
	public static final String TOKEN_COLLECTION_SEPARATOR = "~~~";
	
	protected String allLogicStrings;
	protected List<String> foundSymbols;
	
	
	protected String asNotNullString(String s) {
		return s!=null?s:"";
	}
	
	protected String[] asNotNullStringArray(String[] a) {
		return a!=null?a:new String[] {};
	}
	
	protected static String concatenateTokens(Object[] args) {
		StringBuilder concatenatedArgs = new StringBuilder();
		for(int i=0; i<args.length; i++) {
			concatenatedArgs.append(args[i]);
			if(i<args.length-1)
				concatenatedArgs.append(TOKEN_COLLECTION_SEPARATOR);
		}
		return concatenatedArgs.toString();
	}
	
	protected static String[] splitConcatenatedTokens(String tokensString) {
		return tokensString.split(TOKEN_COLLECTION_SEPARATOR);
	}

	public static String asInstancePropertySymbol(String property) {
		return INSTANCE_PROPERTY_PREFIX + property;
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
	
	protected static String getPropertyName(String symbol) {
		return symbol.substring(INSTANCE_PROPERTY_PREFIX.length());
	}
	
	public String replaceSymbols(String logicString, Map<String, String> symbolsMap) {
		for(String symbol : symbolsMap.keySet()) {			
			String termObjectString = symbolsMap.get(symbol);
			termObjectString = Matcher.quoteReplacement(termObjectString);//this is necessary if the String contains the symbol "$". Otherwise it will be interpreted as a group in the regular expression
			logicString=logicString.replaceAll(Pattern.quote(symbol), termObjectString);
		}
		return logicString;
	}
	
	/**
	 * @param s
	 * @return
	 */
	public static List<String> scanSymbols(String s, String symbolsPattern) {
		Set<String> symbolsSet = new LinkedHashSet<String>();
		Pattern pattern = Pattern.compile(symbolsPattern);
		Matcher findingMatcher = pattern.matcher(s);
		while(findingMatcher.find()) {
			String match = findingMatcher.group();
			symbolsSet.add(match);
		}
		return new ArrayList<String>(symbolsSet);
	}
	
	public List<String> getFoundSymbols() {
		return foundSymbols;
	}
	
	protected Map<String, String> propertiesSymbolsReplacementMap(Object targetObject, List<String> symbols) {
		Map<String, String> dictionary = new HashMap<String, String>();
		for(String symbol : symbols) {
			if(LogicMethodParser.isInstancePropertySymbol(symbol)) {
				String property = LogicMethodParser.getPropertyName(symbol);
				Term propertyAsTerm = null;
				propertyAsTerm = propertyAsTerm = LogicObject.propertyAsTerm(targetObject, property);//TODO the current implementation is rather inefficient !
				dictionary.put(symbol, propertyAsTerm.toString());
			}
		}
		return dictionary;
	}
	
	public abstract AbstractParser parse();
}
