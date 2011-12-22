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
package ise.plugin.svn;

import java.io.File;
import java.util.*;

import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.ViewUpdate;
import ise.plugin.svn.gui.OutputPanel;
import ise.plugin.svn.pv.SVNAction;
import java.awt.event.*;
import javax.swing.*;


public class SVNPlugin extends EBPlugin {

    public final static String NAME = "subversion";
    private static HashMap<View, OutputPanel> panelMap = null;
    private static File storageDir = null;

    public static OutputPanel getOutputPanel( View view ) {
        if ( panelMap == null ) {
            panelMap = new HashMap<View, OutputPanel>();
        }
        OutputPanel panel = panelMap.get( view );
        if ( panel == null ) {
            panel = new OutputPanel();
            panelMap.put( view, panel );
        }
        return panel;
    }

    public void handleMessage( EBMessage message ) {
        if ( message instanceof ViewUpdate ) {
            ViewUpdate vu = ( ViewUpdate ) message;
            if ( ViewUpdate.CLOSED == vu.getWhat() ) {
                if (panelMap != null) {
                    panelMap.remove( vu.getView() );
                }
                SVNAction.remove( vu.getView() );
            }
        }
    }

    public void stop() {
        if ( panelMap != null ) {
            panelMap.clear();
            panelMap = null;
        }
    }

    public void start() {
    }
    
    public static File getSvnStorageDir() {
        if (storageDir != null) {
            return storageDir;   
        }
        try {
            File homeDir = jEdit.getPlugin( "ise.plugin.svn.SVNPlugin" ).getPluginHome();
            if ( !homeDir.exists() ) {
                homeDir.mkdir();
            }
            storageDir = new File(homeDir, ".subversion");
            if (!storageDir.exists()) {
                storageDir.mkdir();   
            }
            return storageDir;
        }
        catch ( Exception ignored ) {
            return null;
        }
    }
}