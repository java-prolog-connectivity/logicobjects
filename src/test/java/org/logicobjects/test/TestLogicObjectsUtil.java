package org.logicobjects.test;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.util.LogicObjectsUtil;
import static org.junit.Assert.*;

public class TestLogicObjectsUtil extends LocalLogicTest {

	@Test
	public void testLogicObjectsUtil() {
		LogicObjectsUtil util = LogicObjectFactory.getDefault().create(LogicObjectsUtil.class);
		assertTrue(util.test());
	}
	
}
