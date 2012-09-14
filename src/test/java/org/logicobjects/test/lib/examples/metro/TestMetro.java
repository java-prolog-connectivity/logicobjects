package org.logicobjects.test.lib.examples.metro;

import java.util.List;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IMetro;
import org.logicobjects.lib.examples.metro.Metro;
import org.logicobjects.test.LocalLogicTest;
import static junit.framework.TestCase.*;

public class TestMetro extends AbstractMetroTest {


	@Test
	public void testAllLines() {
		IMetro metro = createMetro();
		List<ILine> lines = metro.lines();
		assertNotNull(lines);
		/*
		System.out.println("Number of lines: " + lines.size());
		System.out.print("Lines: ");
		for(ILine line : lines) {
			System.out.print(line + " ");
		}
		System.out.println();
		*/
	}
	
	@Test
	public void testIsLine() {
		IMetro metro = createMetro();
		ILine line = metro.line("jubilee");
		assertNotNull(line);
		//System.out.println("Line: " + line);
		
		line = metro.line("line1");
		assertNull(line);
	}
	
}
