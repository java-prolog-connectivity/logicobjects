package org.logicobjects.test.lib.examples.metrojpl;

import org.logicobjects.test.lib.examples.metro.TestMetroFactory;

public class TestMetroFactoryJpl extends TestMetroFactory {
	public TestMetroFactoryJpl() {
		setMetroObjectProvider(new MetroJplObjectProvider());
	}
}
