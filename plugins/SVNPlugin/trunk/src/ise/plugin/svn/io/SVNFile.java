package ise.plugin.svn.io;

import java.io.File;
import java.net.URI;
import java.util.Properties;

/**
 * Extends java.io.File so that SVN properties can be attached.
 */
public class SVNFile extends File {

    private Properties properties = new Properties();

    public SVNFile(File parent, String child) {
        super(parent, child);
    }

    public SVNFile(String pathname) {
        super(pathname);
    }

    public SVNFile(String parent, String child) {
        super(parent, child);
    }

    public SVNFile(URI uri) {
        super(uri);
    }

    public void setProperty(String name, String value) {
        properties.setProperty(new String(name), new String(value));
    }

    public void setProperties(Properties props) {
        properties = (Properties)props.clone();
    }

    public String getProperty(String name) {
        String value = properties.getProperty(name);
        return value == null ? null : new String(value);
    }

    public Properties getProperties() {
        return (Properties)properties.clone();
    }
}
