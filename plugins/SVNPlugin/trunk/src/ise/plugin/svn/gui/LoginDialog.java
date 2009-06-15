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
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import ise.java.awt.KappaLayout;
import ise.plugin.svn.PVHelper;

/**
 * This dialog does not encrypt or decrypt passwords.
 */
public class LoginDialog extends JDialog {

    private LoginPanel login = null;

    private boolean canceled = true;

    public LoginDialog( View view, String title, String message, String username, String password) {
        super( ( JFrame ) view, title, true );
        init( message, username, password );
    }

    public LoginDialog( View view, String title, String message, String filename ) {
        super( ( JFrame ) view, title, true );
        String[] login = PVHelper.getSVNLogin( filename );
        init( message, login[ 0 ], login[ 1 ] );
    }

    public LoginDialog( View view, String title, String message ) {
        super( ( JFrame ) view, title, true );
        init( message, "", "" );
    }

    /** Initialises the option pane. */
    protected void init( String msg, String username, String password ) {

        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        JLabel message = new JLabel(msg);

        login = new LoginPanel( username, password );

        // buttons
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( jEdit.getProperty("ips.Ok", "Ok") );
        ok_btn.setMnemonic(KeyEvent.VK_O);
        JButton cancel_btn = new JButton( jEdit.getProperty("ips.Cancel", "Cancel") );
        cancel_btn.setMnemonic(KeyEvent.VK_C);
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        canceled = false;
                        LoginDialog.this.setVisible( false );
                        LoginDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        canceled = true;
                        LoginDialog.this.setVisible( false );
                        LoginDialog.this.dispose();
                    }
                }
                                    );

        // add the components to the option panel
        panel.add( "0, 0, 1, 1, E, w, 3", message);
        panel.add( "0, 1, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 2, 1, 1, 0, w, 3", login );
        panel.add( "0, 3, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 4, 1, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

        getRootPane().setDefaultButton(ok_btn);
        ok_btn.requestFocus();
    }

    public boolean getCanceled() {
        return canceled;
    }

    public String getUsername() {
        return login.getUsername();
    }

    public String getPassword() {
        return login.getPassword();
    }
}
