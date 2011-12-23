package org.reflectiveutils.reflections4eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.Vfs.Dir;
import org.reflections.vfs.Vfs.File;
import org.reflections.vfs.Vfs.UrlType;
import org.slf4j.LoggerFactory;

import com.google.common.collect.AbstractIterator;

/*
 * DISCLAIMER: implementation of this class adapted from here:
 * http://stackoverflow.com/questions/8339845/reflections-library-not-working-when-used-in-an-eclipse-plug-in
 */
public class BundleUrlType implements UrlType {

	public static final String BUNDLE_PROTOCOL = "bundleresource";
	
	private final Bundle bundle;

    public BundleUrlType(Bundle bundle) {
    	
        this.bundle = bundle;
    }
    
	@Override
	public Dir createDir(URL url) {
		return new BundleDir(bundle, url);
	}

	@Override
	public boolean matches(URL url) {
		return BUNDLE_PROTOCOL.equals(url.getProtocol());
	}

	
		
}
