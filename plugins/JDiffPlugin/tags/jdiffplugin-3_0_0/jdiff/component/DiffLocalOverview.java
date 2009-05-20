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

import java.util.*;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jdiff.component.ui.*;
import jdiff.DualDiff;

import org.gjt.sp.jedit.textarea.JEditTextArea;

public class DiffLocalOverview extends DiffOverview {
    private static final String uiClassID = "DiffLocalOverviewUI";
    private Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();

    public DiffLocalOverview(DualDiff dualDiff) {
        super(dualDiff);
        updateUI();
    }

    public void setUI( DiffLocalOverviewUI ui ) {
        super.setUI( ui );
    }

    public void updateUI() {
        if ( UIManager.get( getUIClassID() ) != null ) {
            setUI( ( DiffLocalOverviewUI ) UIManager.getUI( this ) );
        }
        else {
            setUI( new BasicDiffLocalOverviewUI() );
        }
    }

    public DiffLocalOverviewUI getUI() {
        return ( DiffLocalOverviewUI ) ui;
    }

    public String getUIClassID() {
        return uiClassID;
    }

    public void caretUpdate( final CaretEvent e ) {
        if ( e.getSource() instanceof JEditTextArea ) {
            fireStateChanged();
        }
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

    public void moveRight( int line_number ) {
        getUI().moveRight( line_number );
    }

    public void moveLeft( int line_number ) {
        getUI().moveLeft( line_number );
    }
}
