package org.logicobjects.lib.examples.metro;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LExpression;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.core.LogicObjectFactory;
import static org.logicobjects.LogicObjects.*;

@LObject
public abstract class MetroFactory implements IMetroFactory {

	public static MetroFactory getDefault() {
		return newLogicObject(MetroFactory.class);
	}

}
