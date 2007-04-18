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
import java.io.*;
import java.util.ArrayList;

import javainsight.buildtools.ChainedIOException;
import javainsight.buildtools.JavaUtils;
import net.sf.jode.GlobalOptions;
import net.sf.jode.bytecode.ClassInfo;
import net.sf.jode.bytecode.ClassPath;
import net.sf.jode.decompiler.ClassAnalyzer;
import net.sf.jode.decompiler.ImportHandler;
import net.sf.jode.decompiler.Options;
import net.sf.jode.decompiler.TabbedPrintWriter;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;


public class JodeVFS extends ByteCodeVFS
{
    public static final String PROTOCOL = "jode";

    private static final Object mutex = new Object();


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
    @Override
    public InputStream _createInputStream(Object session,
        String path, boolean ignoreErrors, Component comp)
        throws IOException
    {
        String pathToClass = path;
        if (path.startsWith(PROTOCOL + ':')) {
            pathToClass = pathToClass.substring(PROTOCOL.length() + 1);
        }

        VFS vfs = VFSManager.getVFSForPath(pathToClass);

        if (pathToClass.endsWith(".marks")) {
            return null;
        }

        String className = null;

        try {
            // Get the class name from BCEL!
            DataInputStream in = new DataInputStream(new BufferedInputStream(
                vfs._createInputStream(session, pathToClass, ignoreErrors, comp)
            ));
            JavaClass java_class = new ClassParser(in, pathToClass).parse();
            className = java_class.getClassName();
            in.close();

            // Get the VFS path
            String vfsPath = vfs.getParentOfPath(pathToClass);
            if (vfs != VFSManager.getVFSForPath(vfsPath)) {
                vfsPath = null;
            } else {
                int dotIdx = className.lastIndexOf('.');
                while (dotIdx != -1) {
                    vfsPath = vfs.getParentOfPath(vfsPath);
                    if (vfs != VFSManager.getVFSForPath(vfsPath)) {
                        vfsPath = null;
                        break;
                    }
                    dotIdx = className.lastIndexOf('.', dotIdx - 1);
                }
            }

            ClassPath classPath = createJodeClasspath(vfsPath);

            Log.log(Log.DEBUG, this, 
                "className=" + className
                + " vfsPath=" + vfsPath
                + " pathToClass=" + pathToClass
                + " classPath=" + classPath);

            in = new DataInputStream(new BufferedInputStream(
                vfs._createInputStream(session, pathToClass, ignoreErrors, comp)
            ));

            ClassInfo clazz = classPath.getClassInfo(className);
            clazz.read(in, ClassInfo.ALL);
            
            ByteArrayOutputStream baOut = new ByteArrayOutputStream();
            OutputStreamWriter output = new OutputStreamWriter(baOut);
            decompile(className, classPath, output);
            output.close();
            
            return new BufferedInputStream(new ByteArrayInputStream(baOut.toByteArray()));
        } catch (IOException ioex) {
            throw ioex;
        } catch (Throwable t) {
            // Jode sometimes throws java.lang.InternalError or other
            // messy things:
            throw new ChainedIOException("An error occured while decompiling " + className, t);
        }
    }


    private ClassPath createJodeClasspath(String vfsPath) throws SecurityException, IOException {
        ArrayList<ClassPath.Location> locations = new ArrayList<ClassPath.Location>();
        for(String cp : JavaUtils.getClasspath()) {
            locations.add(ClassPath.createLocation(cp));
        }
        if(vfsPath != null) {
            locations.add(new VFSLocation(vfsPath));
        }
        return new ClassPath(locations.toArray(new ClassPath.Location[locations.size()]));
    }


    private void decompile(String className, ClassPath classPath, Writer decompilerOutput) throws IOException {
        String style = jEdit.getProperty("javainsight.jode.style", "sun");
        boolean pretty = jEdit.getBooleanProperty("javainsight.jode.pretty", true);
        boolean onetime = jEdit.getBooleanProperty("javainsight.jode.onetime", false);
        boolean decrypt = jEdit.getBooleanProperty("javainsight.jode.decrypt", true);
        
        int importPkgLimit = 0;
        try { 
            importPkgLimit = Integer.parseInt(jEdit.getProperty("javainsight.jode.pkglimit", "0"));
        } catch(NumberFormatException ex) {}
        if(importPkgLimit == 0)
            importPkgLimit = Integer.MAX_VALUE;
        
        int importClassLimit = 1;
        try {
            importClassLimit = Integer.parseInt(jEdit.getProperty("javainsight.jode.clslimit", "1"));
        } catch(NumberFormatException ex) {}
        if(importClassLimit == 0)
            importClassLimit = Integer.MAX_VALUE;
        
        Mode mode = jEdit.getMode("java");
        String wrapMode = mode.getProperty("wrap").toString(); // "node", "soft", "hard"
        int maxLineLen = (Integer) mode.getProperty("maxLineLen"); // 0 means no max. line len
        int tabSize = (Integer) mode.getProperty("tabSize");
        int indentSize = (Integer) mode.getProperty("indentSize");
        boolean noTabs = mode.getBooleanProperty("noTabs");

        if(!wrapMode.equals("hard") || maxLineLen == 0)
            maxLineLen = 1000;
        
        if(noTabs)
            tabSize = 0;
        
        int outputStyle = 0; // default for style "pascal"
        if(style.equals("gnu"))
            outputStyle = TabbedPrintWriter.GNU_SPACING | TabbedPrintWriter.INDENT_BRACES;
        else if(style.equals("sun"))
            outputStyle = TabbedPrintWriter.BRACE_AT_EOL;

        if(pretty)
            Options.options &= ~Options.OPTION_PRETTY;
        else
            Options.options |= Options.OPTION_PRETTY;
        
        if(onetime)
            Options.options &= ~Options.OPTION_ONETIME;
        else
            Options.options |= Options.OPTION_ONETIME;
        
        if(decrypt)
            Options.options &= ~Options.OPTION_DECRYPT;
        else
            Options.options |= Options.OPTION_DECRYPT;
        
        StringWriter errorOutput = new StringWriter();
        GlobalOptions.err = new PrintWriter(errorOutput);

        // JODE is not thread-safe
        synchronized (mutex) {
            ImportHandler importHandler = new ImportHandler(classPath, importPkgLimit, importClassLimit);
            TabbedPrintWriter tabbedPrintWriter = new TabbedPrintWriter(decompilerOutput,
                    importHandler, true, outputStyle, indentSize, tabSize, maxLineLen);
            ClassInfo clazz = classPath.getClassInfo(className);
            ClassAnalyzer clazzAnalyzer = new ClassAnalyzer(clazz, importHandler);
            clazzAnalyzer.dumpJavaFile(tabbedPrintWriter);
            tabbedPrintWriter.close();
            errorOutput.close();
        }
        
        GlobalOptions.err = null;
        
        String errorMessages = errorOutput.toString();
        if(errorMessages != null && errorMessages.trim().length() > 0) {
            Log.log(Log.ERROR, this, "Error decompiling class " + className + ":\n" + errorMessages);
        }
    }

}
