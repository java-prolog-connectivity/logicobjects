package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.logicobjects.term.Term;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.GenericsUtil;

public class TermToCollectionAdapter extends TermToObjectAdapter<Collection> {

	public Collection adapt(Term listTerm) {
		return adapt(listTerm, ImplementationMap.getDefault().implementationFor(Collection.class));
	}
	
	
	public Collection adapt(Term listTerm, Type type, AdaptationContext adaptingContext) {
		Collection collection;
		collection = (Collection) ImplementationMap.getDefault().instantiateObject(type);
		//Collection collection = new ArrayList();
		fillCollection(listTerm, type, adaptingContext, collection);
		return collection;
	}
	
	public Collection fillCollection(Term listTerm, Type type, AdaptationContext adaptingContext, Collection collection) {
		//SingleTypeWrapper typeWrapper = (SingleTypeWrapper) AbstractTypeWrapper.wrap(type);
		/*
		 * We ask for the parameterized types of Iterable instead of Collection, because Iterable is more general.
		 * However, we need a Collection instance in order to be able to fill in its elements
		 * (there are not "add" methods in an Iterable)
		 */
		List<Term> terms = LogicUtil.listToTerms(listTerm);
		if(terms.size() > 0) {
			Type[] collectionTypeParameters = new GenericsUtil().findAncestorTypeParameters(Iterable.class, type);
			//Type[] typeParameters = typeWrapper.getParameters();
			for(Term termItem : terms) {
				collection.add(new TermToObjectAdapter().adapt(termItem, collectionTypeParameters[0], adaptingContext));
			}
		}
		return collection;
	}
	
	
}
