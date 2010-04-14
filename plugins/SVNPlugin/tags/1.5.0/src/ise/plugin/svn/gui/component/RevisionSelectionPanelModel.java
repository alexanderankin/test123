package ise.plugin.svn.gui.component;

import javax.swing.SwingConstants;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Tracks the state of the UI for the RevisionSelectionPanel.  This is the "M"
 * in MVC.
 */
public class RevisionSelectionPanelModel {

    private String title = "";
    private int direction = SwingConstants.VERTICAL;
    private boolean showHead = true;
    private boolean showWorking = false;
    private boolean showBase = true;
    private boolean showDate = true;
    private boolean showNumber = true;
    private SVNRevision defaultRevision = SVNRevision.HEAD;
    private SVNRevision revision = null;
    private boolean setEnabled = true;


    /**
     * Returns the value of setEnabled.
     */
    public boolean isEnabled() {
        return setEnabled;
    }

    /**
     * Sets the value of setEnabled.
     * @param setEnabled The value to assign setEnabled.
     */
    public void setEnabled( boolean setEnabled ) {
        this.setEnabled = setEnabled;
    }



    /**
     * Returns the value of title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of title.
     * @param title The value to assign title.
     */
    public void setTitle( String title ) {
        this.title = title;
    }

    /**
     * Returns the value of direction.
     */
    public int getDirection() {
        switch ( direction ) {
            case SwingConstants.HORIZONTAL:
            case SwingConstants.VERTICAL:
                break;
            default:
                direction = SwingConstants.VERTICAL;
        }
        return direction;
    }

    /**
     * Sets the value of direction.
     * @param direction The value to assign direction.
     */
    public void setDirection( int direction ) {
        this.direction = direction;
    }

    /**
     * Returns the value of showHead.
     */
    public boolean getShowHead() {
        return showHead;
    }

    /**
     * Sets the value of showHead.
     * @param showHead The value to assign showHead.
     */
    public void setShowHead( boolean showHead ) {
        this.showHead = showHead;
    }

    /**
     * Returns the value of showWorking.
     */
    public boolean getShowWorking() {
        return showWorking;
    }

    /**
     * Sets the value of showWorking.
     * @param showWorking The value to assign showWorking.
     */
    public void setShowWorking( boolean showWorking ) {
        this.showWorking = showWorking;
    }

    /**
     * Returns the value of showBase.
     */
    public boolean getShowBase() {
        return showBase;
    }

    /**
     * Sets the value of showBase.
     * @param showBase The value to assign showBase.
     */
    public void setShowBase( boolean showBase ) {
        this.showBase = showBase;
    }

    /**
     * Returns the value of showDate.
     */
    public boolean getShowDate() {
        return showDate;
    }

    /**
     * Sets the value of showDate.
     * @param showDate The value to assign showDate.
     */
    public void setShowDate( boolean showDate ) {
        this.showDate = showDate;
    }

    /**
     * Returns the value of showNumber.
     */
    public boolean getShowNumber() {
        return showNumber;
    }

    /**
     * Sets the value of showNumber.
     * @param showNumber The value to assign showNumber.
     */
    public void setShowNumber( boolean showNumber ) {
        this.showNumber = showNumber;
    }

    /**
     * Returns the value of defaultRevision.
     */
    public SVNRevision getDefaultRevision() {
        return defaultRevision;
    }

    /**
     * Sets the value of defaultRevision.
     * @param defaultRevision The value to assign defaultRevision.
     */
    public void setDefaultRevision( SVNRevision defaultRevision ) {
        this.defaultRevision = defaultRevision;
    }

    /**
     * Returns the value of revision.
     */
    public SVNRevision getRevision() {
        return revision == null ? defaultRevision : revision;
    }

    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setRevision( SVNRevision revision ) {
        this.revision = revision;
    }

}
