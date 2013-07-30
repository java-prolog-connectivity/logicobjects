package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.jpc.converter.instantiation.InstantiationManager;
import org.jpc.term.AbstractTerm;
import org.jpc.term.Term;
import org.jpc.util.PrologUtil;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.minitoolbox.reflection.TypeUtil;

public class TermToCollectionAdapter extends TermToObjectAdapter<Collection> {

	public Collection adapt(AbstractTerm listTerm) {
		return adapt(listTerm, InstantiationManager.getDefault().implementationFor(Collection.class));
	}
	
	
	public Collection adapt(AbstractTerm listTerm, Type type, AdaptationContext adaptingContext) {
		Collection collection;
		collection = (Collection) InstantiationManager.getDefault().instantiate(type);
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
		List<AbstractTerm> terms = PrologUtil.listToTerms(listTerm);
		if(terms.size() > 0) {
			Type[] collectionTypeParameters = new TypeUtil().findAncestorTypeParameters(Iterable.class, type);
			//Type[] typeParameters = typeWrapper.getParameters();
			for(AbstractTerm termItem : terms) {
				collection.add(new TermToObjectAdapter().adapt(termItem, collectionTypeParameters[0], adaptingContext));
			}
		}
		return collection;
	}
	
	
}
