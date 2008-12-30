/*
Mergeright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above Mergeright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above Mergeright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE MergeRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE MergeRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.pv;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import projectviewer.vpt.VPTNode;

import ise.plugin.svn.action.MergeAction;
import ise.plugin.svn.data.MergeData;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.gui.MergeDialog;



public class MergeActor extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {

            // get the paths
            List<File> files = new ArrayList<File>();
            for ( VPTNode node : nodes ) {
                if ( node != null && node.getNodePath() != null ) {
                    files.add( new File( node.getNodePath() ) );
                }
            }

            // show the Merge dialog
            MergeDialog dialog = new MergeDialog( view, files.get(0).getAbsolutePath() );   // TODO: fill in remote destination
            GUIUtils.center(view, dialog);
            dialog.setVisible( true );
            MergeData data = dialog.getData();
            if ( data == null ) {
                return ;     // user canceled
            }

            if ( username != null && password != null ) {
                data.setUsername( username );
                data.setPassword( password );
            }

            MergeAction action = new MergeAction(view, data);
            action.actionPerformed(null);
        }
    }
}
