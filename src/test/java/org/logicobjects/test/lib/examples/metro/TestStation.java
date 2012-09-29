package org.logicobjects.test.lib.examples.metro;

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
import org.reflectiveutils.BeansUtil;

public class TestStation extends AbstractMetroTest {


	@Test
	public void testAllConnections() {
		IStation station1 = createStation("bond_street");
		List<IStation> connectedStations = station1.connected();
		assertEquals(connectedStations.size(), 2);
		
//		System.out.println("Stations connected to " + station1 + ": " + connectedStations.size());
//		for(IStation connectedStation: connectedStations) {
//			System.out.println("- " + connectedStation);
//		}
		
		IStation station2 = createStation("inexisting_station");
		assertEquals(station2.connected().size(), 0);
	}
	
	@Test
	public void testOneConnection() {
		IStation station = createStation("bond_street");
		ILine line1 = createLine("central");
		ILine line2 = createLine("northern");
		IStation connectedStation = station.connected(line1);
		assertNotNull(connectedStation);
		assertEquals(BeansUtil.getProperty(connectedStation, "name"), "oxford_circus");
		//System.out.println("The station " + station + " is connected with " + connectedStation + " by means of the line " + line1);
		connectedStation = station.connected(line2);  //no connected with any station by means of line2
		assertNull(connectedStation);
	}
	
	@Test
	public void testNumberConnections() {
		IStation station = createStation("bond_street");
		assertEquals(station.numberConnections(), 2);
		//System.out.println("Number of connections of " + station + ": " + station.numberConnections());
	}
	
	@Test
	public void testIsConnected() {
		IStation station1 = createStation("bond_street");
		IStation station2 = createStation("oxford_circus");
		IStation station3 = createStation("charing_cross");
		assertTrue(station1.connected(station2));
		assertFalse(station1.connected(station3));
	}
	
	
	
	@Test
	public void testAllNearbyStations() {
		IStation station = createStation("bond_street");
		List<IStation> nearbyStations = station.nearby();
		assertEquals(nearbyStations.size(), 4);
//		System.out.println("Stations nearby to " + station + ": " + nearbyStations.size());
//		for(IStation nearbyStation: nearbyStations) {
//			System.out.println("- " + nearbyStation);
//		}
		
	}
	
	@Test
	public void testNumberNearbyStations() {
		IStation station = createStation("bond_street");
		assertEquals(station.numberNearbyStations(), 4);
		//System.out.println("Number of nearby stations of " + station + ": " + station.numberNearbyStations());
	}
	
	@Test
	public void testIsNearby() {
		IStation station1 = createStation("bond_street");
		IStation station2 = createStation("oxford_circus");
		IStation station3 = createStation("charing_cross");
		IStation station4 = createStation("piccadilly_circus");
		assertTrue(station1.nearby(station2));
		assertTrue(station1.nearby(station3));
		assertFalse(station1.nearby(station4));
	}
	
	
	
	@Test
	public void testIntermediateStations() {
		IStation station1 = createStation("bond_street");
		IStation station2 = createStation("oxford_circus");
		IStation station3 = createStation("piccadilly_circus");
		IStation station4 = createStation("inexisting_station");
		
		List<IStation> intermediateStations = station1.intermediateStations(station2);
		assertEquals(intermediateStations.size(), 0);
		
		intermediateStations = station1.intermediateStations(station3);
		assertEquals(intermediateStations.size(), 1);
		
//		System.out.println("Intermediate stations from " + station1 + " to " + station3);
//		for(IStation intermediateStation: intermediateStations) {
//			System.out.println("- " + intermediateStation);
//		}
		
		assertNull(station1.intermediateStations(station4));
	}
	
	@Test
	public void testNumberReachableStations() {
		IStation station = createStation("bond_street");
		assertEquals(station.numberReachableStations(), 22);
		//System.out.println("Number of reachable stations from " + station + ": " + station.numberReachableStations());
	}
	
	@Test
	public void testIsReachable() {
		IStation station1 = createStation("bond_street");
		IStation station2 = createStation("oxford_circus");
		IStation station3 = createStation("charing_cross");
		IStation station4 = createStation("piccadilly_circus");
		IStation station5 = createStation("inexisting_station");
		assertTrue(station1.reachable(station2));
		assertTrue(station1.reachable(station3));
		assertTrue(station1.reachable(station4));
		assertFalse(station1.reachable(station5));
	}
	
}
