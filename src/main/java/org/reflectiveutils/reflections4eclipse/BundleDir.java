package org.reflectiveutils.reflections4eclipse;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.Vfs.Dir;
import org.reflections.vfs.Vfs.File;

import com.google.common.collect.AbstractIterator;

public class BundleDir implements Dir {

    private String path;
    private final Bundle bundle;
    private JarFile jarFile;
    //private JarURLConnection jarURLConnection;
    private String fullPath;
    /*
    private static String urlPath(Bundle bundle, URL url) {
    	try {
			URL resolvedURL = FileLocator.resolve(url);
			String resolvedURLAsfile = resolvedURL.getFile();

			URL bundleRootURL = bundle.getEntry("/");
			URL resolvedBundleRootURL = FileLocator.resolve(bundleRootURL);
			String resolvedBundleRootURLAsfile = resolvedBundleRootURL.getFile();
			
			
			
			String path = "/"+resolvedURLAsfile.substring(resolvedURLAsfile.indexOf(resolvedBundleRootURLAsfile)+resolvedBundleRootURLAsfile.length());
			return path;
			//return("/"+resolvedURLAsfile.substring(resolvedBundleRootURLAsfile.length()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    */
    
    public BundleDir(Bundle bundle, URL url) {
        //this(bundle, url.getPath());
    	//this(bundle, urlPath(bundle,url));
    	this.bundle = bundle;
    	
    	try {
			URL resolvedURL = FileLocator.resolve(url);
			String resolvedURLAsfile = resolvedURL.getFile();
			
			
			fullPath = suppressFileProtocol(resolvedURLAsfile);
			
			/*
			System.out.println("bundleFile: "+FileLocator.getBundleFile(bundle));
			System.out.println("resolvedURL: "+resolvedURL);
			System.out.println("resolvedURL.getProtocol(): "+resolvedURL.getProtocol());
			System.out.println("resolvedURLAsfile: "+resolvedURLAsfile);
			System.out.println("fullPath: "+fullPath);
			*/
			
			
			
			
			/*
			URL bundleRootURL = bundle.getEntry("/");
			URL resolvedBundleRootURL = FileLocator.resolve(bundleRootURL);
			String resolvedBundleRootURLAsfile = resolvedBundleRootURL.getFile();
			
			//path = "/"+fullPath.substring(resolvedBundleRootURLAsfile.length());

			path = "/"+resolvedURLAsfile.substring(resolvedURLAsfile.indexOf(resolvedBundleRootURLAsfile)+resolvedBundleRootURLAsfile.length());
			System.out.println("path: "+path);
			*/
			
			
			
			
			if(!resolvedURL.getProtocol().equals("jar")) {
				URL bundleRootURL = bundle.getEntry("/");
				URL resolvedBundleRootURL = FileLocator.resolve(bundleRootURL);
				String resolvedBundleRootURLAsfile = resolvedBundleRootURL.getFile();
				
				path = "/"+fullPath.substring(resolvedBundleRootURLAsfile.length());
				

				//path = "/"+resolvedURLAsfile.substring(resolvedURLAsfile.indexOf(resolvedBundleRootURLAsfile)+resolvedBundleRootURLAsfile.length());
			} else {
				//jarFile = (JarFile) resolvedURL.getContent();
				jarFile = JarURLConnection.class.cast(resolvedURL.openConnection()).getJarFile();
				path = "/";
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    public JarFile getJarFile() {
    	return jarFile;
    }
    
    public String getFullPath() {
    	return fullPath;
    }
    
    /*
    public BundleDir(Bundle bundle, String p) {
        this.bundle = bundle;
        this.path = suppressBundleProtocol(p);
    }
*/
    private String suppressFileProtocol(String path) {
    	String fileProtocol = "file";
    	if (path.startsWith(fileProtocol + ":")) { 
            path = path.substring((fileProtocol + ":").length()); 
        }
    	return path;
    }
    
    private String suppressBundleProtocol(String path) {
    	if (path.startsWith(BundleUrlType.BUNDLE_PROTOCOL + ":")) { 
            path = path.substring((BundleUrlType.BUNDLE_PROTOCOL + ":").length()); 
        }
    	return path;
    }
    
    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Iterable<File> getFiles() {
    	if(jarFile != null) {
    		return new Iterable<Vfs.File>() {
				@Override
				public Iterator<File> iterator() {
					return new AbstractIterator<Vfs.File>() {
						Enumeration<JarEntry> entries = jarFile.entries();
						
						@Override
						protected File computeNext() {
							return entries.hasMoreElements() ? new BundleZipEntryFile(BundleDir.this, entries.nextElement()) : endOfData();
						}
						
					};
				}
    		};
    	} else {
    		return new Iterable<Vfs.File>() {
    			
    			@Override
                public Iterator<Vfs.File> iterator() {
                    return new AbstractIterator<Vfs.File>() {
                        Enumeration<URL> entries = bundle.findEntries(path, "*.class", true);
                        
                        @Override
                        protected Vfs.File computeNext() {
                            return entries.hasMoreElements() ? new BundleUrlFile(BundleDir.this, entries.nextElement()) : endOfData();
                        }
                    };
                }
            };
    	}
        
    }

    @Override
    public void close() {
    	if(jarFile != null) {
    		try {
				jarFile.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
    	}
			
    }
}

