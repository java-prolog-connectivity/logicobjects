package org.logicobjects.test;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;
import jpl.Util;
import jpl.Variable;
import org.junit.Test;
import org.logicobjects.core.LogicEngine;
import static junit.framework.TestCase.*;

/**
 * This class test some Prolog functionality (to be completed)
 * @author sergioc78
 *
 */
public class TestJpl extends LocalLogicTest {
	
	@Test
	public void testConnection() {
		Query testQuery = new Query("true");
		assertTrue(testQuery.hasSolution());
	}
	
	@Test
	public void testVersion() {
		System.out.println("PROLOG DIALECT: "+LogicEngine.getDefault().prologDialect());
	}
	
	
	@Test
	public void testTermToText() {
		Compound term = new Compound("::", new Term[] {new Atom("list"), new Compound("member", new Term[] {new Variable("Var1"), Util.termArrayToList(new Term[] {new Atom("1"), new Atom("2")})})});
		System.out.println("Original term: " + term);

		String text = LogicEngine.getDefault().termToText(term);
		System.out.println("Term as text: " + text);
	}
	

}
