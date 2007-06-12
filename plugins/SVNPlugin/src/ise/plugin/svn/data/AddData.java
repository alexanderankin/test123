package ise.plugin.svn.data;

import java.io.*;
import java.util.*;
import ise.plugin.svn.io.ConsolePrintStream;

/**
 * Data to pass to an "add" command.
 */
public class AddData extends SVNData {
    private boolean recursive = true;

    public AddData(){}

    public AddData( ConsolePrintStream out,
            ConsolePrintStream err,
            List<String> paths,
            boolean recursive,
            String username,
            String password ) {
        super(out, err, paths, username, password);
        this.recursive = recursive;
    }

    /**
     * Returns the value of recursive.
     */
    public boolean getRecursive() {
        return recursive;
    }

    /**
     * Sets the value of recursive.
     * @param recursive The value to assign recursive.
     */
    public void setRecursive( boolean recursive ) {
        this.recursive = recursive;
    }

}
