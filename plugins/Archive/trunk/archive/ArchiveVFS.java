/*
 * ArchiveVFS.java
 *
 * :tabSize=4:indentSize=4:noTabs=true:
 *
 * Copyright (c) 2001, 2002 Andre Kaplan
 * Portions copyright (C) 2004 Slava Pestov
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


package archive;

import java.awt.Component;

import java.io.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import java.util.zip.*;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;

import com.aftexsw.util.bzip.*;

import com.ice.tar.*;


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

        public String toString() {
            return "ArchivePath[" + protocol + ":" + pathName
                + ArchiveVFS.archiveSeparator + entryName;
        }
    }


    public ArchiveVFS() {
        super(PROTOCOL,VFS.READ_CAP | VFS.WRITE_CAP);
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

            Hashtable directories = getDirectories(archiveIn,
                    archiveProtocol, archivePath);

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

    private Hashtable getDirectories(InputStream source,
        String archiveProtocol, String archivePath)
        throws IOException
    {
        if(source instanceof TarInputStream) {
            return getTarDirectories((TarInputStream)source,
                archiveProtocol, archivePath);
        } else if(source instanceof ZipInputStream) {
            return getZipDirectories((ZipInputStream)source,
                archiveProtocol, archivePath);
        } else {
            throw new ClassCastException();
        }
    }

    private Hashtable getTarDirectories(TarInputStream source,
        String archiveProtocol, String archivePath)
        throws IOException
    {
        Hashtable directories = new Hashtable();

        for (TarEntry entry; (entry = source.getNextEntry()) != null; ) {
            ArchiveVFS.addAllDirectories(
                  directories
                , archiveProtocol, archivePath
                , entry.getName()
                , entry.getSize()
                , entry.isDirectory()
            );
        }

        return directories;
    }

    private Hashtable getZipDirectories(ZipInputStream source,
        String archiveProtocol, String archivePath)
            throws IOException
    {
        Hashtable directories = new Hashtable();

        for (ZipEntry entry; (entry = source.getNextEntry()) != null; ) {
            ArchiveVFS.addAllDirectories(
                  directories
                , archiveProtocol, archivePath
                , entry.getName()
                , Math.max(0, entry.getSize()) // Avoids -1 size
                , entry.isDirectory()
            );
        }

        return directories;
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

        if (archiveEntry.equals("")) {
            return null;
        }

        VFS.DirectoryEntry[] directory = this._listDirectory(
            session, this.getParentOfPath(path), comp
        );

        if  (directory == null) {
            return null;
        }

        String canonPath = (
              archiveProtocol + ':' + archivePath
            + ArchiveVFS.archiveSeparator + ArchiveVFS.fileSeparator
            + archiveEntry
        );

        for (int i = 0; i < directory.length; i++) {
            VFS.DirectoryEntry entry = directory[i];
            if (entry.path.equals(canonPath)) {
                return entry;
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

            return createInputStream(archiveIn,archiveEntry);
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }

        return null;
    }

    private InputStream createInputStream(InputStream in, String path)
        throws IOException {
        if(in instanceof TarInputStream)
            return createTarInputStream((TarInputStream)in,path);
        else if(in instanceof ZipInputStream)
            return createZipInputStream((ZipInputStream)in,path);
        else
            throw new ClassCastException();
    }

    private InputStream createZipInputStream(ZipInputStream in,
        String path) throws IOException {
        for (ZipEntry entry; (entry = in.getNextEntry()) != null; ) {
            if (entry.getName().equals(path)) {
                return new BufferedInputStream(in);
            }
        }

        return null;
    }

    public InputStream createTarInputStream(TarInputStream in,
        String path) throws IOException {
        for (TarEntry entry; (entry = in.getNextEntry()) != null;) {
            if (entry.isDirectory()) {
                continue;
            }
            if (entry.getName().equals(path)) {
                return new BufferedInputStream(in);
            }
        }

        return null;
    }

    /**
     * This only works when saving an actual buffer!
     * Otherwise <code>_saveComplete()</code> is never called!
     */
	public OutputStream _createOutputStream(Object session,
		String path, Component comp)
		throws IOException
	{
        // make this more discriminating in the future.
        ArchiveDirectoryCache.clearAllCachedDirectories();

        ArchivePath archive = new ArchivePath(path);
        String archivePath  = archive.pathName;
        String archiveEntry = archive.entryName;

        if (path.endsWith(".marks")) {
            // Markers not supported
            Log.log(Log.DEBUG, this, "Marker Path: [" + path + "]");

            return null;
        }

        String outputFile = ArchivePlugin.tempFileName();
        FileOutputStream out = new FileOutputStream(outputFile);

        Runnable r = new FinishSavingArchive(comp,archive,outputFile);
        // this is really a hack. runInAWTThread() might evaluate
        // immediately if we're in the AWT thread, and we want
        // the buffer save to finish first.
        if(SwingUtilities.isEventDispatchThread())
            SwingUtilities.invokeLater(r);
        else
            VFSManager.runInAWTThread(r);

		return out;
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

    static class FinishSavingArchive implements Runnable {
        private Component comp;
        private ArchivePath archive;
        private String outputFile;

        FinishSavingArchive(Component comp, ArchivePath archive,
            String outputFile) {
            this.comp = comp;
            this.archive = archive;
            this.outputFile = outputFile;
        }

        public void run() {
            Log.log(Log.DEBUG,this,"Saved entry " + archive + " to "
                + outputFile);

            String archivePath = archive.pathName;

            VFS vfs = VFSManager.getVFSForPath(archivePath);

            String savePath = vfs.getTwoStageSaveName(archivePath);

            InputStream archiveIn = null;
            try {
                archiveIn = vfs._createInputStream(null,
                    archivePath,false,comp);
                if(archiveIn == null)
                    throw new IOException("FIXME");

                archiveIn = ArchiveUtilities.openCompressedStream(archiveIn);
                boolean gzip = false, bzip2 = false;
                if(archiveIn instanceof GZIPInputStream)
                    gzip = true;
                if(archiveIn instanceof CBZip2InputStream)
                    bzip2 = true;

                archiveIn = ArchiveUtilities.openArchiveStream(archiveIn);

                if(archiveIn instanceof ZipInputStream) {
                    saveZipArchive((ZipInputStream)archiveIn,archive,
                        savePath,outputFile,comp);
                } else if(archiveIn instanceof TarInputStream) {
                    saveTarArchive((TarInputStream)archiveIn,archive,
                        savePath,outputFile,comp,gzip,bzip2);
                }
                vfs._rename(null,savePath,archivePath,comp);

            } catch(IOException e) {
                Log.log(Log.ERROR,this,e);
                VFSManager.error(comp,archive.pathName,"ioerror",
                    new String[] { e.toString() });
            } finally {
                try {
                    if(archiveIn != null)
                        archiveIn.close();
                } catch(IOException e) {}
            }
        }

        /**
         * Copy entries from archive.pathName to savePath; replace the
         * entry at archive.entryName with contents of outputFile.
         */
        private void saveZipArchive(ZipInputStream archiveIn,
            ArchivePath archive, String savePath,
            String outputFile, Component comp) throws IOException
        {
            OutputStream out = null;

            VFS vfs = VFSManager.getVFSForPath(savePath);

            // we need to know the length of ZIP entries in advance.
            long length = new File(outputFile).length();

            try {
                out = vfs._createOutputStream(null,savePath,comp);
                out = new ZipOutputStream(out);

                ZipOutputStream archiveOut = (ZipOutputStream)out;

                boolean saved = false;

                for(;;) {
                    ZipEntry next = archiveIn.getNextEntry();
                    if(next == null)
                        break;

                    Log.log(Log.DEBUG,this,"Copy entry " + next);
                    archiveOut.putNextEntry(next);

                    if(next.getName().equals(archive.entryName)) {
                        next.setSize(length);
                        copy(outputFile,archiveOut);
                        saved = true;
                    } else {
                        copy(archiveIn,archiveOut);
                    }
                    archiveOut.closeEntry();
                }

                if(!saved) {
                    // new entry
                    ZipEntry newEntry = new ZipEntry(archive.entryName);
                    newEntry.setSize(length);
                    archiveOut.putNextEntry(newEntry);
                    copy(outputFile,archiveOut);
                    saved = true;
                    archiveOut.closeEntry();
                }
            } finally {
                if(out != null)
                    out.close();
            }
        }

        /**
         * Copy entries from archive.pathName to savePath; replace the
         * entry at archive.entryName with contents of outputFile.
         */
        private void saveTarArchive(TarInputStream archiveIn,
            ArchivePath archive, String savePath,
            String outputFile, Component comp,
            boolean gzip, boolean bzip2) throws IOException
        {
            OutputStream out = null;

            VFS vfs = VFSManager.getVFSForPath(savePath);

            // we need to know the length of TAR entries in advance.
            long length = new File(outputFile).length();

            try {
                out = vfs._createOutputStream(null,savePath,comp);

                if(gzip)
                    out = new GZIPOutputStream(out);
                if(bzip2)
                    out = new CBZip2OutputStream(out);

                out = new TarOutputStream(out);

                TarOutputStream archiveOut = (TarOutputStream)out;

                boolean saved = false;

                for(;;) {
                    TarEntry next = archiveIn.getNextEntry();
                    if(next == null)
                        break;

                    Log.log(Log.DEBUG,this,"Copy entry " + next);

                    if(next.getName().equals(archive.entryName)) {
                        next.setSize(length);
                        archiveOut.putNextEntry(next);
                        copy(outputFile,archiveOut);
                        saved = true;
                    } else {
                        archiveOut.putNextEntry(next);
                        copy(archiveIn,archiveOut);
                    }
                    archiveOut.closeEntry();
                }

                if(!saved) {
                    // new entry
                    TarEntry newEntry = new TarEntry(archive.entryName);
                    newEntry.setSize(length);
                    archiveOut.putNextEntry(newEntry);
                    copy(outputFile,archiveOut);
                    archiveOut.closeEntry();
                    saved = true;
                }
            } finally {
                if(out != null)
                    out.close();
            }
        }

        /**
         * Copies the contents of an input stream to an output stream.
         */
        public static void copy(InputStream in, OutputStream out)
            throws IOException
        {
            byte[] buf = new byte[4096];

            int count;

            for(;;)
            {
                count = in.read(buf,0,buf.length);
                if(count == -1 || count == 0)
                    break;

                out.write(buf,0,count);
            }
        }

        /**
         * Copies the contents of a file to an output stream.
         */
        public static void copy(String inFile, OutputStream out)
            throws IOException
        {
            InputStream in = new BufferedInputStream(
                new FileInputStream(inFile));
            try {
                copy(in,out);
            } finally {
                if(in != null)
                    in.close();
            }
        }
    }
}

