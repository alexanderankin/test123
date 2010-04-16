/*
 * jEdit editor settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * JIndexUtilities.java
 * Copyright (C) 1999 Dirk Moebius
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
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;


public class JIndexUtilities {

    /**
     * the version of the Java Runtime Environment, as provided by the
     * system property "java.version".
     */
    public static final String JVERSION = System.getProperty("java.version");

    /**
     * returns the number of entries in a ZIP file.
     * On Java versions 1.2 or higher, the efficient method
     * <code>java.util.zip.ZipFile.size()</code> is used.
     * On older Java versions, which do not contain this
     * method, a slower implementation is used, that counts
     * the number of entries in the Enumeration.
     * @throws IllegalStateException if the zip file has been closed.
     */
    public static int zipFileSize(ZipFile f) throws IllegalStateException {
        if (JVERSION.compareTo("1.2") < 0)
            return zipFileSize11(f);
        else
            return zipFileSize12(f);
    }

    private static int zipFileSize11(ZipFile f) throws IllegalStateException {
        int num = 0;
        Enumeration e = f.entries();
        while (e.hasMoreElements()) {
            num++;
            e.nextElement();
        }
        return num;
    }

    private static final Object[] sizeMethodArgs = new Object[0];

    private static int zipFileSize12(ZipFile f) throws IllegalStateException {
        try {
            Method sizeMethod = ZipFile.class.getMethod("size", null);
            Object val = sizeMethod.invoke(f, sizeMethodArgs);
            return ((Integer)val).intValue();
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
        return 0;
    }

}

