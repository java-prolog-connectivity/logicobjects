package org.logicobjects.instrumentation;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.logicobjects.core.LogicClass;

public class LogicClassParser extends AbstractParser {

	public static final String CLASS_NAME_TAG = "~CLASS_NAME~";
	
	public static final String CLASS_ARGUMENTS_TAG = "~CLASS_ARGUMENTS~";
	
	public static final String CLASS_LIST_ARGUMENTS_TAG = "~CLASS_LIST_ARGUMENTS~";
	
	private LogicClass logicObjectClass;

	
	private String unparsedClassName;
	private String unparsedLogicArgumentsString;
	private String unparsedListArguments; //the term expression matching with all the arguments as a list
	
	public LogicClassParser(Class clazz) {
		this(LogicClass.findLogicClass(clazz));
	}

	public LogicClassParser(LogicClass logicObjectClass) {
		this.logicObjectClass = logicObjectClass;
	}

	@Override
	public LogicClassParser parse() {
		LogicClassParsingData unparsedData = logicObjectClass.getParsingData();
		unparsedClassName = unparsedData.getName();
		unparsedLogicArgumentsString = concatenateTokens(asNotNullStringList(unparsedData.getClassArguments()));
		unparsedListArguments = unparsedData.getArgumentsAsListProperty();
		allLogicStrings = CLASS_NAME_TAG + unparsedClassName + CLASS_ARGUMENTS_TAG + unparsedLogicArgumentsString + CLASS_LIST_ARGUMENTS_TAG + unparsedListArguments;
		foundSymbols = scanClassSymbols(allLogicStrings);
		return this;
	}

	public static List<String> scanClassSymbols(String s) {
		return scanSymbols(s, Pattern.quote(INSTANCE_PROPERTY_PREFIX)+JAVA_NAME_REX);
	}
	/*
	public ParsedLogicClass parsedLogicClass(Object targetObject) {
		LogicClassParsingData parsedData = parsedData(targetObject);
		return new ParsedLogicClass(getLogicClass(), targetObject, parsedData);
	}
*/
	private LogicClassParsingData parsedData(Object targetObject) {
		Map<String, String> dictionary = propertiesSymbolsReplacementMap(targetObject, foundSymbols);
		String allLogicStringsProcessed = replaceSymbols(allLogicStrings, dictionary);
		LogicClassParsingData parsedData = decomposeLogicString(allLogicStringsProcessed);
		return parsedData;
	}
	
	
	public static LogicClassParsingData decomposeLogicString(String allLogicStringsProcessed) {
		LogicClassParsingData parsedData = new LogicClassParsingData();
		Pattern pattern = Pattern.compile(Pattern.quote(CLASS_NAME_TAG)+"(.*)"+Pattern.quote(CLASS_ARGUMENTS_TAG)+"(.*)"+Pattern.quote(CLASS_LIST_ARGUMENTS_TAG)+"(.*)");
		Matcher matcher = pattern.matcher(allLogicStringsProcessed);
		matcher.find();
		String className = matcher.group(1);
		if(!className.isEmpty())
			parsedData.setName(className);
		String arguments = matcher.group(2);
		if(!arguments.isEmpty())
			parsedData.setClassArguments(splitConcatenatedTokens(arguments));
		String argumentsAsListProperty = matcher.group(3);
		if(!argumentsAsListProperty.isEmpty())
			parsedData.setArgumentsAsListProperty(argumentsAsListProperty);
		return parsedData;
	}

}

