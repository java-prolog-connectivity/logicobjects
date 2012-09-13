package org.logicobjects.resource;

import java.util.ArrayList;
import java.util.List;


//TODO change to extends AbstractResource when that class is finished
public abstract class LogicResource { //implements ITermObject {

	private static String logicResourceExtensionRegex; //an OR regex expression with all the extensions of logic resources
	
	private static String findLogicResourceExtensionRegex() {
		StringBuilder sb = new StringBuilder();
		for(String ext : PrologResource.getFileExtensions()) {
			sb.append(ext + "|");
		}
		for(String ext : LogtalkResource.getFileExtensions()) {
			sb.append(ext + "|");
		}
		if(sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	public static String getLogicResourceExtensionRegex() {
		if(logicResourceExtensionRegex == null)
			logicResourceExtensionRegex = findLogicResourceExtensionRegex();
		return logicResourceExtensionRegex;
	}
	
	public static String normalizeFileName(String name) {
		return name.trim().replaceAll("\\.("+getLogicResourceExtensionRegex()+")", "");
	}
	
	
	public static List<String> normalizeFileNames(List<String> names) {
		List<String> fileNames = new ArrayList<>();
		for(String s : names) {
			fileNames.add(normalizeFileName(s));
		}
		return fileNames;
	}
	
	
	private String name;

	public LogicResource(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {return name;}
	
	/*
	public Term asTerm(URL url) {
		LogicResourcePathAdapter resourceAdapter = new LogicResourcePathAdapter(url);
		return resourceAdapter.adapt(allImports, importTerms);
	}
	*/
	
	public String normalizedFileName() {
		return normalizeFileName(name);
	}
}
