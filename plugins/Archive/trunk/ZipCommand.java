/*
 * ZipCommand.java
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


import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ZipCommand implements ArchiveCommand {
    private ZipInputStream source;


    public ZipCommand(ZipInputStream source) {
        this.source = source;
    }

    public Hashtable getDirectories(String archiveProtocol, String archivePath)
            throws IOException
    {
        Hashtable directories = new Hashtable();

        ZipInputStream in = this.source;

        for (ZipEntry entry; (entry = in.getNextEntry()) != null; ) {
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


    public ArchiveEntry getDirectoryEntry(String path) throws IOException {
        ZipInputStream in = this.source;

        for (ZipEntry entry = null; (entry = in.getNextEntry()) != null; ) {
            if (path.equals(entry.getName())) {
                return new ArchiveEntry(entry);
            }
        }

        return null;
    }


    public InputStream createInputStream(String path) throws IOException {
        ZipInputStream in = this.source;

        for (ZipEntry entry; (entry = in.getNextEntry()) != null; ) {
            if (entry.getName().equals(path)) {
                return new BufferedInputStream(in);
            }
        }

        return null;
    }
}
