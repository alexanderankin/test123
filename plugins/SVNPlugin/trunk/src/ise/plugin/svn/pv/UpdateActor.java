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

package ise.plugin.svn.pv;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Update;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.UpdateData;
import ise.plugin.svn.gui.UpdateResultsPanel;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import projectviewer.vpt.VPTNode;
import org.gjt.sp.jedit.View;
import ise.plugin.svn.action.UpdateAction;

/**
 * Action for ProjectViewer's context menu to execute an svn update.
 */
public class UpdateActor extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null ) {
                    paths.add( node.getNodePath() );
                }
            }

            UpdateAction action = new UpdateAction(view, paths, username, password);
            action.actionPerformed(ae);
        }
    }
}
