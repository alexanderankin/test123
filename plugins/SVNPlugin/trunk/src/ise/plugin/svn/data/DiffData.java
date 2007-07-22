package ise.plugin.svn.data;

import org.tmatesoft.svn.core.wc.SVNRevision;


public class DiffData extends SVNData {

    private SVNRevision revision1 = SVNRevision.HEAD;
    private SVNRevision revision2 = null;


    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setRevision1( SVNRevision revision ) {
        this.revision1 = revision;
    }

    public SVNRevision getRevision1() {
        return revision1;
    }

    public void setRevision2( SVNRevision revision ) {
        this.revision2 = revision;
    }

    public SVNRevision getRevision2() {
        return revision2;
    }

}
