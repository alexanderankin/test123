/*
 * HexVFS.java
 * Copyright (c) 2000 Andre Kaplan
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


    public int getCapabilities()
    {
        return (
              VFS.BROWSE_CAP
            | VFS.READ_CAP
        //  | WRITE_CAP
        );
    }


    public char getFileSeparator() {
        return File.separatorChar;
    }


    public String getParentOfPath(String path)
    {
        if (path.startsWith(PROTOCOL + ':')) {
            return path.substring((PROTOCOL + ':').length());
        } else {
            return path;
        }
    }


    public String showBrowseDialog(Object[] session, Component comp) {
        VFSBrowser browser = (VFSBrowser) comp;

        VFS.DirectoryEntry[] selected = browser.getSelectedFiles();
        if (selected == null || selected.length != 1) {
            // TODO: error message
            browser.getView().getToolkit().beep();
            return null;
        }

        VFS.DirectoryEntry entry = selected[0];
        if (entry.type == VFS.DirectoryEntry.FILE) {
            jEdit.openFile(browser.getView(), PROTOCOL + ':' + entry.path);
            return null;
        }

        return PROTOCOL + ':' + entry.path;
    }


    public VFS.DirectoryEntry[] _listDirectory(Object session, String path,
        Component comp)
    {
        Log.log(Log.DEBUG, this, "_listDirectory Path: " + path);
        String hexPath = this.getParentOfPath(path);
        Log.log(Log.DEBUG, this, "_listDirectory Hex Path: [" + hexPath + "]");

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
                    PROTOCOL + ':' + directoryEntries[i].name,
                    PROTOCOL + ':' + directoryEntries[i].path,
                    PROTOCOL + ':' + directoryEntries[i].deletePath,
                    directoryEntries[i].type,
                    directoryEntries[i].length,
                    directoryEntries[i].hidden
                );
            }
            return retVal;

        } catch (IOException ioe) {
            Log.log(Log.DEBUG, this, ioe);
        }

        return null;
    }


    public DirectoryEntry _getDirectoryEntry(Object session, String path,
        Component comp)
    {
        Log.log(Log.DEBUG, this, "_getDirectoryEntry Path: " + path);
        String hexPath = this.getParentOfPath(path);
        Log.log(Log.DEBUG, this, "_getDirectoryEntry Hex Path: [" + hexPath + "]");

        VFS vfs = VFSManager.getVFSForPath(hexPath);

        try {
            VFS.DirectoryEntry directoryEntry =
                vfs._getDirectoryEntry(session, hexPath, comp);

            if (directoryEntry == null) {
                return null;
            }

            return new VFS.DirectoryEntry(
                PROTOCOL + ':' + directoryEntry.name,
                PROTOCOL + ':' + directoryEntry.path,
                PROTOCOL + ':' + directoryEntry.deletePath,
                directoryEntry.type,
                directoryEntry.length,
                directoryEntry.hidden
            );
        } catch (IOException ioe) {
            Log.log(Log.DEBUG, this, ioe);
        }

        return null;
    }


    /**
     * Creates an input stream. This method is called from the I/O
     * thread.
     * @param session the VFS session
     * @param path The path
     * @param ignoreErrors If true, file not found errors should be
     * ignored
     * @param comp The component that will parent error dialog boxes
     * @exception IOException If an I/O error occurs
     */
    public InputStream _createInputStream(Object session,
        String path, boolean ignoreErrors, Component comp)
        throws IOException
    {
        Log.log(Log.DEBUG, this, "_createInputStream Path: " + path);
        String hexPath = this.getParentOfPath(path);
        Log.log(Log.DEBUG, this, "_createInputStream Hex Path: [" + hexPath + "]");

        VFS vfs = VFSManager.getVFSForPath(hexPath);

        if (hexPath.endsWith(".marks")) {
            // .marks not supported
            return null; // vfs._createInputStream(session, hexPath, ignoreErrors, comp);
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
