/*
 * JasminVFS.java
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


package javainsight;


import de.fub.bytecode.classfile.ClassParser;
import de.fub.bytecode.classfile.JavaClass;

import JasminVisitor;

import java.awt.Component;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import java.util.Vector;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public class JasminVFS extends VFS {
    public static final String PROTOCOL = "jasmin";


    public JasminVFS() {
        super("jasmin");
    }


    public int getCapabilities()
    {
        return (
              VFS.BROWSE_CAP
            | VFS.READ_CAP
        //  | WRITE_CAP
        );
    }


    public char getFileSeparator() {
        return File.separatorChar;
    }


    public String getParentOfPath(String path) {
        if (path.startsWith(PROTOCOL + ':')) {
            String clazzPath = path.substring((PROTOCOL + ':').length());
            VFS vfs = VFSManager.getVFSForPath(clazzPath);

            return PROTOCOL + ':' + vfs.getParentOfPath(clazzPath);
        } else {
            VFS vfs = VFSManager.getVFSForPath(path);

            return vfs.getParentOfPath(path);
        }
    }


    public String showBrowseDialog(Object[] session, Component comp) {
        VFSBrowser browser = (VFSBrowser) comp;

        VFS.DirectoryEntry[] selected = browser.getSelectedFiles();
        if (selected == null || selected.length != 1) {
            // TODO: error message
            browser.getView().getToolkit().beep();
            return null;
        }

        VFS.DirectoryEntry entry = selected[0];
        if (entry.type == VFS.DirectoryEntry.FILE) {
            VFS vfs = VFSManager.getVFSForPath(entry.path);
            jEdit.openFile(browser.getView(), PROTOCOL + ':' + entry.path);
            return vfs.getParentOfPath(entry.path);
        }

        return entry.path;
    }


    public VFS.DirectoryEntry[] _listDirectory(Object session, String path,
        Component comp)
    {
        Log.log(Log.DEBUG, this, "_listDirectory Path: " + path);
        String clazzPath = path;
        if (path.startsWith(PROTOCOL + ':')) {
            clazzPath = clazzPath.substring(PROTOCOL.length() + 1);
        }
        Log.log(Log.DEBUG, this, "_listDirectory clazz Path: [" + clazzPath + "]");

        VFS vfs = VFSManager.getVFSForPath(clazzPath);

        try {
            VFS.DirectoryEntry[] directoryEntries =
                vfs._listDirectory(session, clazzPath, comp);

            if (directoryEntries == null) {
                return null;
            }

            Vector v = new Vector();

            for (int i = 0; i < directoryEntries.length; i++) {
                if (   directoryEntries[i].path.endsWith(".class")
                    || (directoryEntries[i].type == VFS.DirectoryEntry.DIRECTORY)
                ) {
                    v.addElement(
                        new VFS.DirectoryEntry(
                            directoryEntries[i].name,
                            PROTOCOL + ':' + directoryEntries[i].path,
                            PROTOCOL + ':' + directoryEntries[i].deletePath,
                            directoryEntries[i].type,
                            directoryEntries[i].length,
                            directoryEntries[i].hidden
                        )
                    );
                }
            }
            VFS.DirectoryEntry[] retVal = new VFS.DirectoryEntry[v.size()];
            v.copyInto(retVal);

            return retVal;
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }

        return null;
    }


    public DirectoryEntry _getDirectoryEntry(Object session, String path,
        Component comp)
    {
        Log.log(Log.DEBUG, this, "_getDirectoryEntry Path: " + path);
        String clazzPath = path;
        if (path.startsWith(PROTOCOL + ':')) {
            clazzPath = clazzPath.substring(PROTOCOL.length() + 1);
        }
        Log.log(Log.DEBUG, this, "_getDirectoryEntry Clazz Path: [" + clazzPath + "]");

        VFS vfs = VFSManager.getVFSForPath(clazzPath);

        try {
            VFS.DirectoryEntry directoryEntry =
                vfs._getDirectoryEntry(session, clazzPath, comp);

            if (directoryEntry == null) {
                return null;
            }

            return new VFS.DirectoryEntry(
                directoryEntry.name,
                PROTOCOL + ':' + directoryEntry.path,
                PROTOCOL + ':' + directoryEntry.deletePath,
                directoryEntry.type,
                directoryEntry.length,
                directoryEntry.hidden
            );
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        }

        return null;
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
        Log.log(Log.DEBUG, this, "_createInputStream Path: " + path);
        String clazzPath = path;
        if (path.startsWith(PROTOCOL + ':')) {
            clazzPath = clazzPath.substring(PROTOCOL.length() + 1);
        }
        Log.log(Log.DEBUG, this, "_createInputStream clazz Path: [" + clazzPath + "]");

        VFS vfs = VFSManager.getVFSForPath(clazzPath);

        boolean code      = true;
        boolean constants = true;
        boolean verbose   = true;

        if (clazzPath.endsWith(".marks")) {
            return vfs._createInputStream(session, clazzPath, ignoreErrors, comp);
        }

        try {
            InputStream in = vfs._createInputStream(session, clazzPath, ignoreErrors, comp);

            JavaClass java_class = new ClassParser(in, clazzPath).parse();

            ByteArrayOutputStream baOut = new ByteArrayOutputStream();
            OutputStream out = new BufferedOutputStream(baOut);

            new JasminVisitor(java_class, out).disassemble();

            out.close();

            return new BufferedInputStream(new ByteArrayInputStream(
                baOut.toByteArray()
            ));
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
        } catch (Exception e) {
            Log.log(Log.ERROR, this, e);
        }

        return null;
    }
}
