package org.logicobjects.lib.example.metro;

import java.util.List;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.LocalLogicTest;

public class TestStation extends LocalLogicTest {

	@Test
	public void testOneConnection() {
		//Metro metro = LogicObjectFactory.getDefault().create(Metro.class);
		Station station = LogicObjectFactory.getDefault().create(Station.class);
		station.setName("bond_street");
		Line line = LogicObjectFactory.getDefault().create(Line.class);
		line.setName("central");
		Station connectedStation = station.connected(line);
		System.out.println(connectedStation);
	}

	@Test
	public void testAllConnections() {
		Station station = LogicObjectFactory.getDefault().create(Station.class);
		station.setName("bond_street");
		List<Station> connectedStations = station.connected();
		System.out.println("Stations connected to " + station + ": " + connectedStations.size());
		for(Station connectedStation: connectedStations) {
			System.out.println("- " + connectedStation);
		}
	}
	
	@Test
	public void testNumberConnections() {
		Station station = LogicObjectFactory.getDefault().create(Station.class);
		station.setName("bond_street");
		System.out.println("Number of connections of " + station + ": " + station.numberConnections());
	}
	
	
	
}
