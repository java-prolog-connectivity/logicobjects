package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.core.LogicObject;
import org.logicobjects.core.LogicObjectFactory;
import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

/**
 * This class help to guide the transformation to or from a term, given the context of this transformation
 * For example, it this necessary because the term is going to be assigned to a field ? or the term is the result of a method invocation ?
 * @author scastro
 *
 */
public abstract class AdaptationContext {


}
