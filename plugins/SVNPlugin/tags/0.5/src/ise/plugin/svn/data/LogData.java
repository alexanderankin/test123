package ise.plugin.svn.data;

import org.tmatesoft.svn.core.wc.SVNRevision;

public class LogData extends CheckoutData {

    private SVNRevision startRevision = SVNRevision.create( 0L );
    private SVNRevision endRevision = SVNRevision.HEAD;
    private int maxLogs = 100;
    private boolean stopOnCopy = true;
    private boolean showPaths = false;

    /**
     * Returns the value of stopOnCopy.
     */
    public boolean getStopOnCopy() {
        return stopOnCopy;
    }

    /**
     * Sets the value of stopOnCopy.
     * @param stopOnCopy The value to assign stopOnCopy.
     */
    public void setStopOnCopy( boolean stopOnCopy ) {
        this.stopOnCopy = stopOnCopy;
    }

    /**
     * Returns the value of showPaths.
     */
    public boolean getShowPaths() {
        return showPaths;
    }

    /**
     * Sets the value of showPaths.
     * @param showPaths The value to assign showPaths.
     */
    public void setShowPaths( boolean showPaths ) {
        this.showPaths = showPaths;
    }


    public String toString() {
        return "LogData[startRevision=" + startRevision + ", endRevision=" + endRevision + "]";
    }

    public void setMaxLogs( int n ) {
        maxLogs = Math.max( 0, n );
    }

    public int getMaxLogs() {
        return maxLogs;
    }

    /**
     * Returns the value of startRevision.
     */
    public SVNRevision getStartRevision() {
        return startRevision;
    }

    /**
     * Sets the value of startRevision.
     * @param startRevision The value to assign startRevision.
     */
    public void setStartRevision( SVNRevision startRevision ) {
        this.startRevision = startRevision;
    }

    /**
     * Returns the value of endRevision.
     */
    public SVNRevision getEndRevision() {
        return endRevision;
    }

    /**
     * Sets the value of endRevision.
     * @param endRevision The value to assign endRevision.
     */
    public void setEndRevision( SVNRevision endRevision ) {
        this.endRevision = endRevision;
    }

}
