package org.logicobjects.lib.example.metro;

import java.util.List;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.LocalLogicTest;
import static junit.framework.TestCase.*;

public class TestMetro extends LocalLogicTest {

	@Test
	public void testAllLines() {
		Metro metro = LogicObjectFactory.getDefault().create(Metro.class);
		List<Line> lines = metro.lines();
		assertNotNull(lines);
		System.out.println("Number of lines: " + lines.size());
		System.out.print("Lines: ");
		for(Line line : lines) {
			System.out.print(line + " ");
		}
		System.out.println();
	}
	
	@Test
	public void testIsLine() {
		Metro metro = LogicObjectFactory.getDefault().create(Metro.class);
		Line line = metro.line("jubilee");
		assertNotNull(line);
		System.out.println("Line: " + line);
		
		line = metro.line("line1");
		assertNull(line);
	}
	
}
