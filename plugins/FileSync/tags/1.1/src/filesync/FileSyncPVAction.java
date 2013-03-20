/*
Copyright (c) 2012, Dale Anson
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

package filesync;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import org.gjt.sp.jedit.jEdit;

/**
 * ProjectViewer Action to be added to the PV context menu.  This class serves
 * as menu item to do a full file sync of project files.
 */
public class FileSyncPVAction extends projectviewer.action.Action {

    private VPTProject project = null;

    public String getText() {
        return jEdit.getProperty( "filesync.SyncAll", "FileSync - Sync All Files" );
    }

    public void prepareForNode( final VPTNode node ) {
        project = node.findProjectFor( node );
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( project != null ) {
            String projectName = project.getName();

            // maybe remove all files
            int delete = JOptionPane.showConfirmDialog( viewer, jEdit.getProperty("filesync.Delete_existing_files_from_target_before_sync?", "Delete existing files from target before sync?"), jEdit.getProperty("filesync.Delete_files_from_target?", "Delete files from target?"), JOptionPane.YES_NO_CANCEL_OPTION );
            switch ( delete ) {
                case JOptionPane.YES_OPTION:
                    FileSyncPlugin.removeAllFiles( projectName );
                    break;
                case JOptionPane.NO_OPTION:
                    // do nothing on no
                    break;
                default:
                    // do nothing on cancel
                    return;
            }

            // add all files
            FileSyncPlugin.syncAllFiles( projectName );
        }
    }
}