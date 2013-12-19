package org.logicobjects.test.examples.logicobjects;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.jpc.term.examples.metro.AbstractMetroTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.logicobjects.examples.metro.ILine;
import org.logicobjects.examples.metro.IStation;
import org.logicobjects.examples.metro.MetroFactory;
import org.minitoolbox.reflection.BeansUtil;

//@RunWith(Parameterized.class)
public class TestMetroFactory extends AbstractMetroTest {
	
	@Test
	public void testNewInstance() {
			ILine line = line("line1");
			assertNotNull(line);
			assertEquals(BeansUtil.getProperty(line, "id"), "line1");
			IStation station = station("station1");
			assertNotNull(station);
			assertEquals(BeansUtil.getProperty(station, "id"), "station1");
	}
	
}
