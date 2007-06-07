package ise.plugin.svn.command;

import java.io.*;
import java.util.*;

public class CommitData {
    private PrintStream out;
    private PrintStream err;
    private List<String> paths;
    private boolean keepLocks = true;
    private String commitMessage = "";
    private boolean recursive = true;
    private String username = "";
    private String password = "";

    public CommitData(){}

    public CommitData( PrintStream out,
            PrintStream err,
            List<String> paths,
            boolean keepLocks,
            String commitMessage,
            boolean recursive,
            String username,
            String password ) {
        this.out = out;
        this.err = err;
        this.paths = paths;
        this.keepLocks = keepLocks;
        this.commitMessage = commitMessage;
        this.recursive = recursive;
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the value of out.
     */
    public PrintStream getOut() {
        return out;
    }

    /**
     * Sets the value of out.
     * @param out The value to assign out.
     */
    public void setOut( PrintStream out ) {
        this.out = out;
    }

    /**
     * Returns the value of err.
     */
    public PrintStream getErr() {
        return err;
    }

    /**
     * Sets the value of err.
     * @param err The value to assign err.
     */
    public void setErr( PrintStream err ) {
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
     * Returns the value of keepLocks.
     */
    public boolean getKeepLocks() {
        return keepLocks;
    }

    /**
     * Sets the value of keepLocks.
     * @param keepLocks The value to assign keepLocks.
     */
    public void setKeepLocks( boolean keepLocks ) {
        this.keepLocks = keepLocks;
    }

    /**
     * Returns the value of commitMessage.
     */
    public String getCommitMessage() {
        return commitMessage;
    }

    /**
     * Sets the value of commitMessage.
     * @param commitMessage The value to assign commitMessage.
     */
    public void setCommitMessage( String commitMessage ) {
        this.commitMessage = commitMessage;
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
