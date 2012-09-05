package org.logicobjects.test.lib.examples.metrojpl;

import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IMetro;
import org.logicobjects.lib.examples.metro.IMetroFactory;
import org.logicobjects.lib.examples.metro.IStation;
import org.logicobjects.lib.examples.metrojpl.LineJpl;
import org.logicobjects.lib.examples.metrojpl.MetroFactoryJpl;
import org.logicobjects.lib.examples.metrojpl.MetroJpl;
import org.logicobjects.lib.examples.metrojpl.StationJpl;
import org.logicobjects.test.lib.examples.metro.IMetroObjectProvider;

public class MetroJplObjectProvider implements IMetroObjectProvider {

	@Override
	public IMetro createMetro() {
		return new MetroJpl();
	}

	@Override
	public IMetroFactory createMetroFactory() {
		return new MetroFactoryJpl();
	}

	@Override
	public ILine createLine(String name) {
		return new LineJpl(name);
	}

	@Override
	public IStation createStation(String name) {
		return new StationJpl(name);
	}

}
