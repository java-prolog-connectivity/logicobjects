package org.logicobjects.test;

import static junit.framework.Assert.assertTrue;
import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;
import jpl.Util;
import jpl.Variable;

import org.jpc.logicengine.LogicEngine;
import org.jpc.logicengine.jpl.DefaultJplConfiguration;
import org.junit.Test;
import org.logicobjects.test.configuration.TestSuiteJPLConfiguration;

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
		//Atom atom = new Atom("%");
		Atom atom = new Atom("'");
		//Atom atom = new Atom("a b c");
		//Atom atom = new Atom("a");
		System.out.println(atom.toString());
		Atom atom2 = (Atom) Util.textToTerm(atom.toString());
		System.out.println(atom2.name());
		Atom atom3 = (Atom) Util.textToTerm("'a'");
		System.out.println(atom3.name());
		Query testQuery = new Query(new Atom("true"));
		assertTrue(testQuery.hasSolution());
	}
	
	@Test
	public void testVersion() {
		System.out.println("PROLOG DIALECT: "+logicEngineConfig.getEngine().prologDialect());
	}
	

	//@Test
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
	


}
