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
    public static InputStream openCompressedStream(InputStream source)
            throws IOException
    {
        // First, read first two magic bytes. This is enough to decide if we
        // have a GZIP or ZIP archive. If so, we will wrap the input stream...

        int pushbackLen = 2;
        PushbackInputStream pushback = new PushbackInputStream(source, pushbackLen);
        byte[] magic = new byte[pushbackLen];
        int numRead = pushback.read(magic);

        InputStream dest = pushback;

        if (numRead < pushbackLen) {
            pushback.unread(magic, 0, numRead);
            return dest;
        }

        if (   (magic[0] == (byte) 0x1F) // \037
            && (magic[1] == (byte) 0x8B) // \213
        ) {
            pushback.unread(magic, 0, numRead);

            // This is a GZIP archive.
            dest = new GZIPInputStream(dest);
        } else if (    (magic[0] == (byte) 'B')
                    && (magic[1] == (byte) 'Z')
        ) {
            // We don't unread 'BZ' since CBZip2InputStream assumes
            // the stream begins right after
            if ((numRead - pushbackLen ) > 0) {
                pushback.unread(magic, pushbackLen, numRead - pushbackLen);
            }

            // This is a BZIP2 archive
            dest = new CBZip2InputStream(dest);
        } else {
            pushback.unread(magic, 0, numRead);
        }

        return dest;
    }


    public static InputStream openArchiveStream(InputStream source)
            throws IOException
    {
        // First, read first four magic bytes. This is enough to decide if we
        // have a ZIP archive. If so, we will wrap the input stream...

        // From /etc/magic:

        // # ZIP archives (Greg Roelofs, c/o zip-bugs@wkuvx1.wku.edu)
        // 0    string      PK\003\004      Zip archive data
        // >4   byte        0x09            \b, at least v0.9 to extract
        // >4   byte        0x0a            \b, at least v1.0 to extract
        // >4   byte        0x0b            \b, at least v1.1 to extract
        // >4   byte        0x14            \b, at least v2.0 to extract

        // # POSIX tar archives
        // 257  string      ustar\0         POSIX tar archive
        // 257  string      ustar\040\040\0 GNU tar archive

        // We need the first 4 bytes to determine if we have a zip stream
        int zipMagicLen = 4;
        // We need the first 257 + 8 bytes to determine if we have a tar stream
        int tarMagicLen = 257 + 8;
        // PushbackInputStream length: Math.max(4, 257 + 8)
        int pushbackLen = Math.max(zipMagicLen, tarMagicLen);

        byte[] magic = null;
        int numRead  = -1;

        InputStream dest = new PushbackInputStream(source, pushbackLen);

        {
            PushbackInputStream pushback = (PushbackInputStream) dest;

            magic   = new byte[pushbackLen];
            numRead = pushback.read(magic);
            if (numRead > 0) {
                pushback.unread(magic, 0, numRead);
            }
        }

        // Guess whether source is a zip stream
        if (numRead < zipMagicLen) {
            return dest;
        }

        if (    (magic[0] == (byte) 'P')
             && (magic[1] == (byte) 'K')
             && (magic[2] == (byte) 0x03)
             && (magic[3] == (byte) 0x04)
        ) {
            dest = new ZipInputStream(dest);
            return dest;
        } else {}

        // Guess whether source is a tar stream
        if (numRead < tarMagicLen) {
            return dest;
        }

        int off = 257;
        if (    (magic[off    ] == (byte) 'u')
             && (magic[off + 1] == (byte) 's')
             && (magic[off + 2] == (byte) 't')
             && (magic[off + 3] == (byte) 'a')
             && (magic[off + 4] == (byte) 'r')
        ) {
            // Looks good until here...
            if (magic[off + 5] == (byte) '\0') {
                dest = new TarInputStream(dest);
                return dest;
            } else if (    (magic[off + 5] == (byte) '\040')
                        && (magic[off + 6] == (byte) '\040')
                        && (magic[off + 7] == (byte) '\0')
            ) {
                dest = new TarInputStream(dest);
                return dest;
            } else {}
        } else {}

        return dest;
    }
}
