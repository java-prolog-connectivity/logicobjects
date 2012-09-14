package org.logicobjects.test.lib.examples.metro;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IStation;

public class TestLine extends AbstractMetroTest  {
	

	@Test
	public void testConnects() {
		IStation station1 = createStation("bond_street");
		IStation station2 = createStation("oxford_circus");
		IStation station3 = createStation("charing_cross");
		ILine line1 = createLine("central");
		assertTrue(line1.connects(station1, station2));
		assertFalse(line1.connects(station1, station3));
	}

	@Test
	public void testNumberSegments() {
		ILine line1 = createLine("central");
		assertEquals(line1.segments(), 2);
		//System.out.println("Number of segments of line " + line1 + ": " + line1.segments());
		
		ILine line2 = createLine("unexisting_line");
		assertEquals(line2.segments(), 0);
	}

}
