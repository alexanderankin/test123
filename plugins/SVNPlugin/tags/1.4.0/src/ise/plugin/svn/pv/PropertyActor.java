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

import common.swingworker.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.action.PropertyAction;
import ise.plugin.svn.data.PropertyData;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Action for ProjectViewer's context menu to fetch properties.
 */
public class PropertyActor extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            boolean hasDirectory = false;
            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null ) {
                    paths.add( node.getNodePath() );
                    if (!hasDirectory && node.isDirectory()) {
                        hasDirectory = true;
                    }
                }
            }

            PropertyData data = new PropertyData();
            data.setPaths(paths);
            data.setUsername(username);
            data.setPassword(password);
            data.setPathsAreURLs(false);
            data.setHasDirectory(hasDirectory);
            data.setRemote(false);
            data.setRevision(SVNRevision.WORKING);
            PropertyAction la = new PropertyAction(view, data);
            la.actionPerformed(ae);
        }
    }
}
