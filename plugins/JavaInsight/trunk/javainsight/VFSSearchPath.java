/*
 * VFSSearchPath.java
 * Copyright (c) 2001 Andre Kaplan
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


import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import jode.bytecode.SearchPath;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public class VFSSearchPath extends SearchPath {
    private String[] vfsDirs;


    public VFSSearchPath(String path, String vfsPath) {
        super(path);

        StringTokenizer tok = new StringTokenizer(vfsPath, "" + altPathSeparatorChar);
        Vector v = new Vector();
        while (tok.hasMoreTokens()) {
            String s = tok.nextToken();
            v.addElement(s);
        }
        this.vfsDirs = new String[v.size()];
        v.copyInto(this.vfsDirs);
    }


    private boolean vfsExists(String filename) {
        for (int i = 0; i < vfsDirs.length; i++) {
            VFS vfs = VFSManager.getVFSForPath(vfsDirs[i]);
            if (vfs == null) { continue; }

            String path = vfs.constructPath(vfsDirs[i], filename);

            Log.log(Log.DEBUG, this, "#### vfsExists path: [" + vfsDirs[i] + "][" + filename + "]");
            Log.log(Log.DEBUG, this, "#### vfsExists path: " + path);

            VFS.DirectoryEntry entry = null;
            try {
                entry = vfs._getDirectoryEntry(null, path, null);
            } catch (IOException ioe) {
                continue;
            }

            if ((entry != null) && (entry.type != VFS.DirectoryEntry.FILESYSTEM)) {
                return true;
            }
        }

        return false;
    }


    public boolean exists(String filename) {
        if (this.vfsExists(filename)) {
            return true;
        }

        return super.exists(filename);
    }


    public InputStream getFile(String filename) throws IOException {
        if (this.vfsExists(filename)) {
            for (int i = 0; i < vfsDirs.length; i++) {
                VFS vfs = VFSManager.getVFSForPath(vfsDirs[i]);
                if (vfs == null) { continue; }

                String path = vfs.constructPath(vfsDirs[i], filename);

                InputStream in = null;
                try {
                    in = vfs._createInputStream(null, path, true, null);
                } catch (IOException ioe) {
                    continue;
                }

                if (in != null) {
                    return in;
                }
            }
        }

        return super.getFile(filename);
    }


    public boolean isDirectory(String filename) {
        for (int i = 0; i < vfsDirs.length; i++) {
            VFS vfs = VFSManager.getVFSForPath(vfsDirs[i]);
            if (vfs == null) { continue; }

            String path = vfs.constructPath(vfsDirs[i], filename);

            VFS.DirectoryEntry entry = null;
            try {
                entry = vfs._getDirectoryEntry(null, path, null);
            } catch (IOException ioe) {
                continue;
            }

            if ((entry != null) && (entry.type == VFS.DirectoryEntry.DIRECTORY)) {
                return true;
            }
        }

        return super.isDirectory(filename);
    }


    public Enumeration listFiles(final String dirName) {
        return super.listFiles(dirName);
    }
}
