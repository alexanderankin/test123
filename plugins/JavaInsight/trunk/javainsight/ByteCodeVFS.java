/*
 * ByteCodeVFS.java
 * Copyright (c) 2001 Andre Kaplan
 *
 * jEdit settings: :tabSize=4:indentSize=4:noTabs=true:
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


import java.awt.Component;
import java.io.IOException;
import java.io.File;
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public abstract class ByteCodeVFS extends VFS {
    protected ByteCodeVFS(String name) {
        super(name, VFS.READ_CAP);
    }


    public char getFileSeparator() {
        return File.separatorChar;
    }


    public String getFileName(String path) {
        String protocol = this.getName();

        if (path.startsWith(protocol + ':')) {
            String clazzPath = path.substring((protocol + ':').length());
            VFS vfs = VFSManager.getVFSForPath(clazzPath);

            return vfs.getFileName(clazzPath);
        } else {
            VFS vfs = VFSManager.getVFSForPath(path);

            return vfs.getFileName(path);
        }
    }


    public String getParentOfPath(String path) {
        String protocol = this.getName();

        if (path.startsWith(protocol + ':')) {
            String clazzPath = path.substring((protocol + ':').length());
            VFS vfs = VFSManager.getVFSForPath(clazzPath);

            return protocol + ':' + vfs.getParentOfPath(clazzPath);
        } else {
            VFS vfs = VFSManager.getVFSForPath(path);

            return vfs.getParentOfPath(path);
        }
    }


    public String constructPath(String parent, String path) {
        String protocol = this.getName();

        if (parent.startsWith(protocol + ':')) {
            String clazzPath = parent.substring((protocol + ':').length());
            VFS vfs = VFSManager.getVFSForPath(clazzPath);

            return protocol + ':' + vfs.constructPath(clazzPath, path);
        } else {
            VFS vfs = VFSManager.getVFSForPath(parent);

            return vfs.constructPath(parent, path);
        }
    }


    public static void handleBrowserAction(View view,
        VFS.DirectoryEntry[] files, String protocol)
    {
        if (files == null || files.length != 1)  {
            // TODO: error message
            view.getToolkit().beep();
            return;
        }

        VFS.DirectoryEntry entry = files[0];
        if (entry.type != VFS.DirectoryEntry.FILE) {
            view.getToolkit().beep();
            return;
        }

        VFS vfs = VFSManager.getVFSForPath(entry.path);
        jEdit.openFile(view, protocol + ':' + entry.path);
    }


    public VFS.DirectoryEntry[] _listDirectory(Object session, String path,
        Component comp)
    {
        String protocol = this.getName();

        String clazzPath = path;
        if (path.startsWith(protocol + ':')) {
            clazzPath = clazzPath.substring(protocol.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(clazzPath);

        try {
            VFS.DirectoryEntry[] directoryEntries =
                vfs._listDirectory(session, clazzPath, comp);

            if (directoryEntries == null) {
                return null;
            }

            Vector v = new Vector();

            for (int i = 0; i < directoryEntries.length; i++) {
                if (   directoryEntries[i].path.endsWith(".class")
                    || (directoryEntries[i].type == VFS.DirectoryEntry.DIRECTORY)
                ) {
                    v.addElement(
                        new VFS.DirectoryEntry(
                            directoryEntries[i].name,
                            protocol + ':' + directoryEntries[i].path,
                            protocol + ':' + directoryEntries[i].deletePath,
                            directoryEntries[i].type,
                            directoryEntries[i].length,
                            directoryEntries[i].hidden
                        )
                    );
                }
            }
            VFS.DirectoryEntry[] retVal = new VFS.DirectoryEntry[v.size()];
            v.copyInto(retVal);

            return retVal;
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }

        return null;
    }


    public DirectoryEntry _getDirectoryEntry(Object session, String path,
        Component comp)
    {
        String protocol = this.getName();

        String clazzPath = path;
        if (path.startsWith(protocol + ':')) {
            clazzPath = clazzPath.substring(protocol.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(clazzPath);

        try {
            VFS.DirectoryEntry directoryEntry =
                vfs._getDirectoryEntry(session, clazzPath, comp);

            if (directoryEntry == null) {
                return null;
            }

            return new VFS.DirectoryEntry(
                directoryEntry.name,
                protocol + ':' + directoryEntry.path,
                protocol + ':' + directoryEntry.deletePath,
                directoryEntry.type,
                directoryEntry.length,
                directoryEntry.hidden
            );
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }

        return null;
    }
}
