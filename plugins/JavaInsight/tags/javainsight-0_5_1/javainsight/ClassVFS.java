/*
 * ClassVFS.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public class ClassVFS extends ByteCodeVFS {
    public static final String PROTOCOL = "class";


    public ClassVFS() {
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
     * @exception IOException If an I/O error occurs
     */
    public InputStream _createInputStream(Object session,
        String path, boolean ignoreErrors, Component comp)
        throws IOException
    {
        String clazzPath = path;
        if (path.startsWith(PROTOCOL + ':')) {
            clazzPath = clazzPath.substring(PROTOCOL.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(clazzPath);

        boolean code      = true;
        boolean constants = true;
        boolean verbose   = true;

        if (clazzPath.endsWith(".marks")) {
            return null;
        }

        try {
            InputStream in = vfs._createInputStream(session, clazzPath, ignoreErrors, comp);

            JavaClass java_class = new ClassParser(in, clazzPath).parse();

            StringBuffer sb = new StringBuffer();

            sb.append(java_class).append('\n');

            // Dump the constant pool
            if (constants) {
                sb.append(java_class.getConstantPool()).append('\n');
            }

            // Dump the method code
            if (code) {
                this.dumpCode(sb, java_class.getMethods(), verbose);
            }

            ByteArrayOutputStream baOut = new ByteArrayOutputStream();
            OutputStream out = new NewlineOutputFilter(new BufferedOutputStream(baOut));

            out.write(sb.toString().getBytes());
            out.close();

            return new ByteArrayInputStream(baOut.toByteArray());
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        } catch (Exception e) {
            Log.log(Log.ERROR, this, e);
        }

        return null;
    }


    private void dumpCode(StringBuffer sb, Method[] methods, boolean verbose) {
        for (int i = 0; i < methods.length; i++) {
            sb.append(methods[i]).append('\n');

            Code code = methods[i].getCode();
            if (code != null) {
                sb.append(code.toString(verbose)).append('\n');
            }
        }
    }
}
