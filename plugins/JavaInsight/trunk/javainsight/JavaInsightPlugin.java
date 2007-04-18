/*
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package javainsight;


import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.BufferUpdate;


/**
 * Main JavaInsight plugin class.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class JavaInsightPlugin extends EBPlugin
{
    /**
     * Handle message for decompile requests.
     */
    @Override
    public void handleMessage(EBMessage message) {
        if (message instanceof BufferUpdate) {
            BufferUpdate bu = (BufferUpdate) message;
            if (bu.getWhat() == BufferUpdate.LOADED) {
                Buffer buffer = bu.getBuffer();
                VFS    vfs    = buffer.getVFS();
                Mode   mode   = null;
                if (ClassVFS.PROTOCOL.equals(vfs.getName()) || JasminVFS.PROTOCOL.equals(vfs.getName())) {
                    mode = jEdit.getMode("bcel");
                } else if (JodeVFS.PROTOCOL.equals(vfs.getName())) {
                    mode = jEdit.getMode("java");
                }
                if (mode != null) {
                    buffer.setMode(mode);
                }
            }
        }
    }

    public void handleBrowserAction(View view, VFSFile[] files, String protocol) {
        if (files == null)  {
            // TODO: error message
            view.getToolkit().beep();
            return;
        }
    
        for (VFSFile entry : files) {
            if (entry.getType() == VFSFile.FILE) {
                VFS vfs = VFSManager.getVFSForPath(entry.getPath());
                if(protocol.equals(vfs.getName()))
                    jEdit.openFile(view, entry.getPath());
                else
                    jEdit.openFile(view, protocol + ':' + entry.getPath());
            }
        }
    }

}

