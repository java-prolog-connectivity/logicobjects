package org.logicobjects.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LogtalkResource extends LogicResource {

	private static final List<String> defaultFileExtensions = Arrays.asList(new String[] {"lgt"});
	
	public static List<String> getFileExtensions() {
		return defaultFileExtensions;
	}
	
	public static List<LogicResource> asLogtalkResources(List<String> names) {
		List<LogicResource> resources = new ArrayList<>();
		for(String name : names)
			resources.add(new LogtalkResource(name));
		return resources;
	}
	
	public LogtalkResource(String name) {
		super(name);
	}

}
