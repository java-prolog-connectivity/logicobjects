package org.logicobjects.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import static org.logicobjects.instrumentation.AbstractParser.INSTANCE_PROPERTY_PREFIX;
import static org.logicobjects.instrumentation.AbstractParser.JAVA_NAME_REX;
import static org.logicobjects.instrumentation.LogicMethodParser.ALL_ARGUMENTS_SUFFIX;
import static org.logicobjects.instrumentation.LogicMethodParser.METHOD_ARGUMENTS_TAG;
import static org.logicobjects.instrumentation.LogicMethodParser.ARGUMENT_PREFIX;
import static org.logicobjects.instrumentation.LogicMethodParser.BEGIN_JAVA_VALUE_EXP;
import static org.logicobjects.instrumentation.LogicMethodParser.END_JAVA_EXPRESSION_BLOCK;
import static org.logicobjects.instrumentation.LogicMethodParser.QUERY_TAG;
import static org.logicobjects.instrumentation.LogicMethodParser.RETURN_TAG;
import static org.logicobjects.instrumentation.AbstractParser.isInstancePropertySymbol;
import static org.logicobjects.instrumentation.LogicMethodParser.getExpressionValue;
import static org.logicobjects.instrumentation.LogicMethodParser.getJavaExpressions;
import static org.logicobjects.instrumentation.LogicMethodParser.isValidJavaExpression;
import static org.logicobjects.instrumentation.LogicMethodParser.methodArgumentSymbol;
import static org.logicobjects.instrumentation.LogicMethodParser.normalizeExpression;
import static org.logicobjects.instrumentation.LogicMethodParser.scanMethodSymbols;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.logicobjects.instrumentation.LogicMethodParser;
import org.logicobjects.instrumentation.LogicMethodParsingData;


public class TestParsingExpressions extends LocalLogicTest {
	
	@Test
	public void testParseJavaExpressions() {
		String beginExp = BEGIN_JAVA_VALUE_EXP;
		String endExp = END_JAVA_EXPRESSION_BLOCK;
		
		String test = "xxx"+beginExp + "match1" + endExp + "xxx"+beginExp + "match1" + endExp + "xxx" + beginExp + "match2" + endExp +"xxx";
		List<String> javaExpressions = getJavaExpressions(test);
		
		String delimitedExp1 = javaExpressions.get(0);
		String exp1 = getExpressionValue(delimitedExp1);
		assertEquals(delimitedExp1, beginExp+"match1"+endExp);
		assertEquals(exp1, "match1");
		
		String delimitedExp2 = javaExpressions.get(1);
		String exp2 = getExpressionValue(delimitedExp2);
		assertEquals(delimitedExp2, beginExp+"match2"+endExp);
		assertEquals(exp2, "match2");
	}


	@Test
	public void testInstancePropertySymbols() {
		//There are instance property symbols
		assertTrue(isInstancePropertySymbol(INSTANCE_PROPERTY_PREFIX+"this"));
		assertTrue(isInstancePropertySymbol(INSTANCE_PROPERTY_PREFIX+"property1"));
		assertTrue(isInstancePropertySymbol(INSTANCE_PROPERTY_PREFIX+"$_property1"));
		//There are not instance property symbols
		assertFalse(isInstancePropertySymbol(INSTANCE_PROPERTY_PREFIX));
		assertFalse(isInstancePropertySymbol("this"));
		assertFalse(isInstancePropertySymbol(INSTANCE_PROPERTY_PREFIX+" this"));
	}
	
	@Test
	public void testParametersSymbols() {
		assertEquals(methodArgumentSymbol(1), ARGUMENT_PREFIX+1);
		assertEquals(methodArgumentSymbol(10), ARGUMENT_PREFIX+10);
	}
	
	@Test
	public void testGetAllSymbols() {
		//String beginExp = BEGIN_IMMEDIATE_JAVA_EXPRESSION;
		//String endExp = END_JAVA_EXPRESSION_BLOCK;
		
		List<String> symbols = scanMethodSymbols(
			INSTANCE_PROPERTY_PREFIX+"this"
			+" xxx " 
			+ ARGUMENT_PREFIX + "1" 
			+ " xxx " 
			+ INSTANCE_PROPERTY_PREFIX+"this" + //this should be ignored, since the symbol already exists
			" xxx " 
			+ ARGUMENT_PREFIX + ALL_ARGUMENTS_SUFFIX
			//+ beginExp + PARAMETERS_PREFIX + "2" + endExp //this should be ignored since it is inside a Java block
		);
		assertEquals(symbols.get(0), INSTANCE_PROPERTY_PREFIX+"this");
		assertEquals(symbols.get(1), ARGUMENT_PREFIX + "1");
		assertEquals(symbols.get(2), ARGUMENT_PREFIX + ALL_ARGUMENTS_SUFFIX);
		assertEquals(symbols.size(), 3);
	}
	

	
	@Test
	public void testNormalizeJavaExpressions() {
		assertNull(normalizeExpression(null));
		assertNull(normalizeExpression(""));
		assertNull(normalizeExpression(" "));
		assertNull(normalizeExpression(";"));
		assertNull(normalizeExpression("; "));
		assertEquals(normalizeExpression("abc"), "abc");
		assertEquals(normalizeExpression(" abc "), "abc");
		assertEquals(normalizeExpression("abc;"), "abc");
		assertEquals(normalizeExpression("abc;;"), "abc");
		assertEquals(normalizeExpression(" abc;; "), "abc");
		assertEquals(normalizeExpression(" abc ;  ; "), "abc");
	}
	
	@Test
	public void testJavaNames() {
		Pattern pattern = Pattern.compile(JAVA_NAME_REX);
		//System.out.println(pattern.matcher("aName").matches());
		assertTrue(pattern.matcher("aName").matches());
		//System.out.println(pattern.matcher("$aName").matches());
		assertTrue(pattern.matcher("$aName").matches());
		//System.out.println(pattern.matcher("_aName").matches());
		assertTrue(pattern.matcher("_aName").matches());
		//System.out.println(pattern.matcher("___aName").matches());
		assertTrue(pattern.matcher("___aName").matches());
		//System.out.println(pattern.matcher("$$$aName").matches());
		assertTrue(pattern.matcher("$$$aName").matches());
		//System.out.println(pattern.matcher("_$aName").matches());
		assertTrue(pattern.matcher("_$aName").matches());
	}
	
	@Test
	public void testValidJavaExpressions() {
		assertFalse(isValidJavaExpression(null));
		assertFalse(isValidJavaExpression(""));
		assertFalse(isValidJavaExpression(";"));
		assertFalse(isValidJavaExpression("   "));
		assertTrue(isValidJavaExpression(" x "));
	}

	@Test
	public void testDecomposeLogicString() {
		String query = "myQuery";
		String params = "myParams";
		String returnValue = "returnValue";
		String testString = QUERY_TAG+query+METHOD_ARGUMENTS_TAG+params+RETURN_TAG+returnValue;
		LogicMethodParsingData parsedData = LogicMethodParser.decomposeLogicString(testString);
		
		assertEquals(parsedData.getQueryString(), query);
		assertEquals(parsedData.getMethodArguments()[0], params);
		assertEquals(parsedData.getSolutionString(), returnValue);
		
		query = "";
		params = "";
		testString = QUERY_TAG+query+METHOD_ARGUMENTS_TAG+params+RETURN_TAG+returnValue;
		parsedData = LogicMethodParser.decomposeLogicString(testString);
		assertEquals(parsedData.getQueryString(), null);
		assertEquals(parsedData.getMethodArguments(), null);
		assertEquals(parsedData.getSolutionString(), returnValue);
	}

}
