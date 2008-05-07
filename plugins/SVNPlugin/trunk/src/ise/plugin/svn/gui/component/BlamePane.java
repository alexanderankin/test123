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

import java.util.*;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class BlamePane extends JComponent implements CaretListener {

    private static final String uiClassID = "BlamePaneUI";
    private Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
    private BlameModel model = null;

    public BlamePane() {
        updateUI();
    }

    public void setUI( BlamePaneUI ui ) {
        super.setUI( ui );
    }

    public void updateUI() {
        if ( UIManager.get( getUIClassID() ) != null ) {
            setUI( ( BlamePaneUI ) UIManager.getUI( this ) );
        }
        else {
            setUI( new BasicBlamePaneUI() );
        }
    }

    public BlamePaneUI getUI() {
        return ( BlamePaneUI ) ui;
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

    /**
     * Model is a list of strings, one per line in the source file.  Each string
     * in the model provides the blame information about that line, probably just
     * author and revision.  This list is provided by the i.p.s.command.Blame command.
     */
    public void setModel( BlameModel model ) {
        this.model = model;
        updateUI();
    }

    public BlameModel getModel() {
        return model;
    }

    public JPanel getCloser(final View view) {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton button = new JButton("Close Blame");
        panel.add(button);
        button.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    getUI().uninstallUI(BlamePane.this);
                    view.getEditPane().getTextArea().removeTopComponent(panel);
                    view.getEditPane().getTextArea().removeLeftOfScrollBar(BlamePane.this);
                    model.getTextArea().removeCaretListener(BlamePane.this);
                    view.invalidate();
                    view.validate();
                }
            }
        );
        return panel;
    }
}
