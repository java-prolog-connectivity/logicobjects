package org.logicobjects.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PrologResource extends LogicResource {
	

	private static final List<String> defaultFileExtensions = Arrays.asList(new String[] {"pl"});
	
	public static List<String> getFileExtensions() {
		return defaultFileExtensions;
	}
	
	public static List<LogicResource> asPrologResources(List<String> names) {
		List<LogicResource> resources = new ArrayList<>();
		for(String name : names)
			resources.add(new PrologResource(name));
		return resources;
	}
	
	public PrologResource(String name) {
		super(name);
	}

}
