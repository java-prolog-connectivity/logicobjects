package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

import jpl.Term;

import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptingContext;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

public class TermToCollectionAdapter extends TermToObjectAdapter<Collection> {

	public Collection adapt(Term listTerm) {
		return adapt(listTerm, ImplementationMap.getDefault().implementationFor(Collection.class));
	}
	
	
	public Collection adapt(Term listTerm, Type type, AdaptingContext adaptingContext) {
		Collection collection = (Collection) ImplementationMap.getDefault().instantiateObject(type);
		fillCollection(listTerm, type, adaptingContext, collection);
		return collection;
	}
	
	public Collection fillCollection(Term listTerm, Type type, AdaptingContext adaptingContext, Collection collection) {
		//SingleTypeWrapper typeWrapper = (SingleTypeWrapper) AbstractTypeWrapper.wrap(type);
		/*
		 * We ask for the parameterized types of Iterable instead of Collection, because Iterable is more general.
		 * However, we need a Collection instance in order to be able to fill in its elements
		 * (there are not "add" methods in an Iterable)
		 */
		Type[] collectionTypeParameters = new GenericsUtil().findAncestorTypeParameters(Iterable.class, type);
		//Type[] typeParameters = typeWrapper.getParameters();
		for(Term termItem : LogicUtil.listToTermArray(listTerm)) {
			collection.add(new TermToObjectAdapter().adapt(termItem, collectionTypeParameters[0], adaptingContext));
		}
		return collection;
	}
	
	
}
