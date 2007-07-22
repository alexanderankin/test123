package ise.plugin.svn.data;

import java.util.List;
import java.util.ArrayList;
import org.tmatesoft.svn.core.wc.SVNStatus;

public class StatusData {
    private long revision;
    private List<SVNStatus> modified;
    private List<SVNStatus> conflicted;
    private List<SVNStatus> deleted;
    private List<SVNStatus> added;
    private List<SVNStatus> unversioned;
    private List<SVNStatus> missing;
    private List<SVNStatus> outOfDate;
    /**
     * Returns the value of revision.
     */
    public long getRevision()
    {
        return revision;
    }

    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setRevision(long revision)
    {
        this.revision = revision;
    }

    /**
     * Returns the value of modified.
     */
    public List<SVNStatus> getModified()
    {
        return modified;
    }

    /**
     * Sets the value of modified.
     * @param modified The value to assign modified.
     */
    public void setModified(List<SVNStatus> modified)
    {
        this.modified = modified;
    }

    /**
     * Returns the value of conflicted.
     */
    public List<SVNStatus> getConflicted()
    {
        return conflicted;
    }

    /**
     * Sets the value of conflicted.
     * @param conflicted The value to assign conflicted.
     */
    public void setConflicted(List<SVNStatus> conflicted)
    {
        this.conflicted = conflicted;
    }

    /**
     * Returns the value of deleted.
     */
    public List<SVNStatus> getDeleted()
    {
        return deleted;
    }

    /**
     * Sets the value of deleted.
     * @param deleted The value to assign deleted.
     */
    public void setDeleted(List<SVNStatus> deleted)
    {
        this.deleted = deleted;
    }

    /**
     * Returns the value of added.
     */
    public List<SVNStatus> getAdded()
    {
        return added;
    }

    /**
     * Sets the value of added.
     * @param added The value to assign added.
     */
    public void setAdded(List<SVNStatus> added)
    {
        this.added = added;
    }

    /**
     * Returns the value of unversioned.
     */
    public List<SVNStatus> getUnversioned()
    {
        return unversioned;
    }

    /**
     * Sets the value of unversioned.
     * @param unversioned The value to assign unversioned.
     */
    public void setUnversioned(List<SVNStatus> unversioned)
    {
        this.unversioned = unversioned;
    }

    /**
     * Returns the value of missing.
     */
    public List<SVNStatus> getMissing()
    {
        return missing;
    }

    /**
     * Sets the value of missing.
     * @param missing The value to assign missing.
     */
    public void setMissing(List<SVNStatus> missing)
    {
        this.missing = missing;
    }

    /**
     * Returns the value of outOfDate.
     */
    public List<SVNStatus> getOutOfDate()
    {
        return outOfDate;
    }

    /**
     * Sets the value of outOfDate.
     * @param outOfDate The value to assign outOfDate.
     */
    public void setOutOfDate(List<SVNStatus> outOfDate)
    {
        this.outOfDate = outOfDate;
    }


}
