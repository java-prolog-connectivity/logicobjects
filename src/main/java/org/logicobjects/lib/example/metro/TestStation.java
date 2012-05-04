package org.logicobjects.lib.example.metro;

import java.util.List;
import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.LocalLogicTest;
import static junit.framework.TestCase.*;

public class TestStation extends LocalLogicTest {

	@Test
	public void testAllConnections() {
		Station station1 = LogicObjectFactory.getDefault().create(Station.class);
		station1.setName("bond_street");
		List<Station> connectedStations = station1.connected();
		System.out.println("Stations connected to " + station1 + ": " + connectedStations.size());
		for(Station connectedStation: connectedStations) {
			System.out.println("- " + connectedStation);
		}
		
		Station station2 = LogicObjectFactory.getDefault().create(Station.class);
		station2.setName("inexisting_station");
		assertEquals(station2.connected().size(), 0);
	}
	
	@Test
	public void testOneConnection() {
		Station station = LogicObjectFactory.getDefault().create(Station.class);
		station.setName("bond_street");
		Line line1 = LogicObjectFactory.getDefault().create(Line.class);
		line1.setName("central");
		Line line2 = LogicObjectFactory.getDefault().create(Line.class);
		line2.setName("northern");
		Station connectedStation = station.connected(line1);
		assertNotNull(connectedStation);
		System.out.println(connectedStation);
		connectedStation = station.connected(line2);  //no connected with any station by means of line2
		assertNull(connectedStation);
	}
	
	@Test
	public void testNumberConnections() {
		Station station = LogicObjectFactory.getDefault().create(Station.class);
		station.setName("bond_street");
		System.out.println("Number of connections of " + station + ": " + station.numberConnections());
	}
	
	@Test
	public void testIsConnected() {
		Station station1 = LogicObjectFactory.getDefault().create(Station.class);
		station1.setName("bond_street");
		Station station2 = LogicObjectFactory.getDefault().create(Station.class);
		station2.setName("oxford_circus");
		Station station3 = LogicObjectFactory.getDefault().create(Station.class);
		station3.setName("charing_cross");
		assertTrue(station1.connected(station2));
		assertFalse(station1.connected(station3));
	}
	
	
	
	@Test
	public void testAllNearbyStations() {
		Station station = LogicObjectFactory.getDefault().create(Station.class);
		station.setName("bond_street");
		List<Station> nearbyStations = station.nearby();
		System.out.println("Stations nearby to " + station + ": " + nearbyStations.size());
		for(Station nearbyStation: nearbyStations) {
			System.out.println("- " + nearbyStation);
		}
	}
	
	@Test
	public void testNumberNearbyStations() {
		Station station = LogicObjectFactory.getDefault().create(Station.class);
		station.setName("bond_street");
		System.out.println("Number of nearby stations of " + station + ": " + station.numberNearbyStations());
	}
	
	@Test
	public void testIsNearby() {
		Station station1 = LogicObjectFactory.getDefault().create(Station.class);
		station1.setName("bond_street");
		Station station2 = LogicObjectFactory.getDefault().create(Station.class);
		station2.setName("oxford_circus");
		Station station3 = LogicObjectFactory.getDefault().create(Station.class);
		station3.setName("charing_cross");
		Station station4 = LogicObjectFactory.getDefault().create(Station.class);
		station4.setName("piccadilly_circus");
		assertTrue(station1.nearby(station2));
		assertTrue(station1.nearby(station3));
		assertFalse(station1.nearby(station4));
	}
	
	
	
	@Test
	public void testIntermediateStations() {
		Station station1 = LogicObjectFactory.getDefault().create(Station.class);
		station1.setName("bond_street");
		Station station2 = LogicObjectFactory.getDefault().create(Station.class);
		station2.setName("oxford_circus");
		Station station3 = LogicObjectFactory.getDefault().create(Station.class);
		station3.setName("piccadilly_circus");
		Station station4 = LogicObjectFactory.getDefault().create(Station.class);
		station4.setName("inexisting_station");
		
		List<Station> intermediateStations = station1.intermediateStations(station2);
		assertEquals(intermediateStations.size(), 0);
		
		intermediateStations = station1.intermediateStations(station3);
		System.out.println("Intermediate stations from " + station1 + " to " + station3);
		for(Station intermediateStation: intermediateStations) {
			System.out.println("- " + intermediateStation);
		}
		
		assertNull(station1.intermediateStations(station4));
	}
	
	@Test
	public void testNumberReachableStations() {
		Station station = LogicObjectFactory.getDefault().create(Station.class);
		station.setName("bond_street");
		System.out.println("Number of reachable stations from " + station + ": " + station.numberReachableStations());
	}
	
	@Test
	public void testIsReachable() {
		Station station1 = LogicObjectFactory.getDefault().create(Station.class);
		station1.setName("bond_street");
		Station station2 = LogicObjectFactory.getDefault().create(Station.class);
		station2.setName("oxford_circus");
		Station station3 = LogicObjectFactory.getDefault().create(Station.class);
		station3.setName("charing_cross");
		Station station4 = LogicObjectFactory.getDefault().create(Station.class);
		station4.setName("piccadilly_circus");
		Station station5 = LogicObjectFactory.getDefault().create(Station.class);
		station5.setName("inexisting_station");
		assertTrue(station1.reachable(station2));
		assertTrue(station1.reachable(station3));
		assertTrue(station1.reachable(station4));
		assertFalse(station1.reachable(station5));
	}
}
