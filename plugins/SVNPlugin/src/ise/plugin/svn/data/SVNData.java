package ise.plugin.svn.data;

import java.io.*;
import java.util.*;
import ise.plugin.svn.io.ConsolePrintStream;

/**
 * Base class to contain data to pass to the various subversion commands.
 */
public class SVNData {
    private ConsolePrintStream out;
    private ConsolePrintStream err;
    private List<String> paths;
    private String username = "";
    private String password = "";

    public SVNData(){}

    public SVNData( ConsolePrintStream out,
            ConsolePrintStream err,
            List<String> paths,
            String username,
            String password ) {
        this.out = out;
        this.err = err;
        this.paths = paths;
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the value of out.
     */
    public ConsolePrintStream getOut() {
        return out;
    }

    /**
     * Sets the value of out.
     * @param out The value to assign out.
     */
    public void setOut( ConsolePrintStream out ) {
        this.out = out;
    }

    /**
     * Returns the value of err.
     */
    public ConsolePrintStream getErr() {
        return err;
    }

    /**
     * Sets the value of err.
     * @param err The value to assign err.
     */
    public void setErr( ConsolePrintStream err ) {
        this.err = err;
    }

    /**
     * Returns the value of paths.
     */
    public List<String> getPaths() {
        return paths;
    }

    /**
     * Sets the value of paths.
     * @param paths The value to assign paths.
     */
    public void setPaths( List<String> paths ) {
        this.paths = paths;
    }

    /**
     * Returns the value of username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of username.
     * @param username The value to assign username.
     */
    public void setUsername( String username ) {
        this.username = username;
    }

    /**
     * Returns the value of password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of password.
     * @param password The value to assign password.
     */
    public void setPassword( String password ) {
        this.password = password;
    }



}
