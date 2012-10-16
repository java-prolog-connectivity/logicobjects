package org.logicobjects.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

import org.logicobjects.LogicObjectsPreferences;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.base.Preconditions.*;
import com.google.common.base.Predicate;
import com.google.common.io.Resources;

public class ResourceManager {

	private static Logger logger = LoggerFactory.getLogger(ResourceManager.class);
	
	private final String tmpDirPath; // the root temporary directory 
	public static final String LOGIC_OBJECTS_TMP_FOLDER = LogicObjectsPreferences.LOGIC_OBJECTS_NAME;
	private final File logicObjectsTmpDir; //a File object representing a folder in the tmp directory where logic files or similar resources can be unzipped if required

	private Set<URL> processedURLs;
	
	
	public ResourceManager(String tmpDirPath) {
		checkNotNull(tmpDirPath);
		checkArgument(!tmpDirPath.isEmpty());
		this.tmpDirPath = tmpDirPath;
		processedURLs = new HashSet<URL>();
		logicObjectsTmpDir = new File(tmpDirPath, LOGIC_OBJECTS_TMP_FOLDER);
		logicObjectsTmpDir.mkdirs();
	}
	
	/**
	 * 
	 * @param url
	 * @return a boolean indicating if the url has been processed after this call
	 * if the URL was already processed before returns false
	 * If it is not possible to process the URL (e.g., it is needed to copy resources to a undefined tmp folder) an exception will be launched
	 */
	public boolean process(URL url) {
		if(hasAlreadyBeenProcessed(url))
			return false;
		//if(!isFileSystemDir(url)) { 
			try {
				/*
				 * This sould be done if the logic files are in a jar or directly located in an exploded directory in the file system
				 * Even if the files are directly in the file system it does not mean they are accessible from the current execution path
				 * Referring always to the tmp directory 
				 */
				createTmpLogicFiles(url); 
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		//}
		processedURLs.add(url);
		return true;
	}

	
	/**
	 * DISCLAIMER: this method and its comments have been adapted from an answer to this question: http://stackoverflow.com/questions/779519/delete-files-recursively-in-java
	 * @param path
	 * @throws IOException
	 */
	public static void deleteRecursively(Path path) throws IOException {
	    Files.walkFileTree(path, new SimpleFileVisitor<Path>()
	    {
	        @Override
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	            Files.delete(file);
	            return FileVisitResult.CONTINUE;
	        }

	        @Override
	        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
	            Files.delete(file); // try to delete the file anyway, even if its attributes could not be read, since delete-only access is theoretically possible
	            return FileVisitResult.CONTINUE;
	        }

	        @Override
	        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	            if (exc == null) {
	                Files.delete(dir);
	                return FileVisitResult.CONTINUE;
	            }
	            else { //something went wrong
	                throw exc; //propagate the exception
	            }
	        }
	    });
	}
	
	
	public File getTmpDir(URL url) {
		String tmpFolderPath = url.toExternalForm();
		tmpFolderPath = tmpFolderPath.replaceAll("[\\W]+", "_");
		tmpFolderPath = tmpFolderPath.replaceAll("[_]+$", "");
		return new File(logicObjectsTmpDir, tmpFolderPath);
	}
	
	
	public void createTmpLogicFiles(URL url) throws IOException {
		File tmpDirForUrl = getTmpDir(url);
		if(tmpDirForUrl.exists()) {
			logger.debug("Deleting previous tmp directory: " + tmpDirForUrl.getAbsolutePath());
			deleteRecursively(tmpDirForUrl.toPath());
		}
		
		logger.debug("Creating tmp directory: " + tmpDirForUrl.getAbsolutePath());
		tmpDirForUrl.mkdir();
		
		Predicate<String> predicate = new Predicate<String>() {
			  public boolean apply(String string) {
				return string.matches(".*\\.(lgt|pl)$");  //matching resources names ending in "lgt" or "pl" (the default Logtalk and Prolog extensions)
			  }
			};
			
		copyResources(url, predicate, tmpDirForUrl);
	}

	
	public static void copyResources(URL url, Predicate<String> predicate, File destination) {// throws IOException {
		if(predicate == null)
			predicate = new Predicate<String>() {
				  public boolean apply(String string) {
					  return true; //no filter, so all the resources will be copied
				  }
		};
		
		Reflections reflections = new Reflections(new ConfigurationBuilder()
		.setUrls(url)
        .setScanners(new ResourcesScanner()));
		
		/*
		 * WARNING: the getResources method answers resources RELATIVE paths (relatives to the classpath from where they were found)
		 * If a file is created with this path (like with: new File(relativePath)) the path of such File object will be the current execution path + the relative path
		 * If the current execution path is not the base directory of the relative paths, this could lead to files having absolute paths pointing to non existing resources
		 */
		Set<String> resourcePaths = reflections.getResources(predicate);  //in case a complex predicate is needed
		
		for(String resourcePath : resourcePaths) {
			logger.debug("Copying resource to tmp location: " + resourcePath);
			File fileToCreate = new File(destination, resourcePath);
			fileToCreate.getParentFile().mkdirs();
			
			try(FileOutputStream fos = new FileOutputStream(fileToCreate)) {
				URL urlResources = new URL(url, resourcePath);
				Resources.copy(urlResources, fos);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}



	/**
	 * 
	 * @param url
	 * @return a boolean indicating if a url points to a file directly located in the file system (not inside a jar for example)
	 */
	public boolean isFileSystemDir(URL url) {
		try {
			Path path = new File(url.toURI()).toPath();
			return Files.isDirectory(path);
		} catch (Exception e) {
			return false;
		}
		/*
		String protocol = url.getProtocol();
		if(protocol != null) {
			return protocol.matches("^file");
		}
		return false;
		*/
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	private String basePath(URL url) {
		/*
		if(isFileSystemDir(url)) 
			return url.getFile();
		else*/
			return getTmpDir(url).getAbsolutePath(); //always consider the base path the temp directory given the url sent as parameter
	}

	public String getResourcePath(String resource, URL url) {
		File baseDirectory = new File(basePath(url));
		File resourceFile = new File(baseDirectory, resource);
		return resourceFile.getAbsolutePath();
	}
	
	public boolean hasAlreadyBeenProcessed(URL url) {
		return processedURLs.contains(url);
	}
	


}
