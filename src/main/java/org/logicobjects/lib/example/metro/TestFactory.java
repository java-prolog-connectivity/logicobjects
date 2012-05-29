package org.logicobjects.lib.example.metro;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.LocalLogicTest;
import static junit.framework.TestCase.*;

public class TestFactory extends LocalLogicTest {
	
	@Test
	public void testNewInstance() {
		Line line = MetroFactory.getDefault().line("line1");
		assertNotNull(line);
		assertEquals(line.getName(), "line1");
		
		Station station = MetroFactory.getDefault().station("station1");
		assertNotNull(station);
		assertEquals(station.getName(), "station1");
	}
}
