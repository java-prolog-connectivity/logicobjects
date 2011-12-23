package org.logicobjects.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;
import org.logicobjects.adapter.LogtalkResourcePathAdapter;
import org.logicobjects.adapter.objectadapters.ArrayToTermAdapter;

import org.logicobjects.annotation.LObject;


public class LogtalkObject implements ITermObject {
/*
	public static void configureAll() {
		LogicUtil.prepareEngine();  
		LogtalkUtil.loadLogtalk(); //load logtalk if needed, will do nothing is logtalk is already loaded
		IVUtil.loadAll();
	}
	*/
	
	public static final String LOGTALK_OPERATOR = "::";
	
	public static Compound logtalkMessage(Term object, String messageName, Term[] messageArguments) {
		Term rightTerm = messageArguments.length > 0 ? new Compound(messageName, messageArguments) : new Atom(messageName);
		return new Compound(LOGTALK_OPERATOR, new Term[] {object, rightTerm});
	}
	
	
	private String name;
	private Object[] objectParams;
	private Term[] termParams;
	//private Term asTerm;
	
	public LogtalkObject(String name) {
		this(name, new Object[]{});
	}
	
	public LogtalkObject(Term term) {
		this(term.name(), term.args());
	}
	
	public LogtalkObject(String name, Object[] objectParams) {
		setName(name);
		if(objectParams == null)
			objectParams = new Object[]{};
		setObjectParams(objectParams);
	}



	/*
	public LogtalkObject(Atom atom) {
		this(atom.toString());
	}
	
	public LogtalkObject(Compound compound) {
		this(compound.name(), compound.args());
	}
	*/
	


	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getObjectParams() {
		return objectParams;
	}

	public void setObjectParams(Object[] objectParams) {
		setTermParams(ArrayToTermAdapter.arrayAsTerms(objectParams));		
		this.objectParams = objectParams;
	}

	public Term[] getTermParams() {
		return termParams;
	}

	public void setTermParams(Term[] termParams) {
		this.termParams = termParams;
	}

	@Override
	public Term asTerm() {
		if( parametrizedObject() )
			return new Compound(getName(), getTermParams());
		else
			return new Atom(getName());
	}
	
	
	public boolean parametrizedObject() {
		return getTermParams().length > 0;
	}
	
	public Query invokeMethod(String methodName, Object[] messageArgs) {
		Compound compound = logtalkMessage(asTerm(), methodName, ArrayToTermAdapter.arrayAsTerms(messageArgs));
		Query query = new Query(compound);
		return query;
	}
	
	/*
	 * Returns the first class in the hierarchy annotated with LogicObject
	 */
	public static Class getAnnotatedClass(Class clazz) {
		if(clazz.equals(Object.class))
			return null;
		else if(clazz.getAnnotation(LObject.class) != null)
			return clazz;
		else
			return getAnnotatedClass(clazz.getSuperclass());
	}
	
	public static LObject getLogicObjectAnnotation(Class clazz) {
		Class annotatedClass = getAnnotatedClass(clazz);
		return (LObject)annotatedClass.getAnnotation(LObject.class);
	}
	
	/*
	 * Return a boolean indicating if all the modules and objects were loaded correctly
	 */
	public static boolean loadDependencies(Class clazz) {
		//if(true)return true; //TODO delete this
		LObject logicObject = getLogicObjectAnnotation(clazz);
		if(!logicObject.automaticImport())
			return false;
		
		boolean result = true;
		
		String[] annotationModules = logicObject.modules();
		String[] bundleModules = getBundleModules(clazz);
		if(bundleModules == null) {
			bundleModules = new String[] {};
		}
		
		Set<String> allModules = new LinkedHashSet<String>(); //Set is used to avoid duplicates. Using LinkedHashSet instead of HashSet (the faster), since the former will preserve the insertion order
		allModules.addAll(Arrays.asList(bundleModules));
		allModules.addAll(Arrays.asList(annotationModules));
		
		
		List<Term> moduleTerms = new ArrayList<Term>();
		new LogtalkResourcePathAdapter().adapt(allModules, moduleTerms);
		
		result = LogicEngine.getDefault().ensureLoaded(moduleTerms);
		
		String[] annotationImports = logicObject.imports();
		String[] bundleImports = getBundleImports(clazz);
		if(bundleImports == null) {
			bundleImports = new String[] {};
		}
		String[] defaultImports = getDefaultImports(clazz);
		
		Set<String> allImports = new LinkedHashSet<String>(); //Set is used to avoid duplicates.
		allImports.addAll(Arrays.asList(bundleImports));
		allImports.addAll(Arrays.asList(annotationImports));
		allImports.addAll(Arrays.asList(defaultImports));
		

		List<Term> importTerms = new ArrayList<Term>();
		new LogtalkResourcePathAdapter().adapt(allImports, importTerms);
		
		return LogicEngine.getDefault().logtalkLoad(importTerms) && result;
	}
	
	private static final String IMPORTS = "imports";
	private static final String MODULES = "modules";
	public static final String BUNDLE_NAME = "logicobjects";
	
	public static ResourceBundle getBundle(Class clazz) {
		try {
			return ResourceBundle.getBundle(clazz.getPackage().getName() + "."+BUNDLE_NAME);
		} catch(MissingResourceException e) {
			return null;
		}
	}
	
	public static String[] getBundleImports(Class clazz) {
		return getBundleProperty(clazz, IMPORTS);
	}
	
	public static String[] getBundleModules(Class clazz) {
		return getBundleProperty(clazz, MODULES);
	}
	
	public static String[] getBundleProperty(Class clazz, String propertyName) {
		ResourceBundle bundle = getBundle(clazz);
		if(bundle == null)
			return null;
		if(bundle.containsKey(propertyName)) {
			String stringProperties = bundle.getString(propertyName);
			String[] properties = stringProperties.split(",");
			return properties;
		} else
			return new String[] {};
	}
	
	public static String[] getAnnotationImports(Class clazz) {
		return ((LObject)clazz.getAnnotation(LObject.class)).imports();
	}
	
	public static String[] getAnnotationModules(Class clazz) {
		return ((LObject)clazz.getAnnotation(LObject.class)).modules();
	}
	
	private static boolean addIfLgtFileExists(String fileName, Class clazz, List<String> destiny) {
		if(fileName == null || fileName.equals(""))
			return false;
		String packageName = clazz.getPackage().getName();
		if(clazz.getResource(fileName+".lgt") != null) { //the getResource method will append before the path of the class
			destiny.add(packageName+"."+fileName);
			return true;
		}
		return false;
	}
	
	public static String[] getDefaultImports(Class clazz) {
		List<String> defaultImports = new ArrayList<String>();
		addIfLgtFileExists(clazz.getSimpleName(), clazz, defaultImports);
		
		LObject logicObject = getLogicObjectAnnotation(clazz);
		if(logicObject != null)
			addIfLgtFileExists(logicObject.name(), clazz, defaultImports);
		return defaultImports.toArray(new String[] {});
	}
	
/*
	public static void main(String[] args) {
		loadDependencies(IntensionalSet.class);
	}
	*/

	@Override
	public String toString() {
		return asTerm().toString();
	}
	

	
	/*
	 * This method returns the first class is the hierarchy annotated with the LObject annotation 
	 * If the class is marked only with the LObjectAdapter annotation it is NOT considered
	 */
	public static Class findLogicClass(Class candidateClass) {
		if(candidateClass.equals(Object.class))
			return null;
		if(candidateClass.getAnnotation(LObject.class) != null)
			return candidateClass;
		else {
			Class logicClass = null;
			if(!candidateClass.isInterface()) {
				logicClass = findLogicClass(candidateClass.getSuperclass());
			} 
			if(logicClass == null){
				for(Class interfaze : candidateClass.getInterfaces()) {
					logicClass = findLogicClass(interfaze);
					if(logicClass != null)
						break;
				}
			}
			return logicClass;
		}
			
	}
}

