/*
 * ArchiveUtilities.java
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
import java.io.PushbackInputStream;

import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

import com.aftexsw.util.bzip.CBZip2InputStream;

import com.ice.tar.TarInputStream;


public class ArchiveUtilities {
    // Adapted from com.ice.tartool.ArchiveTreePanel
    public static TarInputStream openTarInputStream(InputStream archive)
        throws IOException
    {
        // First, read four magic bytes. This is enough to decide if we
        // have a GZIP or ZIP archive. If so, we will wrap the input stream...

        PushbackInputStream in = new PushbackInputStream(archive, 4);
        InputStream zin = null;
        byte[] magic = new byte[4];
        int numRead = in.read(magic);

        if (numRead < magic.length) {
            throw new IOException("unexpected end of file reading "
                + magic.length + " 'magic' bytes"
            );
        }

        if (   (magic[0] == (byte) 0x1F)
            && (magic[1] == (byte) 0x8B)
        ) {
            in.unread(magic, 0, numRead);

            // This is a GZIP archive.
            zin = new GZIPInputStream(in);
        } else if (    (magic[0] == (byte) 'P')
                    && (magic[1] == (byte) 'K')
                    && (magic[2] == (byte) 0x03)
                    && (magic[3] == (byte) 0x04)
        ) {
            in.unread(magic, 0, numRead);

            // This is a ZIP archive.
            //
            // Open the archive as a ZIPInputStream, and
            // position to the first entry. We only handle
            // the case where the tar archive is the first
            // entry in the ZIP archive.
            zin = new ZipInputStream(in);
            ZipEntry zipEnt = ((ZipInputStream) zin).getNextEntry();
        } else if (    (magic[0] == (byte) 'B')
                    && (magic[1] == (byte) 'Z')
        ) {
            // We don't unread 'BZ' since CBZip2InputStream assumes
            // the stream begins right after
            in.unread(magic, 2, numRead - 2);

            // This is a BZIP2 archive
            zin = new CBZip2InputStream(in);
        } else {
            in.unread(magic, 0, numRead);

            zin = in;
        }

        return new TarInputStream(zin);
    }


    public static ZipInputStream openZipInputStream(InputStream archive)
        throws IOException
    {
        ZipInputStream zin = null;
        if (archive instanceof ZipInputStream) {
            zin = (ZipInputStream) archive;
        } else {
            zin = new ZipInputStream(archive);
        }

        return zin;
    }
}
