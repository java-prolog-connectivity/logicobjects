package org.logicobjects.test;

import static junit.framework.Assert.assertTrue;
import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;
import jpl.Util;
import jpl.Variable;

import org.junit.Test;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.logicengine.jpl.DefaultJplConfiguration;
import org.logicobjects.util.LogicUtil;

/**
 * This class test some Prolog functionality (to be completed)
 * @author scastro
 *
 */
public class TestJpl extends LocalLogicTest {

	/**
	 * Apparently JPL does not work correctly wiht multithreading
	 * The following method illustrates the problem
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new DefaultJplConfiguration().configure();
			Query query;
			query = new Query("true");
			System.out.println(query.hasSolution()); //succeeds
			
			Thread t = new Thread() {
				@Override
				public void run() {
					Query query2 = new Query("true");
					System.out.println(query2.hasSolution()); //succeeds
				}
			};
			t.start();
			t.join();

			query = new Query("true");
			System.out.println(query.hasSolution()); //fails
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
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
	public void testMultithread0() {
		try {

			Thread t = new Thread() {
				@Override
				public void run() {
					Query query = new Query("assert(x)");
					//System.out.println("T1 assert(x):" + query.hasSolution());
					query = new Query("x");
					System.out.println("T1 x:" + query.hasSolution());
				}
			};
			t.start();
			t.join();
			
			
			t = new Thread() {
				@Override
				public void run() {
					Query query;// = new Query("assert(x)");
					//System.out.println("T2 assert(x):" + query.hasSolution());
					query = new Query("x");
					System.out.println("T2 x:" + query.hasSolution());
				}
			};
			t.start();
			t.join();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	@Test
	public void testMultithread() {
		//new DefaultJplConfiguration().configure();
		final Term term = new Atom("x");
		Term assertTerm = LogicUtil.assertTerm(term);
		assertTrue(new Query(assertTerm).hasSolution());
		assertTrue(new Query(term).hasSolution());
		//testing that there is one logic engine per thread
		QueryThreadEvaluator threadEvaluator = new QueryThreadEvaluator(term);
		threadEvaluator.startAndJoin();
		assertTrue(threadEvaluator.hasSolution()); //wtf
		
		//assertTrue(new Query(term).hasSolution());
	}
	*/
	
	@Test
	public void testTermToText() {
		Term argTerm = new Variable("Var1");
		Compound term = new Compound("::", new Term[] {new Atom("list"), new Compound("member", new Term[] {argTerm, Util.termArrayToList(new Term[] {new Atom("1"), new Atom("2")})})});

		String text = LogicEngine.getDefault().termToText(term);
		System.out.println("Term as text: " + text);
	}

}
