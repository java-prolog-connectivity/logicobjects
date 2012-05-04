package org.reflectiveutils.reflections4eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.reflections.vfs.Vfs.File;


public class BundleUrlFile implements File {

    private final BundleDir dir;
    private final String name;
    private final URL url;

    public BundleUrlFile(BundleDir dir, URL url) {
        this.dir = dir;
        this.url = url;
        String path = url.getFile();
        this.name = path.substring(path.lastIndexOf("/") + 1);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRelativePath() {
        return getFullPath().substring(dir.getPath().length());
    }

    //@Override
    public String getFullPath() {
        return url.getFile();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return url.openStream();
    }
}
