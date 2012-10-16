package org.logicobjects.test.lib.examples.metro;

import org.logicobjects.LogicObjects;
import static org.logicobjects.LogicObjects.*;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.lib.examples.metro.Line;
import org.logicobjects.lib.examples.metro.Metro;
import org.logicobjects.lib.examples.metro.MetroFactory;
import org.logicobjects.lib.examples.metro.Station;

public class MetroLogicObjectProvider implements IMetroObjectProvider {

	@Override
	public Metro createMetro() {
		return newLogicObject(Metro.class);
	}

	@Override
	public MetroFactory createMetroFactory() {
		return newLogicObject(MetroFactory.class);
	}

	@Override
	public Line createLine(String name) {
		return newLogicObject(Line.class, name);
	}

	@Override
	public Station createStation(String name) {
		return newLogicObject(Station.class, name);
	}

}
