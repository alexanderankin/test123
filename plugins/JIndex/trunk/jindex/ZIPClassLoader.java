/*
 * jEdit editor settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * ZIPClassLoader.java - Loads classes from ZIP or JAR files
 * Copyright (C) 1999 Dirk Moebius
 * Portions copyright (C) 1999 Slava Pestov, mike dillon
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

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;


/**
 * A class loader implementation that loads classes from ZIP or JAR files.
 * @author Dirk Moebius
 * @author Slava Pestov
 * @author mike dillon
 */
public class ZIPClassLoader extends ClassLoader
{
    // private members
    private ZipFile zipFile;


    public ZIPClassLoader(ZipFile zipFile) {
        super();
        this.zipFile = zipFile;
    }


    public Class loadClass(String clazzname) throws ClassNotFoundException {
        return loadClass(clazzname, true);
    }


    public Class loadClass(String clazzname, boolean resolveIt)
    throws ClassNotFoundException {
        // If already loaded, then return the loaded class
        Class cls = findLoadedClass(clazzname);
        if (cls != null) {
            if (resolveIt)
                resolveClass(cls);
            return cls;
        }

        // Defer to whoever loaded us (such as JShell, Echidna, etc)
        ClassLoader loader = this.getClass().getClassLoader();
        if (loader != null) {
            try {
                cls = loader.loadClass(clazzname);
                if (cls != null) {
                    if (resolveIt)
                        resolveClass(cls);
                    return cls;
                }
            }
            catch (ClassNotFoundException e) { }
        }

        // Doesn't exist yet, look in system classes
        try {
            cls = findSystemClass(clazzname);
            if (cls != null) {
                if (resolveIt)
                    resolveClass(cls);
                return cls;
            }
        }
        catch (ClassNotFoundException e) { }

        String filename = MiscUtilities.classToFile(clazzname);

        try {
            ZipEntry entry = zipFile.getEntry(filename);
            if (entry == null) return null;
            InputStream in = zipFile.getInputStream(entry);
            int len = (int) entry.getSize();
            byte[] data = new byte[len];
            int success = 0;
            int offset = 0;

            while (success < len) {
                len -= success;
                offset += success;
                success = in.read(data, offset, len);
                if (success == -1) {
                    String[] args = { clazzname, zipFile.getName() };
                    System.err.println(jEdit.getProperty(
                        "jar.error.zip", args));
                    throw new ClassNotFoundException(clazzname);
                }
            }

            cls = defineClass(clazzname, data, 0, data.length);

            if (resolveIt)
                resolveClass(cls);

            return cls;
        }
        catch (IOException io) {
            System.err.println("I/O error:");
            io.printStackTrace();
            throw new ClassNotFoundException(clazzname);
        }
    }


    public URL getResource(String name) {
        try {
            return new URL(getResourceAsPath(name));
        }
        catch(MalformedURLException mu) {
            return null;
        }
    }

    private String getResourceAsPath(String name) {
        if (zipFile.getName().toLowerCase().endsWith(".jar"))
            return "jar:" + zipFile.getName() + "#" + name;
        else if (zipFile.getName().toLowerCase().endsWith(".zip"))
            return "zip:" + zipFile.getName() + "#" + name;
        else
            return "";
    }


    public InputStream getResourceAsStream(String name) {
        try {
            ZipEntry entry = zipFile.getEntry(name);
            if (entry == null)
                return getSystemResourceAsStream(name);
            else
                return zipFile.getInputStream(entry);
        }
        catch(IOException io) {
            System.err.println("I/O error:");
            io.printStackTrace();
            return null;
        }
    }
}

