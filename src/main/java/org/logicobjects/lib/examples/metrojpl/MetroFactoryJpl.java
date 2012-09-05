package org.logicobjects.lib.examples.metrojpl;

import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IMetroFactory;
import org.logicobjects.lib.examples.metro.IStation;

public class MetroFactoryJpl implements IMetroFactory {

	public IStation station(String name) {return new StationJpl(name);}
	
	public ILine line(String name) {return new LineJpl(name);}
	
}
