/*
 * HexPlugin.java
 * Copyright (c) 2000, 2001 Andre Kaplan
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


package hex;


import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;


public class HexPlugin extends EBPlugin
{
    public void start() {
        VFSManager.registerVFS(HexVFS.PROTOCOL, new HexVFS());
    }


    public void stop() {}


    public void createMenuItems(Vector menuItems) {}


    public void handleMessage(EBMessage message) {
        if (message instanceof BufferUpdate) {
            BufferUpdate bu = (BufferUpdate) message;

            if (bu.getWhat() == BufferUpdate.CREATED) {
                Buffer buffer = bu.getBuffer();
                VFS    vfs    = buffer.getVFS();
                Mode   mode   = null;
                if (vfs instanceof HexVFS) {
                    mode = jEdit.getMode("text");
                } else {}

                if (mode != null) {
                    buffer.setMode(mode);
                }
            }
        }
    }
}
