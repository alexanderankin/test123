/*
 * ZipVFS.java
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


import java.awt.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public class ZipVFS extends ArchiveVFS {
    public static final String PROTOCOL = "zip";


    public ZipVFS() {
        super(PROTOCOL);
    }


    public int getCapabilities()
    {
        return (
              VFS.BROWSE_CAP
            | VFS.READ_CAP
        //    | VFS.WRITE_CAP
        );
    }


    public VFS.DirectoryEntry[] _listDirectory(Object session, String path,
        Component comp)
    {
        VFS.DirectoryEntry[] directory = ArchiveDirectoryCache.getCachedDirectory(path);

        if (directory != null) {
            return directory;
        }

        ArchivePath archive = new ArchivePath(path);
        String archiveProtocol = archive.protocol;
        String archivePath     = archive.pathName;

        Log.log(Log.DEBUG, this, "1. Path: " + path);
        Log.log(Log.DEBUG, this, "2. Archive Path: [" + archivePath + "]");

        VFS vfs = VFSManager.getVFSForPath(archivePath);

        try {
            Hashtable directories = new Hashtable();

            boolean ignoreErrors = true;
            InputStream in = vfs._createInputStream(session, archivePath, ignoreErrors, comp);
            ZipInputStream archiveIn = ArchiveUtilities.openZipInputStream(in);

            for (ZipEntry entry; (entry = archiveIn.getNextEntry()) != null; ) {
                this.addAllDirectories(
                      directories
                    , archiveProtocol + ':' + archivePath
                    , entry.getName()
                    , Math.max(0, entry.getSize()) // Avoids -1 size
                    , entry.isDirectory()
                );
            }

            archiveIn.close();

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

            VFS.DirectoryEntry[] retVal = ArchiveDirectoryCache.getCachedDirectory(path);
            return retVal;
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
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

        Log.log(Log.DEBUG, this, "1. _getDirectoryEntry: Archive Name: [" + archivePath + "]");
        Log.log(Log.DEBUG, this, "2. _getDirectoryEntry: Archive Path: [" + archiveEntry + "]");

        VFS vfs = VFSManager.getVFSForPath(archivePath);

        try {
            VFS.DirectoryEntry res = null;

            boolean ignoreErrors = true;
            InputStream in = vfs._createInputStream(session, archivePath, ignoreErrors, comp);
            ZipInputStream archiveIn = ArchiveUtilities.openZipInputStream(in);
            ZipEntry entry = null;
            String zipEntryName = null;
            for (; (entry = archiveIn.getNextEntry()) != null; ) {
                zipEntryName = entry.getName();
                if (archiveEntry.equals(zipEntryName)) {
                    break;
                }
            }

            if (entry == null) {
                return null;
            }

            String entryName = zipEntryName;
            int slashIdx = entryName.lastIndexOf(ArchiveVFS.fileSeparatorChar);
            if (slashIdx > 0) {
                entryName = entryName.substring(slashIdx + ArchiveVFS.fileSeparator.length());
            }
            int type = (
                (entry.isDirectory())
                ? VFS.DirectoryEntry.DIRECTORY
                : VFS.DirectoryEntry.FILE
            );
            long size = Math.max(0, entry.getSize()); // Avoids -1 size

            res = (
                new VFS.DirectoryEntry(
                    entryName,
                    archiveProtocol + ':' + archivePath + ArchiveVFS.archiveSeparatorChar + zipEntryName,
                    archiveProtocol + ':' + archivePath + ArchiveVFS.archiveSeparatorChar + zipEntryName,
                    type,
                    size,
                    false
                )
            );

            archiveIn.close();
            return res;
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
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

        Log.log(Log.DEBUG, this, "1. _createInputStream Path: " + path);
        Log.log(Log.DEBUG, this, "2. _createInputStream Archive Name: [" + archivePath + "]");
        Log.log(Log.DEBUG, this, "3. _createInputStream Archive Path: [" + archiveEntry + "]");

        VFS vfs = VFSManager.getVFSForPath(archivePath);

        if (path.endsWith(".marks")) {
            // Markers not supported
            Log.log(Log.DEBUG, this, "Marker Path: [" + path + "]");

            return null;
        }

        try {
            InputStream in = vfs._createInputStream(session, archivePath, ignoreErrors, comp);
            ZipInputStream archiveIn = ArchiveUtilities.openZipInputStream(in);
            for (ZipEntry entry; (entry = archiveIn.getNextEntry()) != null; ) {
                if (entry.getName().equals(archiveEntry)) {
                    return new BufferedInputStream(archiveIn);
                }
            }
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }

        return null;
    }
}

