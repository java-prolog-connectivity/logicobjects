package org.logicobjects.lib.example.metro;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.LocalLogicTest;

public class TestMetro extends LocalLogicTest {

	@Test
	public void testStation() {
		//Metro metro = LogicObjectFactory.getDefault().create(Metro.class);
		Station station = LogicObjectFactory.getDefault().create(Station.class);
		System.out.println(station.getClass());
		station.setName("bond_street");
		
		try {
			Method m = station.getClass().getMethod("connected", String.class);
			System.out.println("Abstract: " + Modifier.isAbstract(m.getModifiers()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		
		
		
		//Line line = LogicObjectFactory.getDefault().create(Line.class);
		//line.setName("central");
		//Station connectedStation = station.connected(line);
		//Station connectedStation = 
		station.connected("central");
		//System.out.println(connectedStation);
	}
	
}
