package ise.plugin.svn.data;

import org.tmatesoft.svn.core.wc.SVNRevision;


public class DiffData extends SVNData {

    private SVNRevision revision = SVNRevision.HEAD;


    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setRevision( SVNRevision revision ) {
        this.revision = revision;
    }

    public SVNRevision getRevision() {
        return revision;
    }



}
