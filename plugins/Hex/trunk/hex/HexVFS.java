/*
 * HexVFS.java
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


import java.awt.Component;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public class HexVFS extends VFS {
    public static final String PROTOCOL = "hex";

    public HexVFS() {
        super(PROTOCOL);
    }


    public int getCapabilities() {
        return (
              VFS.BROWSE_CAP
            | VFS.READ_CAP
        //  | VFS.WRITE_CAP
        );
    }


    public char getFileSeparator() {
        return File.separatorChar;
    }


    public String getFileName(String path) {
        String protocol = this.getName();

        if (path.startsWith(protocol + ':')) {
            String hexPath = path.substring((protocol + ':').length());
            VFS vfs = VFSManager.getVFSForPath(hexPath);

            return vfs.getFileName(hexPath);
        } else {
            VFS vfs = VFSManager.getVFSForPath(path);

            return vfs.getFileName(path);
        }
    }


    public String getParentOfPath(String path) {
        String protocol = this.getName();

        if (path.startsWith(protocol + ':')) {
            String hexPath = path.substring((protocol + ':').length());
            VFS vfs = VFSManager.getVFSForPath(hexPath);

            return protocol + ':' + vfs.getParentOfPath(hexPath);
        } else {
            VFS vfs = VFSManager.getVFSForPath(path);

            return vfs.getParentOfPath(path);
        }
    }


    public String constructPath(String parent, String path) {
        String protocol = this.getName();

        if (path.startsWith(protocol + ':')) {
            String hexPath = parent.substring((protocol + ':').length());
            VFS vfs = VFSManager.getVFSForPath(hexPath);

            return protocol + ':' + vfs.constructPath(hexPath, path);
        } else {
            VFS vfs = VFSManager.getVFSForPath(parent);

            return vfs.constructPath(parent, path);
        }
    }


    public String showBrowseDialog(Object[] session, Component comp) {
        String protocol = this.getName();

        VFSBrowser browser = (VFSBrowser) comp;

        VFS.DirectoryEntry[] selected = browser.getSelectedFiles();
        if (selected == null || selected.length != 1) {
            // TODO: error message
            browser.getView().getToolkit().beep();
            return null;
        }

        VFS.DirectoryEntry entry = selected[0];
        if (entry.type == VFS.DirectoryEntry.FILE) {
            VFS vfs = VFSManager.getVFSForPath(entry.path);
            jEdit.openFile(browser.getView(), protocol + ':' + entry.path);
            return vfs.getParentOfPath(entry.path);
        }

        return entry.path;
    }


    public VFS.DirectoryEntry[] _listDirectory(Object session, String path,
        Component comp)
    {
        String protocol = this.getName();

        String hexPath = path;
        if (path.startsWith(protocol + ':')) {
            hexPath = hexPath.substring(protocol.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(hexPath);

        try {
            VFS.DirectoryEntry[] directoryEntries =
                vfs._listDirectory(session, hexPath, comp);

            if (directoryEntries == null) {
                return null;
            }

            VFS.DirectoryEntry[] retVal = new VFS.DirectoryEntry[directoryEntries.length];

            for (int i = 0; i < directoryEntries.length; i++) {
                retVal[i] = new VFS.DirectoryEntry(
                    directoryEntries[i].name,
                    protocol + ':' + directoryEntries[i].path,
                    protocol + ':' + directoryEntries[i].deletePath,
                    directoryEntries[i].type,
                    directoryEntries[i].length,
                    directoryEntries[i].hidden
                );
            }

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

        String hexPath = path;
        if (path.startsWith(protocol + ':')) {
            hexPath = hexPath.substring(protocol.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(hexPath);

        try {
            VFS.DirectoryEntry directoryEntry =
                vfs._getDirectoryEntry(session, hexPath, comp);

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


    public InputStream _createInputStream(Object session,
        String path, boolean ignoreErrors, Component comp)
        throws IOException
    {
        String protocol = this.getName();

        String hexPath = path;
        if (path.startsWith(protocol + ':')) {
            hexPath = hexPath.substring(protocol.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(hexPath);

        if (hexPath.endsWith(".marks")) {
            // .marks not supported
            return null;
        }

        try {
            InputStream in = new HexInputStream(
                vfs._createInputStream(session, hexPath, ignoreErrors, comp)
            );

            return in;
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }

        return null;
    }
}
