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
 * The base class of all adaptation contexts.
 * Provides general information about the context in which an adaptation occurs (is it a logic term to a Java element, xml, etc...)
 * @author scastro
 *
 */
public abstract class AdaptationContext {


}
