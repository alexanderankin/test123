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

import javax.swing.*;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.PVHelper;


public class LoginPanel extends JPanel {

    private JLabel username_label;
    private JTextField username;
    private JLabel password_label;
    private JPasswordField password;

    public LoginPanel( String filename ) {
        String[] login = PVHelper.getSVNLogin( filename );
        init( login[ 0 ], login[ 1 ] );
    }

    public LoginPanel( String username, String password ) {
        init( username, password );
    }


    /** Initialises the option pane. */
    protected void init( String u, String p ) {
        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "SVN Login" ) );
        setLayout( new LambdaLayout() );

        // possible username and password values
        u = u == null ? "" : u;
        p = u == null ? "" : p;

        // username field
        username_label = new JLabel( "Username:" );
        username = new JTextField( u, 30 );

        // password field
        password_label = new JLabel( "Password:" );
        password = new JPasswordField( p, 30 );

        add( "0, 0, 1, 1, W,  , 3", username_label );
        add( "1, 0, 6, 1, 0, w, 3", username );

        add( "0, 1, 1, 1, W,  , 3", password_label );
        add( "1, 1, 6, 1, 0, w, 3", password );
    }

    public String getUsername() {
        return username.getText();
    }

    public String getPassword() {
        return new String( password.getPassword() );
    }
}
