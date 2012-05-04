package org.logicobjects.lib.example.metro;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.LocalLogicTest;
import static junit.framework.TestCase.*;

public class TestLine extends LocalLogicTest  {

	@Test
	public void testNumberSegments() {
		Line line1 = LogicObjectFactory.getDefault().create(Line.class);
		line1.setName("central");
		System.out.println("Number of segments of line " + line1 + ": " + line1.segments());
		
		Line line2 = LogicObjectFactory.getDefault().create(Line.class);
		line2.setName("inexisting_station");
		assertEquals(line2.segments(), 0);
	}
	

}
