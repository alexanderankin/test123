/*
 * JCompiler.java - a wrapper around sun.tools.javac.Main
 * (c) 1999, 2000 - Kevin A. Burton and Aziz Sharif
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

package jcompiler;

import java.lang.reflect.*;
import java.util.Vector;
import java.io.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
import buildtools.*;


/**
 * The class that performs the javac compile run.
 */ 
public class JCompiler {

    // on first initialization, set a new SecurityManager. Note, that
    // on JDK 1.1.x a SecurityManager can only be set _once_.
    private static NoExitSecurityManager sm = NoExitSecurityManager.getInstance();
    static {
        try {
            System.setSecurityManager(sm);
        }
        catch (SecurityException secex) {
            Log.log(Log.ERROR, JCompiler.class, 
                "Could not set new SecurityManager. Sorry.");
        }
    }

    
    private PipedOutputStream pipe = null;
    private Method compilerMethod = null;


    public JCompiler() {
        pipe = new PipedOutputStream();
    }
  

    /**
     * compile a file with sun.tools.javac.Main.
     *
     * @param view        the view, where error dialogs should go
     * @param buf         the buffer containing the file to be compiled
     * @param pkgCompile  if true, JCompiler tries to locate the base directory
     *                    of the package of the current file and compiles 
     *                    every outdated file.
     * @param rebuild     if true, JCompiler compiles <i>every</i> file in the
     *                    package hierarchy.
     */
    public void compile(View view,
                        Buffer buf,    
                        boolean pkgCompile,
                        boolean rebuild) 
    {
        // Check whether current buffer is a java file
        String filename = buf.getPath();
        if (filename == null || filename.equals("") || !filename.endsWith(".java")) {
            JOptionPane.showMessageDialog(view, 
                "The current buffer can not be compiled as it is not a java file.", 
                "Nothing to Compile",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Search for the compiler method
        if (compilerMethod == null) {
            String className = jEdit.getProperty("jcompiler.compiler.class");
            String methodName = jEdit.getProperty("jcompiler.compiler.method"); 
            try {
                Class compilerClass = Class.forName(className);
                String[] stringarray = new String[] {};
                Class stringarrayclass = stringarray.getClass();
                Class[] formalparams = new Class[] { stringarrayclass };
                compilerMethod = compilerClass.getMethod(methodName, formalparams);
            }
            catch (Exception e) {
                Log.log(Log.ERROR, this, e);
                e.printStackTrace();
                JOptionPane.showMessageDialog(view,
                    "Java compiler method '"
                    + className + "." + methodName
                    + "' not found.\n"
                    + "Please ensure that $CLASSPATH is set correctly.\n\n"
                    + "If you use JDK 1.2, make sure that tools.jar is in the path.\n"
                    + "For more information look at the documentation.",
                    "Compiler class not found",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Check output directory:
        String outDirPath = null;
        if (jEdit.getBooleanProperty( "jcompiler.specifyoutputdirectory")) {
            File outDir = new File(jEdit.getProperty("jcompiler.outputdirectory"));
            try {
                outDirPath = outDir.getCanonicalPath();
            }
            catch (IOException ioex) {
                JOptionPane.showMessageDialog(view,
                    "Error resolving class output directory\n"
                    + outDirPath + "\n:"
                    + ioex.toString(),
                    "JCompiler Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (outDir.exists()) {
                if (!outDir.isDirectory()) {
                    JOptionPane.showMessageDialog(view,
                        "The directory for class files\n"
                        + outDirPath + "\n"
                        + "is not a directory.",
                        "JCompiler Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                int reply = JOptionPane.showConfirmDialog(view,
                    "The directory for class files\n"
                    + outDirPath + "\n"
                    + " does not exist.\n"
                    + "Do you wish to create it now?",
                    "Create Output Directory?", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (reply != JOptionPane.YES_OPTION) {
                    return;
                }
                if (!outDir.mkdirs()) {
                    JOptionPane.showMessageDialog(view,
                        "Error creating output directory.",
                        "JCompiler Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        // Check for auto save / auto save all
        boolean autoSaveBuf = false;
        boolean autoSaveAll = false;        
        if (pkgCompile) {
            autoSaveBuf = jEdit.getProperty(
                "jcompiler.javapkgcompile.autosave").equals("current");
            autoSaveAll = jEdit.getProperty(
                "jcompiler.javapkgcompile.autosave").equals("all");
        } else {
            autoSaveBuf = jEdit.getProperty(
                "jcompiler.javacompile.autosave").equals("current");
            autoSaveAll = jEdit.getProperty(
                "jcompiler.javacompile.autosave").equals("all");
        }
        if (autoSaveAll) {
            // Save all buffers.
            // This code is copied from org/gjt/sp/jedit/actions/save_all.java:
            Buffer[] buffers = jEdit.getBuffers();
            for(int i = 0; i < buffers.length; i++) {
                Buffer buffer = buffers[i];
                if (buffer.isDirty()) {
                    buffer.save(view, null);
                }
            }
        } else {
            if (autoSaveBuf) {
                if (buf.isDirty()) {
                    buf.save(view, null);
                }
            } else {
                if (buf.isDirty()) {
                    int result = JOptionPane.showConfirmDialog(view,
                        "Save changes to " + buf.getName() + "?",
                        "File Not Saved",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (result == JOptionPane.CANCEL_OPTION) {
                        return;
                    } else if (result == JOptionPane.YES_OPTION) {
                        buf.save(view, null);
                    }
                }             
            }
        }
  
        // Construct arguments for javac
        String[] args = new String[2];
        String[] files = null;
        File ff = new File(filename);
        String parent = ff.getParent();
        if (parent != null && pkgCompile == true) {
            String dir;
            try {
                dir = JavaUtils.getBaseDirectory(ff.getAbsolutePath());
            }
            catch (IOException ioex) {
                Log.log(Log.ERROR, "JCompiler",
                    "couldn't get base directory of file "
                    + filename + ": " + ioex.toString());
                dir = parent;
            }
            String exts[] = { "java" };
            FileChangeMonitor monitor = new FileChangeMonitor(dir, exts);
            String list[];
            if (rebuild) {
                list = monitor.getAllFiles();
            } else {
                list = monitor.getChangedFiles();
            }
            if (list.length == 0) {
                sendMessage("jcompiler.msg.nofiles", new Object[] { dir });
                return;
            }
            sendMessage("jcompiler.msg.compilefiles", new Object[] {
                new Integer(list.length), dir
            });
            files = list;
        } else {
            sendMessage("jcompiler.msg.compilefile", new Object[] { filename });
            files = new String[] { filename };
        }
  
        // CLASSPATH setting:
        args[0] = "-classpath";
        if (jEdit.getBooleanProperty("jcompiler.usejavacp")) {
            args[1] = System.getProperty("java.class.path");
        } else {
            args[1] = jEdit.getProperty("jcompiler.classpath");
        }
        // check if package dir should be added to classpath:
        if (jEdit.getBooleanProperty("jcompiler.addpkg2cp")) {
            try {
                String pkgName = buildtools.JavaUtils.getPackageName(filename);
                // If no package stmt found then pkgName would be null
                if (parent != null && pkgName == null) {
                    args[1] = args[1] + System.getProperty("path.separator") 
                              + parent;
                }
                else if (parent != null && pkgName != null) {
                    String pkgPath = pkgName.replace('.', 
                        System.getProperty("file.separator").charAt(0));
                    if (parent.endsWith(pkgPath)) {
                        parent = parent.substring(0, 
                            parent.length() - pkgPath.length() - 1);
                        args[1] = args[1] + System.getProperty("path.separator") 
                            + parent;
                    }
                }
            } 
            catch (Exception exp) {
                exp.printStackTrace();
            }
        }
  
        Vector vectorArgs = new Vector();      
        vectorArgs.addElement(args[0]);
        vectorArgs.addElement(args[1]);

        // Check deprecated flag:
        if (jEdit.getBooleanProperty("jcompiler.showdeprecated")) {
            vectorArgs.addElement("-deprecation");
        }

        // Check output directory:
        if (jEdit.getBooleanProperty("jcompiler.specifyoutputdirectory")
            && outDirPath != null) {
            vectorArgs.addElement("-d");
            vectorArgs.addElement(outDirPath);
        }
  
        // Now add the files...
        for (int i = 0; i < files.length; ++i) {
            vectorArgs.addElement(files[i]);      
        }
  
        String[] arguments = new String[vectorArgs.size()];
        vectorArgs.copyInto(arguments);

        // Start the javac compiler...
        PrintStream origOut = System.out;
        PrintStream origErr = System.err;
        try {
            PrintStream ps = new PrintStream(pipe);
            System.setOut(ps);
            System.setErr(ps);
            // set "no exit" security manager to prevent javac from exiting:
            if (sm != null) sm.setAllowExit(false);
            // now invoke the compiler method:
            Object methodParams[] = new Object[] { arguments };
            compilerMethod.invoke(null, methodParams);
        }
        catch (InvocationTargetException invex) {
            // the invoked method has thrown an exception
            if (invex.getTargetException() instanceof SecurityException) {
                // don't do anything here because sun.tools.javac.Main.main will 
                // always try and exit.
            } else {
                // oh my god, the method has thrown an exception, sheesh!
                Log.log(Log.ERROR, this,  
                    "The compiler method just threw a runtime exception. " + 
                    "Please report this to the current JCompiler maintainer."
                );
                invex.getTargetException().printStackTrace();
            }
        } 
        catch (IllegalArgumentException illargex) {
            illargex.printStackTrace();            
        }
        catch (IllegalAccessException illaccex) {
            illaccex.printStackTrace();
        }
        finally {
            // exit is allowed again
            if (sm != null) sm.setAllowExit(true);
            System.setOut(origOut);   
            System.setErr(origErr);
        }
        
        sendMessage("jcompiler.msg.done");
        
        try {
            pipe.close();
        }
        catch (IOException ioex) {
            // ignored
        }
            
        return;
    } // public void run()

    
    private void sendMessage(String property) {
        sendString(jEdit.getProperty(property));
    }


    private void sendMessage(String property, Object[] args) {
        sendString(jEdit.getProperty(property, args));
    }
    
    
    private void sendString(String msg) {
        Log.log(Log.DEBUG, this, msg);
        try {
            pipe.write(msg.getBytes());
        }
        catch (IOException ioex) {
            // ignored
        }
    }


    public PipedOutputStream getOutputPipe() {
        return pipe;
    }   
}

