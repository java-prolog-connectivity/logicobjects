package org.logicobjects.lib.examples.metro.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IStation;
import org.logicobjects.lib.examples.metro.Line;
import org.logicobjects.lib.examples.metro.Station;
import org.logicobjects.test.LocalLogicTest;

public class TestStation extends LocalLogicTest {

	
	@Test
	public void testAllConnections() {
		IStation station1 = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		List<IStation> connectedStations = station1.connected();
		System.out.println("Stations connected to " + station1 + ": " + connectedStations.size());
		for(IStation connectedStation: connectedStations) {
			System.out.println("- " + connectedStation);
		}
		
		IStation station2 = LogicObjectFactory.getDefault().create(Station.class, "inexisting_station");
		assertEquals(station2.connected().size(), 0);
	}
	
	@Test
	public void testOneConnection() {
		IStation station = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		ILine line1 = LogicObjectFactory.getDefault().create(Line.class, "central");
		ILine line2 = LogicObjectFactory.getDefault().create(Line.class, "northern");
		IStation connectedStation = station.connected(line1);
		assertNotNull(connectedStation);
		System.out.println(connectedStation);
		connectedStation = station.connected(line2);  //no connected with any station by means of line2
		assertNull(connectedStation);
	}
	
	@Test
	public void testNumberConnections() {
		IStation station = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		System.out.println("Number of connections of " + station + ": " + station.numberConnections());
	}
	
	@Test
	public void testIsConnected() {
		IStation station1 = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		IStation station2 = LogicObjectFactory.getDefault().create(Station.class, "oxford_circus");
		IStation station3 = LogicObjectFactory.getDefault().create(Station.class, "charing_cross");
		assertTrue(station1.connected(station2));
		assertFalse(station1.connected(station3));
	}
	
	
	
	@Test
	public void testAllNearbyStations() {
		IStation station = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		List<IStation> nearbyStations = station.nearby();
		System.out.println("Stations nearby to " + station + ": " + nearbyStations.size());
		for(IStation nearbyStation: nearbyStations) {
			System.out.println("- " + nearbyStation);
		}
	}
	
	@Test
	public void testNumberNearbyStations() {
		IStation station = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		System.out.println("Number of nearby stations of " + station + ": " + station.numberNearbyStations());
	}
	
	@Test
	public void testIsNearby() {
		IStation station1 = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		IStation station2 = LogicObjectFactory.getDefault().create(Station.class, "oxford_circus");
		IStation station3 = LogicObjectFactory.getDefault().create(Station.class, "charing_cross");
		IStation station4 = LogicObjectFactory.getDefault().create(Station.class, "piccadilly_circus");
		assertTrue(station1.nearby(station2));
		assertTrue(station1.nearby(station3));
		assertFalse(station1.nearby(station4));
	}
	
	
	
	@Test
	public void testIntermediateStations() {
		IStation station1 = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		IStation station2 = LogicObjectFactory.getDefault().create(Station.class, "oxford_circus");
		IStation station3 = LogicObjectFactory.getDefault().create(Station.class, "piccadilly_circus");
		IStation station4 = LogicObjectFactory.getDefault().create(Station.class, "inexisting_station");
		
		List<IStation> intermediateStations = station1.intermediateStations(station2);
		assertEquals(intermediateStations.size(), 0);
		
		intermediateStations = station1.intermediateStations(station3);
		System.out.println("Intermediate stations from " + station1 + " to " + station3);
		for(IStation intermediateStation: intermediateStations) {
			System.out.println("- " + intermediateStation);
		}
		
		assertNull(station1.intermediateStations(station4));
	}
	
	@Test
	public void testNumberReachableStations() {
		IStation station = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		System.out.println("Number of reachable stations from " + station + ": " + station.numberReachableStations());
	}
	
	@Test
	public void testIsReachable() {
		IStation station1 = LogicObjectFactory.getDefault().create(Station.class, "bond_street");
		IStation station2 = LogicObjectFactory.getDefault().create(Station.class, "oxford_circus");
		IStation station3 = LogicObjectFactory.getDefault().create(Station.class, "charing_cross");
		IStation station4 = LogicObjectFactory.getDefault().create(Station.class, "piccadilly_circus");
		IStation station5 = LogicObjectFactory.getDefault().create(Station.class, "inexisting_station");
		assertTrue(station1.reachable(station2));
		assertTrue(station1.reachable(station3));
		assertTrue(station1.reachable(station4));
		assertFalse(station1.reachable(station5));
	}
	
}
