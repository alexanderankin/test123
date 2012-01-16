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

import java.awt.Color;
import java.awt.Font;

import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jdiff.DualDiff;
import jdiff.component.ui.*;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.*;

/**
 * Component to show the merge controls and line differences.
 */
public class DiffLineOverview extends JComponent implements LineProcessor, EBComponent {

    private static final String uiClassID = "DiffLineOverviewUI";

    private DualDiff dualDiff = null;
    private View view = null;
    private DiffLineModel diffLineModel = null;

    private Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();

    /**
     * @param dualDiff the DualDiff this component is to control.
     */
    public DiffLineOverview( DualDiff dualDiff, View view ) {
        this.dualDiff = dualDiff;
        this.view = view;
        this.updateUI();
        EditBus.addToBus(this);
    }

    public DiffLineOverview(View view) {
        this.view = view;
        this.updateUI();
        EditBus.addToBus(this);
    }

    /**
     * Perform a diff on 2 lines.
     * TODO: is this actually used?
     */
    public void processLines( String leftLine, String rightLine ) {
        if ( leftLine == null || rightLine == null ) {
            return ;
        }
        setModel( new DiffLineModel( leftLine, rightLine ) );
    }

    public void setUI( DiffLineOverviewUI ui ) {
        super.setUI( ui );
    }

    public void updateUI() {
        if ( UIManager.get( getUIClassID() ) != null ) {
            setUI( ( DiffLineOverviewUI ) UIManager.getUI( this ) );
        }
        else {
            setUI( new BasicDiffLineOverviewUI() );
        }
        fireStateChanged();
    }

    public DiffLineOverviewUI getUI() {
        return ( DiffLineOverviewUI ) ui;
    }

    public String getUIClassID() {
        return uiClassID;
    }

    public void addChangeListener(ChangeListener cl) {
        if (cl != null) {
            changeListeners.add(cl);
        }
    }

    public void removeChangeListener(ChangeListener cl) {
        if (cl != null) {
            changeListeners.remove(cl);
        }
    }

    public void fireStateChanged() {
        if (changeListeners.size() > 0) {
            ChangeEvent event = new ChangeEvent(this);
            for (ChangeListener cl : changeListeners) {
                cl.stateChanged(event);
            }
        }
    }

    public DualDiff getDualDiff() {
        return dualDiff;
    }

    /**
     * @return parent frame
     */
    public View getView() {
        return view;
    }

    public void setModel( DiffLineModel model ) {
        diffLineModel = model;
        fireStateChanged();
    }

    public DiffLineModel getModel() {
        return diffLineModel;
    }

    @Override
    public Color getBackground() {
        if (dualDiff != null) {
            return dualDiff.getBackground();
        }
        else {
            return view.getEditPane().getTextArea().getPainter().getBackground();
        }
    }

    @Override
    public Font getFont() {
        if (dualDiff != null) {
            return dualDiff.getFont();
        }
        else {
            return view.getEditPane().getTextArea().getPainter().getFont();
        }
    }

    public void clear() {
        setModel(null);
    }

    public void reset() {
        fireStateChanged();
    }

    public void handleMessage( EBMessage message ) {
        if (message instanceof PropertiesChanged ) {
            fireStateChanged();
        }
    }
}
