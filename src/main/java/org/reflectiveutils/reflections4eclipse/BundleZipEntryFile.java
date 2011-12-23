package org.reflectiveutils.reflections4eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

import org.reflections.vfs.Vfs.File;

public class BundleZipEntryFile implements File {

    private final BundleDir dir;
    private final ZipEntry zipEntry;
    private final String name;
    private final String relativePath;
    
    public BundleZipEntryFile(BundleDir dir, ZipEntry zipEntry) {
        this.dir = dir;
        this.zipEntry = zipEntry;
        this.relativePath = zipEntry.getName();
        this.name = relativePath.substring(relativePath.lastIndexOf("/") + 1);
    }
    
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getRelativePath() {
		return relativePath;
	}

	@Override
	public String getFullPath() {
		return dir.getFullPath()+this.getRelativePath();
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return dir.getJarFile().getInputStream(zipEntry);
	}
	

}
