package ise.plugin.svn.data;

import java.io.*;
import java.util.*;
import ise.plugin.svn.io.ConsolePrintStream;

public class CommitData extends SVNData {
    private boolean keepLocks = true;
    private String commitMessage = "";
    private boolean recursive = true;

    public CommitData(){}

    public CommitData( ConsolePrintStream out,
            ConsolePrintStream err,
            List<String> paths,
            boolean keepLocks,
            String commitMessage,
            boolean recursive,
            String username,
            String password ) {
        super(out, err, paths, username, password);
        this.keepLocks = keepLocks;
        this.commitMessage = commitMessage;
        this.recursive = recursive;
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

}
