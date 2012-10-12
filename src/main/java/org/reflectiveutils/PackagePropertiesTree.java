package org.reflectiveutils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackagePropertiesTree {
	
	private PackagePropertiesTree parent;
	private Map<String, PackagePropertiesTree> children;
	private String packageFragment;
	Map<Object, Object> properties;
	
	/**
	 * Will create a root PackagePropertiesTree
	 */
	public PackagePropertiesTree() {
		this("", null);
	}
	
	public PackagePropertiesTree(String packageFragment, PackagePropertiesTree parent) {
		checkNotNull(packageFragment);
		checkArgument( (parent != null && !packageFragment.isEmpty()) || (parent == null && packageFragment.isEmpty()) );
		this.packageFragment = packageFragment;
		this.parent = parent;
		children = new HashMap<>();
		properties = new HashMap<>();
	}
	
	public String getPackageName() {
		String previous = parent==null?"":parent.getPackageName();
		if(!previous.isEmpty())
			previous+=".";
		return previous+packageFragment;
	}
	
	private List<String> asPackageFragmentsList(String packageName) {
		packageName = packageName.trim();
		List<String> packageFragmentsList;
		if(packageName.isEmpty())
			packageFragmentsList = new ArrayList<>();
		else {
			String[] packageFragments = packageName.split("\\.");
			packageFragmentsList = Arrays.<String>asList(packageFragments);
		}
		return packageFragmentsList;
	}
	
	public Object findPackageProperty(Class clazz, Object property) {
		return findProperty(clazz.getPackage(), property);
	}
	
	public Object findProperty(Package pakkage, Object property) {
		return findProperty(pakkage.getName(), property);
	}
	
	public Object findProperty(String packageName, Object property) {
		checkNotNull(packageName);
		checkNotNull(property);
		return findProperty(asPackageFragmentsList(packageName), property);
	}
	
	
	private Object findProperty(List<String> packageFragments, Object property) {
		Object propertyValue = properties.get(property); //get the local value of the property (if any)
		//if(hasMorePackageFragments(packageFragments)) {//find if the value is overridden
		if(!packageFragments.isEmpty()) {//find if the value is overridden
			PackagePropertiesTree child = children.get(packageFragments.get(0));
			if(child != null) {
				Object propertyValueRest = child.findProperty(packageFragments.subList(1, packageFragments.size()), property);
				if(propertyValueRest != null)
					propertyValue = propertyValueRest;
			}
		}
		return propertyValue;
	}
	
	public void addProperty(Package pakkage, Object property, Object propertyValue, boolean canOverride) {
		addProperty(pakkage.getName(), property, propertyValue, canOverride);
	}
	
	public void addProperty(String packageName, Object property, Object propertyValue, boolean canOverride) {
		checkNotNull(packageName);
		checkNotNull(property);
		addProperty(asPackageFragmentsList(packageName), property, propertyValue, canOverride);
	}
	
	private void addProperty(List<String> packageFragments, Object property, Object propertyValue, boolean canOverride) {
		//if(!hasMorePackageFragments(packageFragments))
		if(packageFragments.isEmpty()) {
			Object currentPropertyValue = properties.get(property);
			if(currentPropertyValue!=null && !canOverride)
				throw new RuntimeException("The package " + getPackageName() + " has already configured the property \"" + property + "\" as:" + currentPropertyValue +
					".Attempting to override this property with: " + propertyValue);
			else
				properties.put(property, propertyValue);
		}
		else {
			String head = packageFragments.get(0);
			PackagePropertiesTree child = children.get(head);
			if(child == null) {
				child = new PackagePropertiesTree(head, this);
				children.put(head, child);
			}
			child.addProperty(packageFragments.subList(1, packageFragments.size()), property, propertyValue, canOverride);
		}
	}
	
	/*
	private boolean hasMorePackageFragments(List<String> packageFragments) {
		if(packageFragments.size() == 1) {
			String head = packageFragments.get(0);
			return head.isEmpty();
		}
		return false;
	}
	*/

	

	

}
