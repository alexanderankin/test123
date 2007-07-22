package ise.plugin.svn.data;

import org.tmatesoft.svn.core.wc.SVNRevision;


public class DeleteData extends SVNData {

    private boolean deleteFiles = true;

    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setDeleteFiles( boolean b ) {
        deleteFiles = b;
    }

    public boolean getDeleteFiles() {
        return deleteFiles;
    }
}
