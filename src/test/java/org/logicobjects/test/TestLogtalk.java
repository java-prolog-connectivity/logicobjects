package org.logicobjects.test;

import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.jpc.term.Atom;
import org.jpc.term.Query;
import org.jpc.term.Term;
import org.junit.Test;

/**
 * This class test some Logtalk functionality (to be completed)
 * @author scastro
 *
 */
public class TestLogtalk extends LocalLogicTest {

//	@Test
//	public void testLoadingLogtalk() {
//		LogicEngine.getDefault().loadLogtalk();
//	}

	
	@Test
	public void t() {
		Query query = logicUtil.createQuery("Op='::', current_op(_, Type, Op), atom_chars(Type, Chars), Chars=[_, f, _]");
		assertTrue(query.hasSolution());
		new Thread() {
			@Override
			public void run() {
				Atom atomOp = new Atom("::");
				//Query query = new Query("Op='?', current_op(_, Type, Op), atom_chars(Type, Chars), Chars=[_, f, _]", new Term[] {atomOp});
				//assertTrue( query.hasSolution() );
			}
			
		}.start();
	}
	
	/**
	 * verify that the Logtalk operators have been defined in the logic engine
	 */
	
	@Test
	public void testLogtalkOperators() {
		assertTrue(logicUtil.isBinaryOperator("::"));
		assertTrue(logicUtil.isUnaryOperator("::"));
	}

	@Test
	public void testCurrentObjects() {
		List<Term> currentObjects = logicUtil.currentLogtalkObjects();
		assertTrue(currentObjects.contains(new Atom("logtalk")));
	}
	
}
