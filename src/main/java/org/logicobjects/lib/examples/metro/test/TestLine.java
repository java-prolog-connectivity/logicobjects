package org.logicobjects.lib.examples.metro.test;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IStation;
import org.logicobjects.lib.examples.metro.Line;
import org.logicobjects.lib.examples.metro.Station;
import org.logicobjects.test.LocalLogicTest;
import static junit.framework.TestCase.*;

public class TestLine extends LocalLogicTest  {
	
	@Test
	public void testConnects() {
		IStation station1 = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		IStation station2 = LogicObjectFactory.getDefault().create(Station.class, "oxford_circus");
		IStation station3 = LogicObjectFactory.getDefault().create(Station.class, "charing_cross");
		ILine line1 = LogicObjectFactory.getDefault().create(Line.class, "central");
		assertTrue(line1.connects(station1, station2));
		assertFalse(line1.connects(station1, station3));
	}

	@Test
	public void testNumberSegments() {
		ILine line1 = LogicObjectFactory.getDefault().create(Line.class, "central");
		System.out.println("Number of segments of line " + line1 + ": " + line1.segments());
		
		ILine line2 = LogicObjectFactory.getDefault().create(Line.class, "unexisting_line");
		assertEquals(line2.segments(), 0);
	}

}
