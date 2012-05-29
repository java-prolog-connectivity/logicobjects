package org.logicobjects.lib;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.logicobjects.annotation.LObject;

@LObject(name="tuple", args = {"items"})
//@XmlRootElement(namespace = "iv4e.xml.jaxb.model")
//@XmlType(name = "tuple")
//@XmlTransient
public class Tuple<T> /*extends ArrayList<T>*/ {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private List<T> items;

	public Tuple() {
		this(new ArrayList<T>());
	}
	
	public Tuple(T ...members) {
		this(Arrays.asList(members));
	}
	
	public Tuple(List<T> list) {
		//super(list);
		setItems(list);
	}

	//@XmlElementWrapper(name = "tuple")
	//@XmlElement(name = "value")
	//@XmlValue
	@XmlTransient
	public List<T> getItems() {
		//return this;
		return items;
	}

	public T head() {
		return getItems().get(0);
	}
	
	public List<T> tail() {
		ArrayList<T> tail = new ArrayList<T>(getItems());
		tail.remove(0);
		return tail;
	}
	

	public void setItems(List<T> list) {
		this.items = list;
	}

	
/*
	public Tuple() {
		super(new ArrayList<T>());
	}

	
	public Tuple(List<T> list) {
		super(list);
	}
	

	public Tuple(T ...members) {
		this(Arrays.asList(members));
	}
*/
	

	
	
	
}
