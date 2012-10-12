package org.logicobjects.context;

import java.util.Set;

import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.reflectiveutils.PackagePropertiesTree;
import org.reflectiveutils.ReflectionUtil;

public class UserLContext extends SimpleLContext {

	private Set<Class<? extends LogicEngineConfiguration>> engineConfigurations;
	private PackagePropertiesTree packagePropertiesTree;
	
	@Override
	protected void refresh() {
		super.refresh();
		
		engineConfigurations = filterAbstractClasses(reflections.getSubTypesOf(LogicEngineConfiguration.class));
		packagePropertiesTree = new PackagePropertiesTree();
		for(Class<? extends LogicEngineConfiguration> clazz : engineConfigurations) {
			if(!ReflectionUtil.isAbstract(clazz)) {
				try {
					LogicEngineConfiguration logicEngineConfiguration = clazz.newInstance();
					if(logicEngineConfiguration.isEnabled()) {
						
						for(String packageName : logicEngineConfiguration.getScope()) {
							packagePropertiesTree.addProperty(packageName, LogicEngineConfiguration.class, logicEngineConfiguration, false);
						}
					}
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public LogicEngineConfiguration getLogicEngineConfiguration(Class clazz) {
		return (LogicEngineConfiguration) packagePropertiesTree.findPackageProperty(clazz, LogicEngineConfiguration.class);
	}


	
}
