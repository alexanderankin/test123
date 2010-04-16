/*
 * jEdit editor settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * LibEntry.java
 * Copyright (C) 1999 2000 Dirk Moebius
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

package jindex;

import java.util.zip.ZipFile;

public class LibEntry {

    public static final int PUBLIC = 1;
    public static final int PROTECTED = 2;
    public static final int PACKAGE = 3;
    public static final int PRIVATE = 4;

    public String lib = "";
    public String doc = "";
    public boolean isOldJavaDoc = false;
    public int visibility = PROTECTED;

    private ZipFile f = null;
    private String oldlib = "$$$";

    public LibEntry() {}

    public LibEntry(String lib,
                    String doc,
                    boolean isOldJavaDoc,
                    int visibility) {
        this.lib = lib;
        this.doc = doc;
        this.isOldJavaDoc = isOldJavaDoc;
        this.visibility = visibility;
    }

    /** for debugging purposes only */
    public String toString() {
        return doc + " oldjdoc=" + isOldJavaDoc;
    }


    public ZipFile getLibFile() {
        if (f == null || !lib.equals(oldlib)) {
            if (f != null) {
                try {
                    f.close();
                }
                catch (java.io.IOException e) { }
            }
            try {
                f = new ZipFile(lib);
                oldlib = lib;
            }
            catch (java.io.IOException e) {
                f = null;
            }
        }
        return f;
    }

    protected void finalize() throws java.io.IOException {
        if (f != null) {
            f.close();
        }
    }
}
