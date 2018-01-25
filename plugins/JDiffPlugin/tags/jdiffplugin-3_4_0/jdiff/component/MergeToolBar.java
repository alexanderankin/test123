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


package jdiff.component;


import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jdiff.DiffMessage;
import jdiff.component.ui.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.*;

/**
 * Component to show the merge controls.
 * Implements EBComponent to know about when the View splits and unsplits so
 * the buttons can be enabled or disabled accordingly.
 */
public class MergeToolBar extends JComponent implements EBComponent {

    private static final String uiClassID = "MergeToolBarUI";

    private View view = null;

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    public static final int COMPACT = 3;
    private int orientation = HORIZONTAL;   // TODO: this is set, but I don't think it's actually used anywhere.

    private Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();

    /**
     * @param view the parent frame
     */
    public MergeToolBar( View view ) {
        this.view = view;
        this.updateUI();
        EditBus.addToBus( this );
    }

    public void setUI( MergeToolBarUI ui ) {
        super.setUI( ui );
    }

    public void updateUI() {
        if ( UIManager.get( getUIClassID() ) != null ) {
            setUI( ( MergeToolBarUI ) UIManager.getUI( this ) );
        }
        else {
            setUI( new BasicMergeToolBarUI() );
        }
        fireStateChanged();
    }

    public MergeToolBarUI getUI() {
        return ( MergeToolBarUI ) ui;
    }

    public String getUIClassID() {
        return uiClassID;
    }

    public void removeNotify() {
        EditBus.removeFromBus( this );
    }

    public void addChangeListener( ChangeListener cl ) {
        if ( cl != null ) {
            changeListeners.add( cl );
        }
    }

    public void removeChangeListener( ChangeListener cl ) {
        if ( cl != null ) {
            changeListeners.remove( cl );
        }
    }

    public void fireStateChanged() {
        if ( changeListeners.size() > 0 ) {
            ChangeEvent event = new ChangeEvent( this );
            for ( ChangeListener cl : changeListeners ) {
                cl.stateChanged( event );
            }
        }
    }

    /**
     * @return parent frame
     */
    public View getView() {
        return view;
    }

    public void handleMessage( EBMessage message ) {
        if ( message instanceof EditPaneUpdate ) {
            EditPaneUpdate epu = ( EditPaneUpdate ) message;
            if (!view.equals(epu.getEditPane().getView())) {
                return;     // not my view   
            }
            if ( epu.getWhat() == EditPaneUpdate.DESTROYED ) {
                // this happens on an unsplit
                fireStateChanged();
            }
        }
        else if ( message instanceof DiffMessage ) {
            DiffMessage dm = ( DiffMessage ) message;
            if ( !view.equals( dm.getView() ) ) {
                return ;    // not my view
            }
            fireStateChanged();
        }
        else if ( message instanceof ViewUpdate ) {
            ViewUpdate vu = ( ViewUpdate ) message;
            if ( !view.equals( vu.getView() ) ) {
                return ;    // not my view
            }
            fireStateChanged();
        }
        else if ( message instanceof PropertiesChanged ) {
            int orient = jEdit.getIntegerProperty( "jdiff.toolbar-orientation", orientation );
            if ( orient != orientation ) {
                orientation = orient;
                fireStateChanged();
            }
        }
    }
}