package org.logicobjects.util;

import java.util.List;

import org.reflections.scanners.SubTypesScanner;

public class LogicObjectsSubTypesScanner extends SubTypesScanner {
/*
	   @SuppressWarnings({"unchecked"})
	    public void scan(final Object cls) {
			String className = getMetadataAdapter().getClassName(cls);
			String superclass = getMetadataAdapter().getSuperclassName(cls);

	        //if (acceptResult(superclass)) {
	            getStore().put(superclass, className);
	        //}

			for (String anInterface : (List<String>) getMetadataAdapter().getInterfacesNames(cls)) {
				//if (acceptResult(anInterface)) {
	                getStore().put(anInterface, className);
	            //}
	        }
	    }
*/
}
