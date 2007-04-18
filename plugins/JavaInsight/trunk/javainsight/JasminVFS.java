/*
 * JasminVFS.java
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


package javainsight;

import java.awt.Component;
import java.io.*;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public class JasminVFS extends ByteCodeVFS
{
    public static final String PROTOCOL = "jasmin";


    public JasminVFS() {
        super(PROTOCOL);
    }


    /**
     * Creates an input stream. This method is called from the I/O
     * thread.
     * @param session the VFS session
     * @param path The path
     * @param ignoreErrors If true, file not found errors should be
     * ignored
     * @param comp The component that will parent error dialog boxes
     */
    @Override
    public InputStream _createInputStream(Object session,
        String path, boolean ignoreErrors, Component comp)
    {
        String clazzPath = path;
        if (path.startsWith(PROTOCOL + ':')) {
            clazzPath = clazzPath.substring(PROTOCOL.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(clazzPath);

        if (clazzPath.endsWith(".marks")) {
            return null;
        }

        try {
            InputStream in = vfs._createInputStream(session, clazzPath, ignoreErrors, comp);

            JavaClass java_class = new ClassParser(in, clazzPath).parse();

            StringWriter out = new StringWriter();

            new JasminVisitor(java_class, out).disassemble();

            out.close();

            return new BufferedInputStream(new ByteArrayInputStream(
                out.toString().replace("\r", "").getBytes()));
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        } catch (Exception e) {
            Log.log(Log.ERROR, this, e);
        }

        return null;
    }
}
