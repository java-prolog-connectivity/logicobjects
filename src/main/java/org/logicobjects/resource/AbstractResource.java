package org.logicobjects.resource;

import java.net.URL;

import org.logicobjects.core.ITermObject;

//TODO
public abstract class AbstractResource implements ITermObject {

	private String name;
	private Package pakkage;
	private URL baseUrl;
	
	//private ClassLoader classLoader;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		
		this.name = name;
	}
	
	/*
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public ClassLoader getClassLoader() {
		return classLoader;
	}
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	*/
	public Package getPackage() {
		return pakkage;
	}
	public void setPackage(Package pakkage) {
		this.pakkage = pakkage;
	}
	
	
	
}
