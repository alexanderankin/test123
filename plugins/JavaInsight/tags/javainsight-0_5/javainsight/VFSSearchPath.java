/*
 * VFSSearchPath.java
 * Copyright (c) 2001 Andre Kaplan
 *
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


import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import net.sf.jode.bytecode.ClassPath;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public class VFSSearchPath extends ClassPath {

    private String[] vfsDirs;


    public VFSSearchPath(String[] path, String vfsPath) {
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


    private boolean vfsExists(String filename, boolean directoryOnly) {
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

            if ((entry != null)) {
                if (directoryOnly) {
                    return (entry.type == VFS.DirectoryEntry.DIRECTORY);
                } else {
                    return (entry.type != VFS.DirectoryEntry.FILESYSTEM);
                }
            }
        }

        return false;
    }


    public boolean existsFile(String filename) {
        if (super.existsFile(filename)) {
            return true;
        }

        return this.vfsExists(filename, false);
    }


    public InputStream getFile(String filename) throws IOException {
        if (this.vfsExists(filename, false)) {
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
        } else {}

        return super.getFile(filename);
    }


    public boolean isDirectory(String filename) {
        if (super.isDirectory(filename)) {
            return true;
        }

        return this.vfsExists(filename, true);
    }


    public Enumeration listFiles(final String dirName) {
        if (this.vfsExists(dirName, true)) {
            for (int i = 0; i < vfsDirs.length; i++) {
                VFS vfs = VFSManager.getVFSForPath(vfsDirs[i]);
                if (vfs == null) { continue; }

                String path = vfs.constructPath(vfsDirs[i], dirName);

                VFS.DirectoryEntry[] entries = null;
                try {
                    entries = vfs._listDirectory(null, dirName, null);
                } catch (IOException ioe) {
                    continue;
                }

                if (entries != null) {
                    Vector files = new Vector(entries.length);
                    for (int j = 0; j < entries.length; j++) {
                        files.addElement(entries[j].path);
                    }
                    return files.elements();
                }
            }
        } else {}

        return super.listFiles(dirName);
    }

}
