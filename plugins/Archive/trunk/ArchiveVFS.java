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

import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public class ArchiveVFS extends VFS {
    public static final String PROTOCOL = "archive";

    public static final String archiveSeparator    = "!";
    public static final int    archiveSeparatorLen = 1;

    public static final char   fileSeparatorChar = '/';
    public static final String fileSeparator     = "/";
    public static final int    fileSeparatorLen  = 1;


    public class ArchivePath {
        public String protocol;
        public String pathName;
        public String entryName;


        public ArchivePath(String path) {
            String archive = path.substring((ArchiveVFS.this.getName() + ':').length());
            String archivePath = archive;
            String archiveEntry = "";

            int idx = -1;
            if ((idx = archive.lastIndexOf(ArchiveVFS.archiveSeparator)) != -1) {
                archivePath  = archive.substring(0, idx);
                archiveEntry = archive.substring(idx + ArchiveVFS.archiveSeparatorLen);
            }

            // Remove archiveEntry leading and trailing slashes
            for (int i = 0; i < archiveEntry.length(); i++) {
                if (archiveEntry.charAt(i) != ArchiveVFS.fileSeparatorChar) {
                    if (i > 0) {
                        archiveEntry = archiveEntry.substring(i);
                    }
                    break;
                }
            }

            for (int i = archiveEntry.length() - 1; i >= 0; i--) {
                if (archiveEntry.charAt(i) != ArchiveVFS.fileSeparatorChar) {
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


    public ArchiveVFS(String name) {
        super(name);
    }


    public ArchiveVFS() {
        this(ArchiveVFS.PROTOCOL);
    }


    public int getCapabilities() {
        return (
              VFS.BROWSE_CAP
            | VFS.READ_CAP
        //    | VFS.WRITE_CAP
        );
    }


    public String getFileName(String path) {
        ArchivePath archive = new ArchivePath(path);
        String archiveProtocol = archive.protocol;
        String archivePath  = archive.pathName;
        String archiveEntry = archive.entryName;

        int fileSeparatorIdx = archiveEntry.lastIndexOf(ArchiveVFS.fileSeparatorChar);
        if (fileSeparatorIdx != -1) {
            return (
                archiveEntry.substring(
                    fileSeparatorIdx + ArchiveVFS.archiveSeparatorLen
                )
            );
        }

        if (archiveEntry.length() > 0) {
            return archiveEntry;
        }

        VFS vfs = VFSManager.getVFSForPath(archivePath);
        return vfs.getFileName(archivePath);
    }


    public String getParentOfPath(String path) {
        ArchivePath archive = new ArchivePath(path);
        String archiveProtocol = archive.protocol;
        String archivePath  = archive.pathName;
        String archiveEntry = archive.entryName;

        int fileSeparatorIdx = archiveEntry.lastIndexOf(ArchiveVFS.fileSeparatorChar);
        if (fileSeparatorIdx != -1) {
            return (
                  archiveProtocol + ':'
                + archivePath
                + ArchiveVFS.archiveSeparator + ArchiveVFS.fileSeparator
                + archiveEntry.substring(0, fileSeparatorIdx)
            );
        }

        if (archiveEntry.length() > 0) {
            return (
                  archiveProtocol + ':'
                + archivePath
                + ArchiveVFS.archiveSeparator
            );
        }

        VFS vfs = VFSManager.getVFSForPath(archivePath);
        return vfs.getParentOfPath(archivePath);
    }


    public String constructPath(String parent, String path) {
        // Log.log(Log.DEBUG, this, "constructPath: [" + parent + "][" + path + "]");
        if (parent.endsWith(ArchiveVFS.archiveSeparator)) {
            if (path.startsWith(ArchiveVFS.fileSeparator)) {
                return parent + path;
            } else {
                return parent + ArchiveVFS.fileSeparator + path;
            }
        } else {
            if (parent.endsWith(ArchiveVFS.fileSeparator)) {
                return parent + path;
            } else {
                return parent + ArchiveVFS.fileSeparator + path;
            }
        }
    }


    public char getFileSeparator() {
        return '/';
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

        return this.getName() + ':' + entry.path + ArchiveVFS.archiveSeparator;
    }


    protected InputStream openArchiveStream(InputStream in) throws IOException {
        return ArchiveUtilities.openArchiveStream(
            ArchiveUtilities.openCompressedStream(in)
        );
    }


    private void cacheDirectories(Object session, String path,
        Component comp
    ) {
        ArchivePath archive = new ArchivePath(path);
        String archiveProtocol = archive.protocol;
        String archivePath     = archive.pathName;

        // Log.log(Log.DEBUG, this, "1. Path: " + path);
        // Log.log(Log.DEBUG, this, "2. Archive Path: [" + archivePath + "]");

        VFS vfs = VFSManager.getVFSForPath(archivePath);

        try {
            boolean ignoreErrors = true;
            InputStream in = vfs._createInputStream(session, archivePath, ignoreErrors, comp);

            InputStream archiveIn = this.openArchiveStream(in);

            Hashtable directories = null;
            ArchiveCommand archiveCmd = ArchiveCommand.getCommand(archiveIn);
            if (archiveCmd != null) {
                directories = archiveCmd.getDirectories(archiveProtocol, archivePath);
            }
            archiveIn.close();

            if (directories == null) {
                return;
            }

            for (Enumeration e = directories.keys(); e.hasMoreElements(); ) {
                String name = (String) e.nextElement();
                Hashtable h = (Hashtable) directories.get(name);

                VFS.DirectoryEntry[] list = new VFS.DirectoryEntry[h.size()];
                int idx1 = 0;
                for (Enumeration e1 = h.elements(); e1.hasMoreElements(); ) {
                    list[idx1++] = (VFS.DirectoryEntry) e1.nextElement();
                }
                ArchiveDirectoryCache.setCachedDirectory(name, list);
            }
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }
    }


    public VFS.DirectoryEntry[] _listDirectory(Object session, String path,
        Component comp)
    {
        VFS.DirectoryEntry[] directory =
            ArchiveDirectoryCache.getCachedDirectory(path);

        if (directory != null) {
            return directory;
        }

        ArchivePath archive = new ArchivePath(path);
        String archiveProtocol = archive.protocol;
        String archivePath     = archive.pathName;

        // We cache the archive only if it was not previously cached
        String archiveRoot = archiveProtocol + ':' + archive.pathName + ArchiveVFS.archiveSeparator;

        if (ArchiveDirectoryCache.getCachedDirectory(archiveRoot) == null) {
            this.cacheDirectories(session, path, comp);

            return ArchiveDirectoryCache.getCachedDirectory(path);
        }

        return null;
    }


    public DirectoryEntry _getDirectoryEntry(Object session, String path,
        Component comp)
    {
        ArchivePath archive = new ArchivePath(path);
        String archiveProtocol = archive.protocol;
        String archivePath  = archive.pathName;
        String archiveEntry = archive.entryName;

        if (archive.entryName.equals("")) {
            return null;
        }

        VFS.DirectoryEntry[] directory = this._listDirectory(
            session, this.getParentOfPath(path), comp
        );

        if  (directory == null) {
            return null;
        }

        for (int i = 0; i < directory.length; i++) {
            VFS.DirectoryEntry entry = directory[i];
            if (    (entry.type == VFS.DirectoryEntry.FILE)
                 && entry.name.equals(archive.entryName)
            ) {
                return directory[i];
            }
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
        ArchivePath archive = new ArchivePath(path);
        String archivePath  = archive.pathName;
        String archiveEntry = archive.entryName;

        // Log.log(Log.DEBUG, this, "1. _createInputStream Path: " + path);
        // Log.log(Log.DEBUG, this, "2. _createInputStream Archive Name: [" + archivePath + "]");
        // Log.log(Log.DEBUG, this, "3. _createInputStream Archive Path: [" + archiveEntry + "]");

        VFS vfs = VFSManager.getVFSForPath(archivePath);

        if (path.endsWith(".marks")) {
            // Markers not supported
            Log.log(Log.DEBUG, this, "Marker Path: [" + path + "]");

            return null;
        }

        try {
            InputStream in = vfs._createInputStream(session, archivePath, ignoreErrors, comp);
            InputStream archiveIn = this.openArchiveStream(in);

            ArchiveCommand archiveCmd = ArchiveCommand.getCommand(archiveIn);
            if (archiveCmd != null) {
                return archiveCmd.createInputStream(archiveEntry);
            }

            return null;
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }

        return null;
    }


    public static void addAllDirectories(
            Hashtable directories,
            String archiveProtocol, String archivePath,
            String entryName, long entrySize, boolean entryIsDirectory
    ) {
        // We add all possible directories to directories hashtable
        Hashtable directoryEntries = null;
        StringTokenizer tokenizer = new StringTokenizer(entryName, ArchiveVFS.fileSeparator);

        String currentPath = "";
        String nextPath    = "";

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            currentPath  = nextPath;
            if (currentPath.equals("")) {
                nextPath = token;
            } else {
                nextPath = currentPath + ArchiveVFS.fileSeparatorChar + token;
            }

            String vfsPath = archiveProtocol + ':' + archivePath;
            String currentVFSPath = vfsPath + ArchiveVFS.archiveSeparator + ArchiveVFS.fileSeparator + currentPath;
            String nextVFSPath    = vfsPath + ArchiveVFS.archiveSeparator + ArchiveVFS.fileSeparator + nextPath;

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

