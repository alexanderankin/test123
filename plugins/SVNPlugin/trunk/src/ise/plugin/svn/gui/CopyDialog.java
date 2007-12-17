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

// imports
import java.io.File;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.*;
import org.gjt.sp.jedit.browser.VFSBrowser;

import projectviewer.ProjectViewer;

import ise.java.awt.KappaLayout;
import ise.plugin.svn.PVHelper;
import ise.plugin.svn.pv.SVNAction;
import ise.plugin.svn.data.*;
import ise.plugin.svn.command.*;

public class CopyDialog extends JDialog {
    // instance fields
    private View view = null;
    private List<File> toCopy = null;
    private JTextField path = null;

    private boolean cancelled = false;

    public CopyDialog( View view, List<File> files ) {
        super( ( JFrame ) view, "Copy", true );
        if (files == null || files.size() == 0) {
            throw new IllegalArgumentException("no source file(s) to copy");
        }
        this.view = view;
        this.toCopy = files;
        init();
    }

    protected void init() {
        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // local source file name
        JLabel to_copy_label = new JLabel();
        if (toCopy.size() == 1) {
            to_copy_label.setText("Copy file: " + toCopy.get(0).getAbsolutePath());
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><b>Copy these files:</b><br>");
            for (File file : toCopy) {
                sb.append(file.getAbsolutePath()).append("<br>");
            }
            to_copy_label.setText(sb.toString());
        }

        // local destination directory
        JLabel path_label = new JLabel( "Copy to:" );
        path = new JTextField( PVHelper.getProjectRoot(view), 30 );
        JButton browse_btn = new JButton( "Browse" );
        browse_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        String[] dirs = GUIUtilities.showVFSFileDialog( view, PVHelper.getProjectRoot(view), toCopy.size() == 1 ? VFSBrowser.OPEN_DIALOG : VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false );
                        if (dirs != null && dirs.length > 0) {
                            String filename = dirs[0];
                            File f = new File(filename);
                            if (toCopy.size() == 1 && f.exists()) {
                                int overwrite = JOptionPane.showConfirmDialog(view, "File exists, okay to overwrite?", "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                if ( overwrite == JOptionPane.NO_OPTION) {
                                    return;
                                }
                            }
                            path.setText(f.getAbsolutePath());
                        }
                    }
                }
                                    );

        // buttons
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( "Ok" );
        JButton cancel_btn = new JButton( "Cancel" );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        if ( path == null || path.getText().length() == 0 ) {
                            JOptionPane.showMessageDialog( CopyDialog.this, "Directory is required.", "Error", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        cancelled = false;
                        CopyDialog.this.setVisible( false );
                        CopyDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        cancelled = true;
                        CopyDialog.this.setVisible( false );
                        CopyDialog.this.dispose();
                    }
                }
                                    );


        // add the components to the option panel
        panel.add( "0, 0, 8, 1, W,  , 3", to_copy_label);
        panel.add( "0, 1, 1, 1, W,  , 3", path_label );
        panel.add( "1, 1, 6, 1, 0, w, 3", path );
        panel.add( "7, 1, 1, 1, 0, w, 3", browse_btn );

        panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 10, true ) );
        panel.add( "0, 5, 8, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    public CopyData getData() {
        if ( cancelled ) {
            return null;
        }
        CopyData cd = new CopyData();
        cd.setSourceFiles(toCopy);
        cd.setDestinationFile(new File(path.getText()));
        return cd;
    }
}
