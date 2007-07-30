/*
 * ArchiveEntry.java
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
import java.util.zip.ZipEntry;


public class ArchiveEntry {
    private String  name      = "";
    private long    size      = -1;
    private boolean directory = false;


    public ArchiveEntry(TarEntry entry) {
        this.name      = entry.getName();
        this.size      = entry.getSize();
        this.directory = entry.isDirectory();
    }


    public ArchiveEntry(ZipEntry entry) {
        this.name      = entry.getName();
        this.size      = entry.getSize();
        this.directory = entry.isDirectory();
    }


    public String getName() {
        return this.name;
    }


    public long getSize() {
        return this.size;
    }


    public boolean isDirectory() {
        return this.directory;
    }
}
