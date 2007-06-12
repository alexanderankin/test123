package ise.plugin.svn.data;


import java.util.*;

/**
 * Data returned from an "add" command.
 */
public class AddResults {

    private List<String> paths = new ArrayList();
    private TreeMap<String, String> error_paths = new TreeMap();

    public void addPath(String path) {
        paths.add(path);
    }

    public List getPaths() {
        return paths;
    }

    public void addErrorPath(String path, String msg) {
        error_paths.put(path, msg);
    }

    public Map getErrorPaths() {
        return error_paths;
    }
}
