/*
 * TarCommand.java
 * Copyright (c) 2001, 2002 Andre Kaplan
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

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

import java.util.Hashtable;


public class TarCommand extends ArchiveCommand {
    private TarInputStream source;


    public TarCommand(TarInputStream source) {
        this.source = source;
    }

    public Hashtable getDirectories(String archiveProtocol, String archivePath)
            throws IOException
    {
        Hashtable directories = new Hashtable();

        TarInputStream in = this.source;

        for (TarEntry entry; (entry = in.getNextEntry()) != null; ) {
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


    public InputStream createInputStream(String path) throws IOException {
        TarInputStream in = this.source;

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
}
