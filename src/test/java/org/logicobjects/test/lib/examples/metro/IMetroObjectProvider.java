package org.logicobjects.test.lib.examples.metro;

import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IMetro;
import org.logicobjects.lib.examples.metro.IMetroFactory;
import org.logicobjects.lib.examples.metro.IStation;

public interface IMetroObjectProvider {
	public IMetro createMetro();
	public IMetroFactory createMetroFactory();
	public ILine createLine(String name);
	public IStation createStation(String name);
}
