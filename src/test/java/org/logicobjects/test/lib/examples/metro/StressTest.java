package org.logicobjects.test.lib.examples.metro;

import org.junit.Test;
import org.logicobjects.lib.examples.metro.ILine;
import org.logicobjects.lib.examples.metro.IMetro;
import org.logicobjects.lib.examples.metro.IMetroFactory;
import org.logicobjects.lib.examples.metro.IStation;

public class StressTest extends AbstractMetroTest {

	public static int TIMES = 30000;
	public static long milliSeconds(long start, long end) {return (end - start)/1000000;}
	
	@Test
	public void testLineConnects_Station_Station() {
		ILine line1 = createLine("central");
		IStation station1 = createStation("bond_street");
		IStation station2 = createStation("oxford_circus");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			line1.connects(station1, station2);
		}
		long endTime = System.nanoTime();
		System.out.println("*** testLineConnects_Station_Station: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testLineSegments() {
		ILine line1 = createLine("central");
		long startTime = System.nanoTime();
		
		for(int i=0; i<TIMES;i++) {
			line1.segments();
		}
		long endTime = System.nanoTime();
		System.out.println("*** testLineSegments: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testStationConnected_Station() {
		IStation station1 = createStation("bond_street");
		IStation station2 = createStation("oxford_circus");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			station1.connected(station2);
		}
		long endTime = System.nanoTime();
		System.out.println("*** testStationConnected_Station: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testStationNumberConnections() {
		IStation station = createStation("bond_street");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			station.numberConnections();
		}
		long endTime = System.nanoTime();
		System.out.println("*** testStationNumberConnections: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testStationConnected_Line() {
		IStation station1 = createStation("bond_street");
		ILine line1 = createLine("central");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			station1.connected(line1);
		}
		long endTime = System.nanoTime();
		System.out.println("*** testStationConnected_Line: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testStationConnected() {
		IStation station1 = createStation("bond_street");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			station1.connected();
		}
		long endTime = System.nanoTime();
		System.out.println("*** testStationConnected: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testStationNearby_Station() {
		IStation station1 = createStation("bond_street");
		IStation station2 = createStation("oxford_circus");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			station1.nearby(station2);
		}
		long endTime = System.nanoTime();
		System.out.println("*** testStationNearby_Station: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testStationNumberNearbyStations() {
		IStation station1 = createStation("bond_street");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			station1.numberNearbyStations();
		}
		long endTime = System.nanoTime();
		System.out.println("*** testStationNumberNearbyStations: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testStationNearby() {
		IStation station1 = createStation("bond_street");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			station1.nearby();
		}
		long endTime = System.nanoTime();
		System.out.println("*** testStationNearby: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testStationReachable_Station() {
		IStation station1 = createStation("bond_street");
		IStation station2 = createStation("oxford_circus");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			station1.reachable(station2);
		}
		long endTime = System.nanoTime();
		System.out.println("*** testStationReachable_Station: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testStationNumberReachableStations() {
		IStation station1 = createStation("bond_street");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			station1.numberReachableStations();
		}
		long endTime = System.nanoTime();
		System.out.println("*** testStationNumberReachableStations: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testStationIntermediateStations_Station() {
		IStation station1 = createStation("bond_street");
		IStation station2 = createStation("oxford_circus");
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			station1.intermediateStations(station2);
		}
		long endTime = System.nanoTime();
		System.out.println("*** testStationIntermediateStations_Station: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testMetroLines() {
		IMetro metro = createMetro();
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			metro.lines();
		}
		long endTime = System.nanoTime();
		System.out.println("*** testMetroLines: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testMetroLine_String() {
		IMetro metro = createMetro();
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			metro.line("central");
		}
		long endTime = System.nanoTime();
		System.out.println("*** testMetroLine_String: "+ milliSeconds(startTime, endTime));
	}
	
	@Test
	public void testMetroFactoryLine() {
		IMetroFactory metroFactory = createMetroFactory();
		
		long startTime = System.nanoTime();
		for(int i=0; i<TIMES;i++) {
			metroFactory.line("central");
		}
		long endTime = System.nanoTime();
		System.out.println("*** testMetroFactoryLine: "+ milliSeconds(startTime, endTime));
	}
}
