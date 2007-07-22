package ise.plugin.svn.data;


import java.util.*;

/**
 * Data returned from an "add" command.
 */
public class AddResults {

    private List<String> paths = new ArrayList<String>();
    private TreeMap<String, String> error_paths = new TreeMap<String, String>();

    public void addPath(String path) {
        paths.add(path);
    }

    public List<String> getPaths() {
        return paths;
    }

    public void addErrorPath(String path, String msg) {
        error_paths.put(path, msg);
    }

    public Map<String, String> getErrorPaths() {
        return error_paths;
    }
}
