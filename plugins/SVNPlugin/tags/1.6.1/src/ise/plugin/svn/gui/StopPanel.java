/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.gui;

import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.event.*;
import ise.plugin.svn.*;
import common.swingworker.SwingWorker;
import org.gjt.sp.jedit.GUIUtilities;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 * A panel for the SVN Console that shows a 'stop' button for each running
 * svn command.
 */
public class StopPanel extends JPanel {

    // list of workers
    private HashMap<SwingWorker, JButton> workers = new HashMap<SwingWorker, JButton>();

    private List<ChangeListener> listeners = null;

    public StopPanel() {
    }

    public void addWorker( String name, final SwingWorker worker ) {
        if ( worker == null ) {
            return ;
        }
        PropertyChangeListener listener = new PropertyChangeListener() {
                    public void propertyChange( PropertyChangeEvent event ) {
                        Object newValue = event.getNewValue();
                        if ( newValue != null && newValue.equals( SwingWorker.StateValue.DONE ) ) {
                            removeWorker( ( SwingWorker ) event.getSource() );
                            if ( workers.size() == 0 ) {
                                fireChangeEvent();
                            }
                        }
                    }
                };
        worker.addPropertyChangeListener( listener );
        JButton button = new JButton( name, GUIUtilities.loadIcon( "16x16/actions/process-stop.png"));//"Stop.png" ) );
        button.setToolTipText("Stop '" + name + "' action.");
        button.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    worker.cancel( true );
                    removeWorker( worker );
                    final JButton b = (JButton) ae.getSource();
                    SwingUtilities.invokeLater( new Runnable() {
                                public void run() {
                                    remove( b );
                                    invalidate();
                                    repaint();
                                }
                            }
                                              );
                }
            }
        );
        add( button );
        workers.put( worker, button );
    }

    private void removeWorker( final SwingWorker worker ) {
        SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        JButton button = workers.get( worker );
                        workers.remove( worker );
                        if (button != null) {
                            remove( button );
                        }
                        invalidate();
                        repaint();
                    }
                }
                                  );
    }

    public String getName() {
        return "Progress";
    }

    public int getWorkerCount() {
        return workers.size();
    }

    public void addChangeListener( ChangeListener listener ) {
        if ( listeners == null ) {
            listeners = new ArrayList<ChangeListener>();
        }
        listeners.add( listener );
    }

    private void fireChangeEvent() {
        for ( ChangeListener listener : listeners ) {
            listener.stateChanged( ( ChangeEvent ) null );
        }
    }
}