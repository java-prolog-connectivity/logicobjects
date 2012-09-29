package org.logicobjects.test.lib.examples.metro;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IStation;
import org.logicobjects.lib.examples.metro.MetroFactory;
import org.reflectiveutils.BeansUtil;

//@RunWith(Parameterized.class)
public class TestMetroFactory extends AbstractMetroTest {
	
	@Test
	public void testNewInstance() {
			ILine line = createLine("line1");
			assertNotNull(line);
			assertEquals(BeansUtil.getProperty(line, "name"), "line1");
			IStation station = createStation("station1");
			assertNotNull(station);
			assertEquals(BeansUtil.getProperty(station, "name"), "station1");
	}
	
}
