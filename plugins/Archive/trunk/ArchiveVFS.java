/*
 * ArchiveVFS.java
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


import java.awt.Component;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;


public abstract class ArchiveVFS extends VFS {
    public class ArchivePath {
        public String protocol;
        public String pathName;
        public String entryName;

        public ArchivePath(String path) {
            String archive = path.substring((ArchiveVFS.this.getName() + ':').length());
            String archivePath = archive;
            String archiveEntry = "";

            int idx = -1;
            if ((idx = archive.lastIndexOf('!')) != -1) {
                archivePath  = archive.substring(0, idx);
                archiveEntry = archive.substring(idx + 1);
            }

            // Remove archiveEntry trailing slashes
            for (int i = archiveEntry.length() - 1; i >= 0; i--) {
                if (archiveEntry.charAt(i) != '/') {
                    if (i < archiveEntry.length() - 1) {
                        archiveEntry = archiveEntry.substring(0, i + 1);
                    }
                    break;
                }
            }

            this.protocol  = ArchiveVFS.this.getName();
            this.pathName  = archivePath;
            this.entryName = archiveEntry;
        }
    }


    protected ArchiveVFS(String name) {
        super(name);
    }


    public String getParentOfPath(String path) {
        ArchivePath archive = new ArchivePath(path);
        String archiveProtocol = archive.protocol;
        String archivePath  = archive.pathName;
        String archiveEntry = archive.entryName;

        int slashIdx = archiveEntry.lastIndexOf('/');
        if (slashIdx != -1) {
            return (
                  archiveProtocol + ':'
                + archivePath
                + '!'
                + archiveEntry.substring(0, slashIdx)
            );
        }

        if (archiveEntry.length() > 0) {
            return (
                  archiveProtocol + ':'
                + archivePath
                + '!'
            );
        }

        VFS vfs = VFSManager.getVFSForPath(archivePath);
        return vfs.getParentOfPath(archivePath);
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
        if (entry.type != VFS.DirectoryEntry.FILE) {
            browser.getView().getToolkit().beep();
            return null;
        }

        return this.getName() + ':' + entry.path + '!';
    }


    protected void addAllDirectories(
            Hashtable directories,
            String vfsPath,
            String entryName, long entrySize, boolean entryIsDirectory
    ) {
        // We add all possible directories to directories hashtable
        Hashtable directoryEntries = null;
        StringTokenizer tokenizer = new StringTokenizer(entryName, "/");

        String currentPath = "";
        String nextPath    = "";

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            currentPath  = nextPath;
            if (currentPath.equals("")) {
                nextPath = token;
            } else {
                nextPath = currentPath + '/' + token;
            }

            String currentVFSPath = vfsPath + "!" + currentPath;
            String nextVFSPath    = vfsPath + "!" + nextPath;

            directoryEntries = (Hashtable) directories.get(currentVFSPath);
            if (directoryEntries == null) {
                directoryEntries = new Hashtable();
                directories.put(currentVFSPath, directoryEntries);
            }

            int type = VFS.DirectoryEntry.DIRECTORY;
            long length = 0;

            if (!tokenizer.hasMoreTokens()) { // Last Element
                if (!entryIsDirectory) {
                    type   = VFS.DirectoryEntry.FILE;
                    length = entrySize;
                }
            }

            directoryEntries.put(
                token,
                new VFS.DirectoryEntry(
                    token, nextVFSPath, nextVFSPath, type, length, false
                )
            );
        }
    }
}

