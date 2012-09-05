package org.logicobjects.test.lib.examples.metro;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IMetro;
import org.logicobjects.lib.examples.metro.IMetroFactory;
import org.logicobjects.lib.examples.metro.IStation;
import org.logicobjects.test.LocalLogicTest;
import org.logicobjects.test.lib.examples.metrojpl.MetroJplObjectProvider;

public class AbstractMetroTest extends LocalLogicTest implements IMetroObjectProvider {

	private IMetroObjectProvider metroObjectProvider;
	
	
	public AbstractMetroTest() {
		this.metroObjectProvider = new MetroLogicObjectProvider();
	}
	/*
	public AbstractObjectProviderTest(IMetroObjectProvider metroObjectProvider) {
		this.metroObjectProvider = metroObjectProvider;
	}
	*/
	public IMetroObjectProvider getMetroObjectProvider() {
		return metroObjectProvider;
	}

	public void setMetroObjectProvider(IMetroObjectProvider metroObjectProvider) {
		this.metroObjectProvider = metroObjectProvider;
	}
	
	
	public IMetro createMetro() {
		return metroObjectProvider.createMetro();
	}

	public IMetroFactory createMetroFactory() {
		return metroObjectProvider.createMetroFactory();
	}

	public ILine createLine(String name) {
		return metroObjectProvider.createLine(name);
	}

	public IStation createStation(String name) {
		return metroObjectProvider.createStation(name);
	}

	/*
	@Parameters
	public static Collection<IMetroObjectProvider[]> objectProviders() {
		return Arrays.asList(new IMetroObjectProvider[][] {
			new IMetroObjectProvider[]{
				new MetroLogicObjectProvider()
			},
			new IMetroObjectProvider[]{
					new MetroJplObjectProvider()
				}
		});
	}
	*/
}
