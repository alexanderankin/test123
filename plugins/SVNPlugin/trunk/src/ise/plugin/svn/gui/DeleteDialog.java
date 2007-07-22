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
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.View;

import ise.plugin.svn.data.DeleteData;
import ise.java.awt.KappaLayout;


public class DeleteDialog extends JDialog {

    private View view = null;
    private List<String> paths = null;
    private DeleteData data = null;

    /**
     * @param view the parent frame
     * @param path the local working file to diff against.
     */
    public DeleteDialog(View view, DeleteData data) {
        super( ( JFrame ) view, "Delete", true );
        if ( data == null ) {
            throw new IllegalArgumentException( "data may not be null" );
        }
        this.view = view;
        this.data = data;

        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        final JCheckBox force_cb = new JCheckBox("Force");
        force_cb.setSelected(data.getForce());
        final JCheckBox dry_run_cb = new JCheckBox("Dry run");
        dry_run_cb.setSelected(data.getDryRun());
        final JCheckBox delete_files_cb = new JCheckBox("Delete files from file system");
        delete_files_cb.setSelected(data.getDeleteFiles());

        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( "Ok" );
        JButton cancel_btn = new JButton( "Cancel" );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        getData().setForce(force_cb.isSelected());
                        getData().setDryRun(dry_run_cb.isSelected());
                        getData().setDeleteFiles(delete_files_cb.isSelected());
                        DeleteDialog.this.setVisible( false );
                        DeleteDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        DeleteDialog.this.data = null;
                        DeleteDialog.this.setVisible( false );
                        DeleteDialog.this.dispose();
                    }
                }
                                    );

        panel.add(force_cb, "0, 0, 1, 1, W, w, 3");
        panel.add(dry_run_cb, "0, 1, 1, 1, W, w, 3");
        panel.add(delete_files_cb, "0, 2, 1, 1, W, w, 3");
        panel.add(KappaLayout.createVerticalStrut(6), "0, 3, 1, 1");
        panel.add(btn_panel, "0, 4, 1, 1, E");
        setContentPane(panel);
        pack();
    }

    public DeleteData getData() {
        return data;
    }
}
