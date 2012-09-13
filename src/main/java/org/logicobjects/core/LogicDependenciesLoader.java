package org.logicobjects.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jpl.Term;

import org.logicobjects.adapter.LogicResourcePathAdapter;
import org.logicobjects.resource.LogicResource;
import org.logicobjects.resource.LogtalkResource;
import org.logicobjects.resource.PrologResource;
import org.logicobjects.util.LogicUtil;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogicDependenciesLoader {
	private static Logger logger = LoggerFactory.getLogger(LogicDependenciesLoader.class);
	
	
	private Map<Class, List<LogicResource>> loadedClasses;
	private Map<Package, List<LogicResource>> loadedPackages;
	
	
	/**
	 * Package access so it cannot be instantiated externally
	 */
	public LogicDependenciesLoader() {
		loadedClasses = new HashMap<>();
		loadedPackages = new HashMap<>();
	}
	
	public boolean isClassLoaded(Class clazz) {
		return loadedClasses.containsKey(clazz);
	}
	
	public boolean isPackageLoaded(Package pakkage) {
		return loadedPackages.containsKey(pakkage);
	}
	
	private void rememberLoadedClass(Class clazz, List<LogicResource> logicResources) {
		loadedClasses.put(clazz, logicResources);
	}
	
	private void rememberLoadedPackage(Package pakkage, List<LogicResource> logicResources) {
		loadedPackages.put(pakkage, logicResources);
	}
	
	
	public boolean loadDependencies(Class clazz) {
		boolean result = true;
		if(!isClassLoaded(clazz)) {
			List<LogicObjectClass> logicObjectClasses = LogicObjectClass.findAllLogicObjectClasses(clazz);
			Collections.reverse(logicObjectClasses); //load top down
			for(LogicObjectClass logicObjectClass : logicObjectClasses) {
				if(!loadDependencies(logicObjectClass))
					result = false;
			}
		} 
		return result;
	}
	
	
	public boolean loadDependencies(LogicObjectClass logicObjectClass) {
		if(isClassLoaded(logicObjectClass.getWrappedClass()))
			return true;
		Package pakkage = logicObjectClass.getWrappedClass().getPackage();
		boolean packageLoaded = true;
		if(!isPackageLoaded(pakkage)) {
			URL url = ClasspathHelper.forClass(logicObjectClass.getWrappedClass());
			packageLoaded = simpleLoadPackage(pakkage, url);
		}
		return packageLoaded && simpleLoadClass(logicObjectClass);
	}
	
	
	public boolean simpleLoadPackage(Package pakkage, URL url) {
		boolean prologResult;
		boolean logtalkResult;
		
		LogicResourcePathAdapter resourceAdapter = new LogicResourcePathAdapter(url);
		
		//LOADING PROLOG MODULES
		String[] packageModules = getPackageModules(pakkage); //modules defined in the configuration file
		
		if(packageModules == null) { //TODO delete this ?
			packageModules = new String[] {};
		}
		List<String> allModulesNames = new ArrayList<>();
		allModulesNames.addAll(Arrays.asList(packageModules));
		List<LogicResource> allModules = PrologResource.asPrologResources(allModulesNames);

		List<Term> moduleTerms = new ArrayList<Term>();
		resourceAdapter.adapt(allModules, moduleTerms);
		
		prologResult = LogicEngine.getDefault().ensureLoaded(moduleTerms); //loading prolog modules
		if(!prologResult)
			logger.warn("Impossible to load prolog files from package: " + pakkage.getName() + ". List of resources: " + allModules);
		
		
		//LOADING LOGTALK OBJECTS
		String[] packageImports = getPackageImports(pakkage);
		
		if(packageImports == null) { //TODO delete this ?
			packageImports = new String[] {};
		}
		
		List<String> allImportsNames = new ArrayList<>();
		allImportsNames.addAll(Arrays.asList(packageImports));
		List<LogicResource> allImports = LogtalkResource.asLogtalkResources(allImportsNames);

		List<Term> importTerms = new ArrayList<Term>();
		resourceAdapter.adapt(allImports, importTerms);
		
		logtalkResult = LogicEngine.getDefault().logtalkLoad(importTerms); //loading Logtalk objects
		
		if(!logtalkResult)
			logger.warn("Impossible to load Logtalk files from package: " + pakkage.getName() + ". List of resources: " + allImports);
		
		
		List<LogicResource> allResources = new ArrayList<>();
		allResources.addAll(allModules);
		allResources.addAll(allImports);
		rememberLoadedPackage(pakkage, allResources);
		
		return prologResult && logtalkResult;
	}
	
	
	
	public boolean simpleLoadClass(LogicObjectClass logicObjectClass) {
		boolean prologResult;
		boolean logtalkResult;
		
		LogicResourcePathAdapter resourceAdapter = new LogicResourcePathAdapter(ClasspathHelper.forClass(logicObjectClass.getWrappedClass()));
		
		//LOADING PROLOG MODULES
		String[] descriptorModules = logicObjectClass.getLogicObjectDescriptor().modules(); //modules defined in the descriptor (e.g., with the LObject annotation)
		List<String> allModulesNames = new ArrayList<>();
		allModulesNames.addAll(Arrays.asList(descriptorModules)); 
		
		if(logicObjectClass.getLogicObjectDescriptor().automaticImport()) {
			String[] defaultModules = getDefaultPrologResources(logicObjectClass);  //modules defined by convention (Prolog files in the same package than the class and with the same name + Prolog extension)
			allModulesNames.addAll(Arrays.asList(defaultModules));
		}
		
		List<LogicResource> allModules = PrologResource.asPrologResources(allModulesNames);

		List<Term> moduleTerms = new ArrayList<Term>();
		resourceAdapter.adapt(allModules, moduleTerms);
		
		prologResult = LogicEngine.getDefault().ensureLoaded(moduleTerms); //loading prolog modules
		if(!prologResult)
			logger.warn("Impossible to load Prolog files from class: " + logicObjectClass.getSimpleName() + ". List of resources: " + allModules);
		
		//LOADING LOGTALK OBJECTS
		String[] descriptorImports = logicObjectClass.getLogicObjectDescriptor().imports();
		
		List<String> allImportsNames = new ArrayList<>();
		allImportsNames.addAll(Arrays.asList(descriptorImports));

		if(logicObjectClass.getLogicObjectDescriptor().automaticImport()) {
			String[] defaultImports = getDefaultLogtalkResources(logicObjectClass);   //Logtalk files defined by convention (in the same package than the class and with the same name + Logtalk extension)
			allImportsNames.addAll(Arrays.asList(defaultImports));
		}
		
		List<LogicResource> allImports = LogtalkResource.asLogtalkResources(allImportsNames);
		
		List<Term> importTerms = new ArrayList<Term>();
		resourceAdapter.adapt(allImports, importTerms);
		
		
		logtalkResult = LogicEngine.getDefault().logtalkLoad(importTerms); //loading Logtalk objects
		if(!logtalkResult)
			logger.warn("Impossible to load Logtalk files from class: " + logicObjectClass.getSimpleName() + ". List of resources: " + allImports);
		
		List<LogicResource> allResources = new ArrayList<>();
		allResources.addAll(allModules);
		allResources.addAll(allImports);
		rememberLoadedClass(logicObjectClass.getWrappedClass(), allResources);
		
		return prologResult && logtalkResult;
	}
	

	

	
	

	
	
	/**
	 * Load the default dependencies of a class
	 * These dependencies are based on the class name only, it does not assume that the class is annotated with LObject or LDelegationObject (e.g., it just implements the ITermObject interface or it is annotated with the LTermAdapter adapter)
	 * @param clazz
	 */
	/*
	public static void loadDefaultDependencies(LogicObjectClass logicObjectClass) {
		LogicResourcePathAdapter resourceAdapter = new LogicResourcePathAdapter(ClasspathHelper.forClass(logicObjectClass.getWrappedClass()));
		String[] defaultImports = getClassDefaultImports(logicObjectClass);
		List<Term> importTerms = new ArrayList<Term>();
		resourceAdapter.adapt(Arrays.asList(defaultImports), importTerms);
		LogicEngine.getDefault().logtalkLoad(importTerms);
	}
	*/
	
	
	
	
	
	private static final String IMPORTS = "imports"; //"objects" in logicobjects files will be loaded with logtalk_load
	private static final String MODULES = "modules"; //"modules" in logicobjects files will be loaded with ensure_loaded
	public static final String BUNDLE_NAME = "logicobjects";

	
	
	
	public String[] getPackageImports(Package pakkage) {
		return getBundleProperty(pakkage, IMPORTS);
	}
	
	public String[] getPackageModules(Package pakkage) {
		return getBundleProperty(pakkage, MODULES);
	}
	
	
	private String[] getBundleProperty(Package pakkage, String propertyName) {
		ResourceBundle bundle = getBundle(pakkage);
		if(bundle == null)
			return null;
		if(bundle.containsKey(propertyName)) {
			String stringProperties = bundle.getString(propertyName);
			String[] properties = stringProperties.split(",");
			return properties;
		} else
			return new String[] {};
	}
	
	public ResourceBundle getBundle(Package pakkage) {
		try {
			return ResourceBundle.getBundle(pakkage.getName() + "."+BUNDLE_NAME);
		} catch(MissingResourceException e) {
			return null;
		}
	}
	

	
	
	
	public String[] getDefaultPrologResources(LogicObjectClass logicObjectClass) {
		List<String> resources = new ArrayList<String>();
		for(String ext : PrologResource.getFileExtensions()) {
			getDefaultResources(logicObjectClass, ext, resources);
		}
		return resources.toArray(new String[] {});
	}
	
	public String[] getDefaultLogtalkResources(LogicObjectClass logicObjectClass) {
		List<String> resources = new ArrayList<String>();
		for(String ext : LogtalkResource.getFileExtensions()) {
			getDefaultResources(logicObjectClass, ext, resources);
		}
		return resources.toArray(new String[] {});
	}
	
	public String[] getDefaultResources(LogicObjectClass logicObjectClass, String fileExtension, List<String> resources) {
		addIfResourceExists(logicObjectClass.getSimpleName(), logicObjectClass, fileExtension, resources);
		String prologName = LogicUtil.javaClassNameToProlog(logicObjectClass.getSimpleName());
		if(!prologName.toUpperCase().equals(logicObjectClass.getSimpleName().toUpperCase()))
			addIfResourceExists(prologName, logicObjectClass, fileExtension, resources);
		String descriptorName = logicObjectClass.getLogicObjectDescriptor().name();
		if(descriptorName != null && !descriptorName.isEmpty()) {
			if(!descriptorName.toUpperCase().equals(logicObjectClass.getSimpleName().toUpperCase()) && !descriptorName.toUpperCase().equals(prologName))
			addIfResourceExists(descriptorName, logicObjectClass, fileExtension, resources);
		}
		return resources.toArray(new String[] {});
	}
	
	
	private boolean addIfResourceExists(String fileName, LogicObjectClass logicObjectClass, String fileExtension, List<String> resources) {
		if(fileName == null || fileName.equals(""))
			return false;
		/**
		 * the getResource method will append before the resource name the path of the class
		 * note that this method is not case sensitive: fileName will match any file with the same name without taking into consideration its case
		 */
		String fileWithExtension = fileName+"."+fileExtension;
		URL url = logicObjectClass.getResource(fileWithExtension);
		if(url != null) { 
			String packageName = logicObjectClass.getPackage().getName();
			String resourceName = packageName.replaceAll("\\.", "/");
			resourceName += "/" + fileWithExtension;
			resources.add(LogicResource.normalizeFileName(resourceName));
			//String urlFileName = url.getFile();
	//		System.out.println("**************************************************************");
	//		System.out.println(urlFileName);
	//		System.out.println("**************************************************************");
			//destiny.add(packageName+"."+fileName);
			//destiny.add(normalizeFileName(urlFileName));
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	
	

}
