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

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.jEdit;

public class BlamePane extends JComponent implements CaretListener, EBComponent {

    private static final String uiClassID = "BlamePaneUI";
    private Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
    private BlameModel model = null;

    public BlamePane() {
        updateUI();
        EditBus.addToBus( this );
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

    public JPanel getCloser( final View view ) {
        final JPanel panel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 16, 0 ) );
        JButton button = new JButton( jEdit.getProperty("ips.Close_blame", "Close blame"), GUIUtilities.loadIcon( "10x10/actions/close.png" ) );
        button.setBorder( null );
        panel.add( button );
        button.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    getUI().uninstallUI( BlamePane.this );
                    view.getEditPane().getTextArea().removeTopComponent( panel );
                    view.getEditPane().getTextArea().removeLeftOfScrollBar( BlamePane.this );
                    model.getTextArea().removeCaretListener( BlamePane.this );
                    view.invalidate();
                    view.validate();
                }
            }
        );
        return panel;
    }

    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof EditPaneUpdate ) {
            EditPaneUpdate epu = ( EditPaneUpdate ) msg;
            if ( EditPaneUpdate.BUFFER_CHANGING.equals( epu.getWhat() ) ) {
                // remove the blame components from the text area
                JEditTextArea textArea = epu.getEditPane().getTextArea();
                JEditBuffer buffer = textArea.getBuffer();
                Object old_blame = buffer.getProperty( "_old_blame_" );
                if ( old_blame != null ) {
                    textArea.removeLeftOfScrollBar( ( JComponent ) old_blame );
                }
                Object old_closer = buffer.getProperty( "_old_closer_" );
                if ( old_closer != null ) {
                    textArea.removeTopComponent( ( JComponent ) old_closer );
                }
                epu.getEditPane().getView().invalidate();
                epu.getEditPane().getView().validate();

            }
            else if ( EditPaneUpdate.BUFFER_CHANGED.equals( epu.getWhat() ) ) {
                // check if the new buffer has stored blame parts, if so, add
                // them to the text area
                JEditTextArea textArea = epu.getEditPane().getTextArea();
                JEditBuffer buffer = textArea.getBuffer();
                Object old_blame = buffer.getProperty( "_old_blame_" );
                Object old_closer = buffer.getProperty( "_old_closer_" );
                if ( old_blame != null && old_closer != null ) {
                    textArea.addLeftOfScrollBar( ( JComponent ) old_blame );
                    textArea.addTopComponent( ( JComponent ) old_closer );
                    epu.getEditPane().getView().invalidate();
                    epu.getEditPane().getView().validate();
                }
            }
        }
    }
}