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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;


public abstract class ByteCodeVFS extends VFS
{
    protected ByteCodeVFS(String name) {
        super(name, VFS.READ_CAP);
    }


    @Override
    public char getFileSeparator() {
        return File.separatorChar;
    }


    @Override
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


    @Override
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


    @Override
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


    @Override
    public VFSFile[] _listFiles(Object session, String path, Component comp) {
        String protocol = this.getName();

        String clazzPath = path;
        if (path.startsWith(protocol + ':')) {
            clazzPath = clazzPath.substring(protocol.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(clazzPath);

        try {
            VFSFile[] directoryEntries =
                vfs._listFiles(session, clazzPath, comp);

            if (directoryEntries == null) {
                return null;
            }

            ArrayList<VFSFile> result = new ArrayList<VFSFile>();

            for (int i = 0; i < directoryEntries.length; i++) {
                if (   directoryEntries[i].getPath().toLowerCase().endsWith(".class")
                    || (directoryEntries[i].getType() == VFSFile.DIRECTORY)
                ) {
                    result.add(
                        new VFSFile(
                            directoryEntries[i].getName(),
                            protocol + ':' + directoryEntries[i].getPath(),
                            protocol + ':' + directoryEntries[i].getDeletePath(),
                            directoryEntries[i].getType(),
                            directoryEntries[i].getLength(),
                            directoryEntries[i].isHidden()
                        )
                    );
                }
            }
            
            return result.toArray(new VFSFile[result.size()]);
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }

        return null;
    }


    @Override
    public VFSFile _getFile(Object session, String path, Component comp) {
        String protocol = this.getName();

        String clazzPath = path;
        if (path.startsWith(protocol + ':')) {
            clazzPath = clazzPath.substring(protocol.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(clazzPath);

        try {
            VFSFile file = vfs._getFile(session, clazzPath, comp);

            if (file == null) {
                return null;
            }

            return new VFSFile(
                file.getName(),
                protocol + ':' + file.getPath(),
                protocol + ':' + file.getDeletePath(),
                file.getType(),
                file.getLength(),
                file.isHidden()
            );
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }

        return null;
    }
}
