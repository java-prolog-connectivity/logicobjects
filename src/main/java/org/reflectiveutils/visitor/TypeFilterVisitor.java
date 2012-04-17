package org.reflectiveutils.visitor;

import java.util.ArrayList;
import java.util.List;

import org.reflectiveutils.visitor.TypeVisitor.InterfaceMode;

public abstract class TypeFilterVisitor extends TypeVisitor {
	protected List<Class> filteredTypes;
	
	public TypeFilterVisitor() {
		filteredTypes = new ArrayList<Class>();
	}
	
	public TypeFilterVisitor(InterfaceMode interfaceMode) {
		super(interfaceMode);
		filteredTypes = new ArrayList<Class>();
	}
	
	public List<Class> getFilteredTypes() {
		return filteredTypes;
	} 
	
	@Override
	public boolean doVisit(Class clazz) {
		if(match(clazz))
			filteredTypes.add(clazz);
		return true;
	}
	
	public abstract boolean match(Class clazz);
}
