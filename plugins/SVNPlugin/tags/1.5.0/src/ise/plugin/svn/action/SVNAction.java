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

package ise.plugin.svn.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import ise.plugin.svn.PVHelper;
import ise.plugin.svn.gui.LoginDialog;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.PasswordHandler;

public abstract class SVNAction implements ActionListener {

    private View view;
    private String username = null;
    private String password = null;
    private String actionName = "Subversion Command";

    private boolean canceled = false;

    public SVNAction( View view, String actionName ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        this.view = view;
        if ( actionName != null ) {
            this.actionName = actionName;
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean b) {
        canceled = b;
    }

    /**
     * Returns the value of actionName.
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * Sets the value of actionName.
     * @param actionName The value to assign actionName.
     */
    public void setActionName( String actionName ) {
        this.actionName = actionName;
    }


    /**
     * Returns the value of view.
     */
    protected View getView() {
        return view;
    }

    /**
     * Sets the value of view.
     * @param view The value to assign view.
     */
    protected void setView( View view ) {
        this.view = view;
    }

    /**
     * Returns the value of username.
     */
    protected String getUsername() {
        return username == null || username.length() == 0 ? null : username;
    }

    /**
     * Sets the value of username.
     * @param username The value to assign username.
     */
    protected void setUsername( String username ) {
        this.username = username;
    }

    /**
     * Returns the value of encrypted password.
     */
    protected String getPassword() {
        return password == null || password.length() == 0 ? null : password;
    }

    /**
     * Returns the value of decrypted password.
     */
    protected String getDecryptedPassword() {
        return PasswordHandler.decryptPassword(password);
    }

    /**
     * Sets the value of encrypted password.
     * @param password The value to assign password.
     */
    protected void setPassword( String password ) {
        this.password = password;
    }


    public abstract void actionPerformed( ActionEvent ae );

    protected void verifyLogin() {
        verifyLogin( null );
    }

    protected void verifyLogin( String filename ) {
        verifyLogin( filename, null );
    }

    protected void verifyLogin( String filename, String message ) {
        if ( message == null ) {
            message = jEdit.getProperty("ips.Confirm_SVN_login>", "Confirm SVN login:");
        }
        String uname = getUsername();
        String pwd = getPassword();     // encrypted password

        // no username, so assume no password.  Attempt to get username and
        // password from project the file belongs to
        if ( uname == null || uname.length() == 0 || pwd == null || pwd.length() == 0 ) {
            String[] login = PVHelper.getSVNLogin( filename );
            uname = login[ 0 ];
            pwd = login[ 1 ];           // encrypted password from PVHelper
        }

        // still no username, so ask the user for it.
        if ( uname == null || uname.length() == 0 || pwd == null || pwd.length() == 0 ) {
            LoginDialog ld = new LoginDialog( view, getActionName(), message );
            GUIUtils.center( view, ld );
            ld.setVisible( true );
            if ( ld.getCanceled() == true ) {
                setCanceled(true);
            }
            uname = ld.getUsername();
            pwd = ld.getPassword();     // encrypted password from login dialog
        }

        setUsername( uname );
        setPassword( pwd );             // encrypted password
    }
}