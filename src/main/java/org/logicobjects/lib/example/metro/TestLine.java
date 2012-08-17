package org.logicobjects.lib.example.metro;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.LocalLogicTest;
import static junit.framework.TestCase.*;

public class TestLine extends LocalLogicTest  {
	
	@Test
	public void testConnects() {
		Station station1 = LogicObjectFactory.getDefault().create(Station.class);
		station1.setName("bond_street");
		Station station2 = LogicObjectFactory.getDefault().create(Station.class);
		station2.setName("oxford_circus");
		Station station3 = LogicObjectFactory.getDefault().create(Station.class);
		station3.setName("charing_cross");
		Line line1 = LogicObjectFactory.getDefault().create(Line.class);
		line1.setName("central");
		assertTrue(line1.connects(station1, station2));
		assertFalse(line1.connects(station1, station3));
	}

	@Test
	public void testNumberSegments() {
		Line line1 = LogicObjectFactory.getDefault().create(Line.class);
		line1.setName("central");
		System.out.println("Number of segments of line " + line1 + ": " + line1.segments());
		
		Line line2 = LogicObjectFactory.getDefault().create(Line.class);
		line2.setName("unexisting_line");
		assertEquals(line2.segments(), 0);
	}

	

}
