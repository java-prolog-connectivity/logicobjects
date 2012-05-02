package org.logicobjects.test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.logicobjects.instrumentation.AbstractLogicMethodParser.*;
import org.junit.Test;

public class TestParametersParser extends AbstractLogicTest {
	
	@Test
	public void testParseJavaExpressions() {
		String beginExp = BEGIN_JAVA_EXPRESSION;
		String endExp = END_JAVA_EXPRESSION;
		
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
		assertEquals(parameterSymbol(1), PARAMETERS_PREFIX+1);
		assertEquals(parameterSymbol(10), PARAMETERS_PREFIX+10);
	}
	
	@Test
	public void testGetAllSymbols() {
		String beginExp = BEGIN_JAVA_EXPRESSION;
		String endExp = END_JAVA_EXPRESSION;
		
		List<String> symbols = getAllSymbols(
			INSTANCE_PROPERTY_PREFIX+"this"
			+" xxx " 
			+ PARAMETERS_PREFIX + "1" 
			+ " xxx " 
			+ INSTANCE_PROPERTY_PREFIX+"this" + //this should be ignored, since the symbol already exists
			" xxx " 
			+ PARAMETERS_PREFIX + ALL_PARAMS_SUFFIX
			+ beginExp + PARAMETERS_PREFIX + "2" + endExp //this should be ignored since it is inside a Java block
		);
		assertEquals(symbols.get(0), INSTANCE_PROPERTY_PREFIX+"this");
		assertEquals(symbols.get(1), PARAMETERS_PREFIX + "1");
		assertEquals(symbols.get(2), PARAMETERS_PREFIX + ALL_PARAMS_SUFFIX);
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

}
