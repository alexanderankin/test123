/*
 * JodeVFS.java
 * Copyright (c) 2001, 2002 Andre Kaplan
 *
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javainsight.buildtools.JavaUtils;

import net.sf.jode.bytecode.ClassInfo;
import net.sf.jode.bytecode.ClassPath;
import net.sf.jode.decompiler.ClassAnalyzer;
import net.sf.jode.decompiler.Decompiler;
import net.sf.jode.decompiler.ImportHandler;
import net.sf.jode.decompiler.TabbedPrintWriter;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.FileVFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public class JodeVFS extends ByteCodeVFS {

    public static final String PROTOCOL = "jode";


    public JodeVFS() {
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

        if (clazzPath.endsWith(".marks")) {
            return null;
        }

        String className = null;

        try {
            // Get the class name from BCEL!
            DataInputStream in = new DataInputStream(new BufferedInputStream(
                vfs._createInputStream(session, clazzPath, ignoreErrors, comp)
            ));
            JavaClass java_class = new ClassParser(in, clazzPath).parse();
            className = java_class.getClassName();

            // Get the classpath
            String[] cp = JavaUtils.getClasspath();

            // Get the VFS path
            String vfsPath = null;
            int dotIdx = className.lastIndexOf('.');
            vfsPath = vfs.getParentOfPath(clazzPath);
            if (vfs != VFSManager.getVFSForPath(vfsPath)) {
                vfsPath = null;
            } else {
                while (dotIdx != -1) {
                    vfsPath = vfs.getParentOfPath(vfsPath);
                    if (vfs != VFSManager.getVFSForPath(vfsPath)) {
                        vfsPath = null;
                        break;
                    }
                    dotIdx = className.lastIndexOf('.', dotIdx - 1);
                }
            }

            Log.log(Log.DEBUG, this, "className=" + className
                + " vfsPath=" + vfsPath
                + " clazzPath=" + clazzPath
                + " classPath=" + Arrays.asList(cp));

            in = new DataInputStream(new BufferedInputStream(
                vfs._createInputStream(session, clazzPath, ignoreErrors, comp)
            ));

            // JODE is not thread-safe
            synchronized (this) {
                ClassPath classPath;
                if (vfsPath == null) {
                    classPath = new ClassPath(cp);
                } else {
                    classPath = new VFSSearchPath(cp, vfsPath);
                }
                ClassInfo clazz = classPath.getClassInfo(className);
                clazz.read(in, ClassInfo.ALL);

                boolean pretty  = jEdit.getBooleanProperty("javainsight.jode.pretty",  true);
                boolean onetime = jEdit.getBooleanProperty("javainsight.jode.onetime", false);
                boolean decrypt = jEdit.getBooleanProperty("javainsight.jode.decrypt", true);
                String  style   = jEdit.getProperty("javainsight.jode.style", "sun");

                // Setting decompiler options
                Decompiler decompiler = new Decompiler();
                decompiler.setOption("style",   style);
                decompiler.setOption("pretty",  pretty  ? "yes" : "no");
                decompiler.setOption("onetime", onetime ? "yes" : "no");
                decompiler.setOption("decrypt", decrypt ? "yes" : "no");

                int packageLimit = ImportHandler.DEFAULT_PACKAGE_LIMIT;
                int classLimit   = ImportHandler.DEFAULT_CLASS_LIMIT;;

                try {
                    String importPackageLimit = jEdit.getProperty("javainsight.jode.pkglimit", "0");
                    packageLimit = Integer.parseInt(importPackageLimit);
                } catch (NumberFormatException nfe) {}

                try {
                    String importClassLimit   = jEdit.getProperty("javainsight.jode.clslimit", "1");
                    classLimit = Integer.parseInt(importClassLimit);
                } catch (NumberFormatException nfe) {}

                ImportHandler imports = new ImportHandler(classPath, packageLimit, classLimit);

                ByteArrayOutputStream baOut = new ByteArrayOutputStream();
                TabbedPrintWriter writer = new TabbedPrintWriter(
                    new NewlineOutputFilter(new BufferedOutputStream(baOut)), imports, false
                );

                ClassAnalyzer clazzAna = new ClassAnalyzer(clazz, imports);

                clazzAna.dumpJavaFile(writer);

                writer.close();

                return new BufferedInputStream(new ByteArrayInputStream(
                    baOut.toByteArray()
                ));
            }
        } catch (IOException ioex) {
            throw ioex;
        } catch (Throwable t) {
            // Jode sometimes throws java.lang.InternalError or other
            // messy things:
            Log.log(Log.ERROR, this, t);
            throw new IOException("An error occured while decompiling " + className + ": " + t);
        }
    }
}
