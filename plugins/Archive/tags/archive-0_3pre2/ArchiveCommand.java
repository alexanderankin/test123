/*
 * ArchiveCommand.java
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


import com.ice.tar.TarInputStream;

import java.io.IOException;
import java.io.InputStream;

import java.util.Hashtable;

import java.util.zip.ZipInputStream;


public abstract class ArchiveCommand {
    abstract Hashtable getDirectories(String archiveProtocol, String archivePath)
        throws IOException;


    abstract ArchiveEntry getDirectoryEntry(String path) throws IOException;


    abstract InputStream createInputStream(String path) throws IOException;


    public static ArchiveCommand getCommand(InputStream in) {
        if (in instanceof TarInputStream) {
            return new TarCommand((TarInputStream) in);
        } else if (in instanceof ZipInputStream) {
            return new ZipCommand((ZipInputStream) in);
        } else {}

        return null;
    }
}
