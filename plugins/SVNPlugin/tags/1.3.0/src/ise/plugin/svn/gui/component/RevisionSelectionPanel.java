/*
* Copyright (c) 2008, Dale Anson
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/


package ise.plugin.svn.gui.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.*;

import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Panel to let the user choose a revision by pre-defined value (HEAD, etc), or
 * by date or by specific revision.  This is the "C" in MVC.
 */
public class RevisionSelectionPanel extends JComponent {

    private static final String uiClassID = "RevisionSelectionPanelUI";

    private View view = null;

    private Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
    private Set<PropertyChangeListener> propertyChangeListeners = new HashSet<PropertyChangeListener>();

    // data model
    private RevisionSelectionPanelModel model;

    /**
     * Set a title on vertical panel, do not show WORKING revision as a choice.
     * @param title title for the panel
     */
    public RevisionSelectionPanel( String title ) {
        this( title, SwingConstants.VERTICAL, false );
    }

    /**
     * Set a title on vertical panel, optionally show WORKING revision as a choice.
     * @param title title for the panel
     * @param direction either SwingConstants.HORIZONTAL or VERTICAL
     * @param showWorking if true, WORKING will be shown as a revision choice.
     */
    public RevisionSelectionPanel( String title, int direction, boolean showWorking ) {
        this( title, direction, true, true, true, true, showWorking );
    }

    /**
     * Fully specify which revision choices to show.
     */
    public RevisionSelectionPanel( String title, int direction, boolean showHead, boolean showBase, boolean showNumber, boolean showDate, boolean showWorking ) {
        model = new RevisionSelectionPanelModel();
        model.setTitle( title );
        model.setDirection( direction );
        model.setShowHead( showHead );
        model.setShowBase( showBase );
        model.setShowNumber( showNumber );
        model.setShowDate( showDate );
        model.setShowWorking( showWorking );
        updateUI();
    }

    public RevisionSelectionPanelModel getModel() {
        return model;
    }

    public void setModel( RevisionSelectionPanelModel model ) {
        this.model = model;
    }

    public void setUI( RevisionSelectionPanelUI ui ) {      // NOPMD
        super.setUI( ui );
    }

    /**
     * Install the UI.
     */
    public void updateUI() {
        if ( UIManager.get( getUIClassID() ) != null ) {
            setUI( ( RevisionSelectionPanelUI ) UIManager.getUI( this ) );
        }
        else {
            setUI( new BasicRevisionSelectionPanelUI() );
        }
        fireStateChanged();
    }

    public RevisionSelectionPanelUI getUI() {
        return ( RevisionSelectionPanelUI ) ui;
    }

    public String getUIClassID() {
        return uiClassID;
    }

    public void addChangeListener( ChangeListener cl ) {
        if ( cl != null ) {
            changeListeners.add( cl );
        }
    }

    public void addPropertyChangeListener( PropertyChangeListener cl ) {
        if ( cl != null ) {
            propertyChangeListeners.add( cl );
        }
    }

    public void removeChangeListener( ChangeListener cl ) {
        if ( cl != null ) {
            changeListeners.remove( cl );
        }
    }

    private void fireStateChanged() {
        if ( changeListeners.size() > 0 ) {
            ChangeEvent event = new ChangeEvent( this );
            for ( ChangeListener cl : changeListeners ) {
                cl.stateChanged( event );
            }
        }
    }

    private void firePropertyChanged( String name, Object oldValue, Object newValue ) {
        if ( propertyChangeListeners.size() > 0 ) {
            PropertyChangeEvent event = new PropertyChangeEvent( this, name, oldValue, newValue );
            for ( PropertyChangeListener cl : propertyChangeListeners ) {
                cl.propertyChange( event );
            }
        }
    }

    protected void firePropertyChanged(PropertyChangeEvent event) {
        if ( propertyChangeListeners.size() > 0 ) {
            for ( PropertyChangeListener cl : propertyChangeListeners ) {
                cl.propertyChange( event );
            }
        }
    }

    /**
     * @return parent frame
     */
    public View getView() {
        return view;
    }

    /**
     * Returns the value of setEnabled.
     */
    public boolean isEnabled() {
        return model.isEnabled();
    }

    /**
     * Sets the value of setEnabled.
     * @param setEnabled The value to assign setEnabled.
     */
    public void setEnabled( boolean setEnabled ) {
        model.setEnabled( setEnabled );
        fireStateChanged();
    }

    /**
     * Returns the value of title.
     */
    public String getTitle() {
        return model.getTitle();
    }

    /**
     * Sets the value of title.
     * @param title The value to assign title.
     */
    public void setTitle( String title ) {
        model.setTitle( title );
        fireStateChanged();
    }

    /**
     * Returns the value of direction.
     */
    public int getDirection() {
        return model.getDirection();
    }

    /**
     * Sets the value of direction.
     * @param direction The value to assign direction.
     */
    public void setDirection( int direction ) {
        model.setDirection( direction );
        fireStateChanged();
    }

    /**
     * Returns the value of showHead.
     */
    public boolean getShowHead() {
        return model.getShowHead();
    }

    /**
     * Sets the value of showHead.
     * @param showHead The value to assign showHead.
     */
    public void setShowHead( boolean showHead ) {
        model.setShowHead( showHead );
        fireStateChanged();
    }

    /**
     * Returns the value of showWorking.
     */
    public boolean getShowWorking() {
        return model.getShowWorking();
    }

    /**
     * Sets the value of showWorking.
     * @param showWorking The value to assign showWorking.
     */
    public void setShowWorking( boolean showWorking ) {
        model.setShowWorking( showWorking );
        fireStateChanged();
    }

    /**
     * Returns the value of showBase.
     */
    public boolean getShowBase() {
        return model.getShowBase();
    }

    /**
     * Sets the value of showBase.
     * @param showBase The value to assign showBase.
     */
    public void setShowBase( boolean showBase ) {
        model.setShowBase( showBase );
        fireStateChanged();
    }

    /**
     * Returns the value of showDate.
     */
    public boolean getShowDate() {
        return model.getShowDate();
    }

    /**
     * Sets the value of showDate.
     * @param showDate The value to assign showDate.
     */
    public void setShowDate( boolean showDate ) {
        model.setShowDate( showDate );
        fireStateChanged();
    }

    /**
     * Returns the value of showNumber.
     */
    public boolean getShowNumber() {
        return model.getShowNumber();
    }

    /**
     * Sets the value of showNumber.
     * @param showNumber The value to assign showNumber.
     */
    public void setShowNumber( boolean showNumber ) {
        model.setShowNumber( showNumber );
        fireStateChanged();
    }

    /**
     * Returns the value of defaultRevision.
     */
    public SVNRevision getDefaultRevision() {
        return model.getDefaultRevision();
    }

    /**
     * Sets the value of defaultRevision.
     * @param defaultRevision The value to assign defaultRevision.
     */
    public void setDefaultRevision( SVNRevision defaultRevision ) {
        SVNRevision old_value = model.getDefaultRevision();
        model.setDefaultRevision( defaultRevision );
        firePropertyChanged( "revision", old_value, defaultRevision );
    }

    /**
     * Returns the value of revision.
     */
    public SVNRevision getRevision() {
        return model.getRevision();
    }

    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setRevision( SVNRevision revision ) {
        SVNRevision old_value = model.getRevision();
        model.setRevision( revision );
        firePropertyChanged( "revision", old_value, revision );
    }
}
