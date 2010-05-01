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

import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.*;
import org.gjt.sp.jedit.gui.HistoryTextField;

import ise.java.awt.KappaLayout;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.pv.SVNAction;
import ise.plugin.svn.data.*;
import ise.plugin.svn.command.*;
import ise.plugin.svn.library.PasswordHandler;
import static ise.plugin.svn.gui.HistoryModelNames.*;

/**
 * Dialog for obtaining/editing the url and credentials to browse a repository.
 */
public class AddRepositoryDialog extends JDialog {

    private RepositoryData data = null;
    private HistoryTextField name = null;
    private HistoryTextField url = null;
    private HistoryTextField username = null;
    private JPasswordField password = null;

    private boolean canceled = false;

    public AddRepositoryDialog( View view ) {
        super( ( JFrame ) view, jEdit.getProperty("ips.Add_Repository_Location", "Add Repository Location"), true );
        _init();
    }

    public AddRepositoryDialog( View view, RepositoryData data ) {
        super( ( JFrame ) view, jEdit.getProperty("ips.Edit_Repository_Location", "Edit Repository Location"), true );
        this.data = data;
        _init();
    }

    /** Initialises the option pane. */
    protected void _init() {
        JPanel panel = new JPanel( new LambdaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // name field
        JLabel name_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "name.label" ) );
        String name_value = data != null && data.getName() != null ? data.getName() : "";
        name = new HistoryTextField( REPOSITORY_NAME );
        name.setName("repository name");
        name.setText(name_value);
        name.setColumns(30);

        // subversion repository url field
        JLabel url_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "url.label" ) );
        String url_value = data != null ? data.getURL() : "";
        url = new HistoryTextField(URL);
        url.setName("repository url");
        url.setText(url_value);
        url.setColumns(30);

        // username field
        JLabel username_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "username.label" ) );
        String username_value = data != null && data.getUsername() != null ? data.getUsername() : "";
        username = new HistoryTextField(USERNAME);
        username.setName("repository username");
        username.setText(username_value);
        username.setColumns(30);

        // password field
        JLabel password_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "password.label" ) );
        String password_value = data != null ? data.getDecryptedPassword() : "";
        password = new JPasswordField( password_value, 30 );
        password.setName("repository password");
        
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
                        if ( url == null || url.getText().length() == 0 ) {
                            JOptionPane.showMessageDialog( AddRepositoryDialog.this, jEdit.getProperty("ips.URL_is_required", "URL is required."), jEdit.getProperty("ips.Error", "Error"), JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        canceled = false;
                        AddRepositoryDialog.this.setVisible( false );
                        AddRepositoryDialog.this.dispose();
                        name.addCurrentToHistory();
                        url.addCurrentToHistory();
                        username.addCurrentToHistory();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        canceled = true;
                        AddRepositoryDialog.this.setVisible( false );
                        AddRepositoryDialog.this.dispose();
                    }
                }
                                    );


        // add the components to the option panel
        panel.add( "0, 0, 1, 1, E,  , 3", name_label );
        panel.add( "1, 0, 2, 1, 0, w, 3", name );

        panel.add( "0, 1, 1, 1, E,  , 3", url_label );
        panel.add( "1, 1, 2, 1, 0, w, 3", url );

        panel.add( "0, 2, 1, 1, E,  , 3", username_label );
        panel.add( "1, 2, 2, 1, 0, w, 3", username );

        panel.add( "0, 3, 1, 1, E,  , 3", password_label );
        panel.add( "1, 3, 2, 1, 0, w, 3", password );

        panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 10, true ) );
        panel.add( "0, 5, 3, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

        getRootPane().setDefaultButton(ok_btn);
        ok_btn.requestFocus();
    }

    public RepositoryData getValues() {
        if ( canceled ) {
            return null;
        }
        RepositoryData data = new RepositoryData();
        data.setName( name.getText() );
        data.setURL( url.getText() );
        data.setUsername( username.getText() );

        // encrypt the password if there is one
        String pwd = PasswordHandler.encryptPassword(new String( password.getPassword() ));
        data.setPassword( pwd );
        return data;
    }

}
