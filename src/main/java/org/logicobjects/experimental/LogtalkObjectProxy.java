package org.logicobjects.experimental;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.core.LogtalkObject;
import org.logicobjects.experimental.LogtalkObjectProxy;


public class LogtalkObjectProxy implements InvocationHandler {

	private Object obj;

	public static Object newInstance2(Object obj) {
		return java.lang.reflect.Proxy.newProxyInstance(obj.getClass()
				.getClassLoader(), obj.getClass().getInterfaces(),
				new LogtalkObjectProxy(obj));
	}
	
	public static <T> T newInstance(Class<T> c, LogtalkObject logtalkObject) {
		return (T)java.lang.reflect.Proxy.newProxyInstance(c.getClassLoader(), new Class[] {c},
				new LogtalkObjectProxy(logtalkObject));
	}

	private LogtalkObjectProxy(Object obj) {
		this.obj = obj;
	}

	public Object invoke(Object proxy, Method m, Object[] args)
			throws Throwable {
		Object result;
		LMethod logicMessageAnnotation = m.getAnnotation(LMethod.class);
		String logicMessage = logicMessageAnnotation.name();
		System.out.println(logicMessage);
		Method proxyMethod = obj.getClass().getMethod("invokeMethod", String.class, Object[].class);
		System.out.println(proxyMethod.toString());
		
		try {
			/*
			System.out.print("begin method " + m.getName() + "(");
			for (int i = 0; i < args.length; i++) {
				if (i > 0)
					System.out.print(",");
				System.out.print(" " + args[i].toString());
			}
			System.out.println(" )");
			//result = m.invoke(obj, args);
			 * 
			 */
			Object[] logicMessageArguments = args != null? args : new Object[] {};
			result = proxyMethod.invoke(obj, new Object[] {logicMessage, logicMessageArguments});
		} catch (Exception e) {
			/*
			throw new RuntimeException("unexpected invocation exception: "
					+ e.getMessage());
			*/
			throw new RuntimeException(e);
		} finally {
			//System.out.println("end method " + m.getName());
		}
		return result;
	}
/*
	public static void main(String[] args) {
		//Class[] interfaces = IntensionalViewLibrary.class.getInterfaces();
		//System.out.println(interfaces.length);
		IntensionalViewLibrary iv = (IntensionalViewLibrary)newInstance(IntensionalViewLibrary.class);
		iv.testAll();
	}
*/
}
