package org.logicobjects.util;

import java.io.IOException;
import java.net.URL;

import javassist.ClassPool;
import javassist.LoaderClassPath;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicObjectFactory;
import org.reflections.util.ClasspathHelper;
import org.reflections.vfs.Vfs;
import org.reflectiveutils.reflections4eclipse.BundleUrlType;


public abstract class EclipsePluginConfigurator {

	//prepares an Eclipse plugin to be used with LOGICOBJECTS
	public static void configure(Plugin plugin) {
		String logicFilesPath = null;
		URL urlPlugin = ClasspathHelper.forClass(plugin.getClass());
		LogicObjectFactory.getDefault().addSearchUrl(urlPlugin);  //adding all the classes in the plugin to the search path
		try {
			logicFilesPath = FileLocator.toFileURL(urlPlugin).getPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		LogicEngine.getBootstrapEngine().cd(logicFilesPath);
		
		Vfs.addDefaultURLTypes(new BundleUrlType(plugin.getBundle()));  //enabling the Reflections filters to work in Eclipse
		//enabling javassist to work correctly in Eclipse
		ClassPool classPool;
		classPool = ClassPool.getDefault();
		classPool.appendClassPath(new LoaderClassPath(plugin.getClass().getClassLoader()));
		LogicObjectFactory.getDefault().setClassPool(classPool);
	}
}
