package org.logicobjects.test.lib.examples.metro;

import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.lib.examples.metro.Line;
import org.logicobjects.lib.examples.metro.Metro;
import org.logicobjects.lib.examples.metro.MetroFactory;
import org.logicobjects.lib.examples.metro.Station;

public class MetroLogicObjectProvider implements IMetroObjectProvider {

	@Override
	public Metro createMetro() {
		return LogicObjectFactory.getDefault().create(Metro.class);
	}

	@Override
	public MetroFactory createMetroFactory() {
		return LogicObjectFactory.getDefault().create(MetroFactory.class);
	}

	@Override
	public Line createLine(String name) {
		return LogicObjectFactory.getDefault().create(Line.class, name);
	}

	@Override
	public Station createStation(String name) {
		return LogicObjectFactory.getDefault().create(Station.class, name);
	}

}
