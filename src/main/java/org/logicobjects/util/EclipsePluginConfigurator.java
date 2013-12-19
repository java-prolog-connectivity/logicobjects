package org.logicobjects.util;

import java.io.IOException;
import java.net.URL;

import javassist.ClassPool;
import javassist.LoaderClassPath;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.logicobjects.LogicObjects;
import org.minitoolbox.reflection.googlereflections.eclipse.BundleUrlType;
import org.reflections.util.ClasspathHelper;
import org.reflections.vfs.Vfs;


public abstract class EclipsePluginConfigurator {

	/**
	 * Prepares an Eclipse plugin to be used with LOGICOBJECTS
	 * @param plugin
	 */
	public static void configure(Plugin plugin) {
		//enabling javassist to work correctly in Eclipse
		ClassPool classPool = ClassPool.getDefault();
		classPool.appendClassPath(new LoaderClassPath(plugin.getClass().getClassLoader()));
		LogicObjects.getDefault().getContext().getLogicObjectFactory().setClassPool(classPool);
		
		Vfs.addDefaultURLTypes(new BundleUrlType(plugin.getBundle()));  //enabling the Reflections filters to work in Eclipse
		
		String pathLogicFiles = null;
		URL urlPlugin = ClasspathHelper.forClass(plugin.getClass());
		/**
		 * adding all the classes in the plugin to the search path
		 * This line has to be after the call to Vfs.addDefaultURLTypes(...)
		 */
		LogicObjects.getDefault().getContext().addSearchUrl(urlPlugin);
		try {
			pathLogicFiles = FileLocator.toFileURL(urlPlugin).getPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		/**
		 * move to the directory where the plugin is deployed
		 * This code uses the bootstrap engine since this engine will not trigger the loading of Logtalk
		 * After we have moved to the location of the plugin files we can load logtalk afterwards
		 */
		//LogicEngine.getBootstrapEngine().cd(pathLogicFiles); TODO verify that this is not needed anymore (it should work without this since all the logic files are copied to a tmp directory)
		

	}
}
