package org.logicobjects.lib.example.metro;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.LocalLogicTest;
import static junit.framework.TestCase.*;

public class TestLine extends LocalLogicTest  {
	
	@Test
	public void testConnects() {
		Station station1 = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		//station1.setName("bond_street");
		Station station2 = LogicObjectFactory.getDefault().create(Station.class, "oxford_circus");
		Station station3 = LogicObjectFactory.getDefault().create(Station.class, "charing_cross");
		Line line1 = LogicObjectFactory.getDefault().create(Line.class, "central");
		assertTrue(line1.connects(station1, station2));
		assertFalse(line1.connects(station1, station3));
	}

	@Test
	public void testNumberSegments() {
		Line line1 = LogicObjectFactory.getDefault().create(Line.class, "central");
		System.out.println("Number of segments of line " + line1 + ": " + line1.segments());
		
		Line line2 = LogicObjectFactory.getDefault().create(Line.class, "unexisting_line");
		assertEquals(line2.segments(), 0);
	}

}
