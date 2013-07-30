package org.logicobjects.test;

import org.jpc.JpcPreferences;
import org.junit.Test;

public class TestEnvironment {
	
	@Test
	public void testLogtalkEnvVars() {
		new JpcPreferences().getVarOrThrow(JpcPreferences.LOGTALK_HOME_ENV_VAR);
		new JpcPreferences().getVarOrThrow(JpcPreferences.LOGTALK_HOME_ENV_VAR);
	}


	

	
}
