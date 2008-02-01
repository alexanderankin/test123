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

package ise.plugin.svn.gui.br;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.gjt.sp.jedit.View;
import ise.plugin.svn.action.*;
import ise.plugin.svn.data.RepositoryData;


public class Checkout extends BRAction {

/**
 *     public Checkout( View view, String repositoryUrl, JTree tree, String username, String password ) {
 *         super( view, repositoryUrl, tree, username, password );
 *     }
 */

    public void actionPerformed( ActionEvent ae ) {
        TreePath[] paths = tree.getSelectionPaths();

        if ( paths.length == 0 ) {
            return ;
        }
        if ( paths.length > 1 ) {
            JOptionPane.showMessageDialog( view, "Please select a single entry.", "Too many selections", JOptionPane.ERROR_MESSAGE );
            return ;
        }
        String url = null;
        for ( TreePath path : paths ) {
            if ( path != null ) {
                Object[] parts = path.getPath();
                StringBuilder sb = new StringBuilder();
                sb.append( parts[ 0 ] );
                for ( int i = 1; i < parts.length; i++ ) {
                    sb.append( "/" ).append( parts[ i ].toString() );
                }
                url = sb.toString();
                break;
            }
        }
        RepositoryData data = new RepositoryData();
        data.setURL( url );
        data.setUsername( username );
        data.setPassword( password );
        CheckoutAction action = new CheckoutAction( view, data );
        action.actionPerformed( ae );
    }
}
