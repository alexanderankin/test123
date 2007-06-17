package ise.plugin.svn.data;

import java.io.*;
import java.util.*;
import ise.plugin.svn.io.ConsolePrintStream;
import org.tmatesoft.svn.core.SVNCommitInfo;


public class CommitData extends SVNData {
    private boolean keepLocks = true;
    private String commitMessage = "";
    private SVNCommitInfo info = null;

    public CommitData(){}

    public CommitData( ConsolePrintStream out,
            ConsolePrintStream err,
            List<String> paths,
            boolean keepLocks,
            String commitMessage,
            boolean recursive,
            String username,
            String password ) {
        super(out, err, paths, username, password, recursive);
        this.keepLocks = keepLocks;
        this.commitMessage = commitMessage;
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

    public SVNCommitInfo getInfo() {
        return info;
    }

    public void setInfo(SVNCommitInfo info) {
        this.info = info;
    }

}
