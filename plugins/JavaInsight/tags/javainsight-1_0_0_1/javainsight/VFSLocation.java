/*
 * VFSLocation.java
 * Copyright (c) 2007 Dirk Moebius
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
import java.util.Vector;

import net.sf.jode.bytecode.ClassPath;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;


public class VFSLocation extends ClassPath.Location
{
    private String vfsPath;


    public VFSLocation(String vfsPath) {
        this.vfsPath = vfsPath;
    }


    @Override
    public boolean exists(String file) {
        VFS vfs = VFSManager.getVFSForPath(vfsPath);
        if (vfs == null) {
            return false;
        }
        String path = vfs.constructPath(vfsPath, file);
        VFSFile entry = null;
        try {
            entry = vfs._getFile(null, path, null);
            return entry != null;
        } catch (IOException ioe) {
            return false;
        }
    }


    @Override
    public InputStream getFile(String file) throws IOException {
        if (exists(file)) {
            VFS vfs = VFSManager.getVFSForPath(vfsPath);
            if (vfs == null) { 
                return null;
            }
            String path = vfs.constructPath(vfsPath, file);
            return vfs._createInputStream(null, path, true, null);
        }
        return null;
    }


    @Override
    public boolean isDirectory(String file) {
        VFS vfs = VFSManager.getVFSForPath(vfsPath);
        if (vfs == null) {
            return false;
        }
        String path = vfs.constructPath(vfsPath, file);
        VFSFile entry = null;
        try {
            entry = vfs._getFile(null, path, null);
            return entry != null && entry.getType() == VFSFile.DIRECTORY;
        } catch (IOException ioe) {
            return false;
        }
    }


    @Override
    public Enumeration listFiles(final String directory) {
        if (!isDirectory(directory)) {
            return null;
        }
        VFS vfs = VFSManager.getVFSForPath(vfsPath);
        if (vfs == null) { 
            return null; 
        }
        VFSFile[] entries = null;
        try {
            entries = vfs._listFiles(null, directory, null);
        } catch (IOException ioe) {
            return null;
        }
        if (entries != null) {
            Vector<String> files = new Vector<String>(entries.length);
            for (int i = 0; i < entries.length; i++) {
                files.addElement(entries[i].getPath());
            }
            return files.elements();
        }
        return null;
    }
    
    @Override
    public String toString() {
        return vfsPath;
    }
}
