/*
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

package net.sourceforge.jedit.jcompiler;


//jEdit interface
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

//GUI support
import javax.swing.*;
import java.awt.event.ActionEvent;

//build support
import net.sourceforge.jedit.buildtools.*;
import net.sourceforge.jedit.buildtools.msg.*;


//standard java stuff
import java.util.Vector;
import java.io.*;
import java.lang.*;

import net.sourceforge.jedit.pluginholder.*;

/**
@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/
public class CompilerThread extends Thread {


    String                  filename;
    String                  compileResult;
    NoExitSecurityManager   sm;
    View                    view;
    boolean                 pkgCompile;
    boolean                 rebuild         = false;
    JCompiler               compiler        = null;

    private     Vector      listeners       = new Vector();
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public CompilerThread(  View view,
                            JCompiler compiler,
                            String path, 
                            NoExitSecurityManager sm, 
                            boolean pkgCompile, 
                            boolean rebuild)    {
                                
        this(view, compiler, path, sm, pkgCompile);
        this.rebuild = rebuild;

    }
    
    /**
    This is the main entry for the compiler.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public CompilerThread(  View view,
                            JCompiler compiler,
                            String path, 
                            NoExitSecurityManager sm, 
                            boolean pkgCompile ) {

        super();

        this.setPriority( Thread.MIN_PRIORITY );
        this.view = view;
        this.compiler = compiler;
        this.sm = sm;
        this.pkgCompile = pkgCompile;
        filename = path;
        this.compiler.getProgress().setValue(0);

    }


    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void addBuildProgressListener(BuildProgressListener listener) {
        listeners.addElement(listener);
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void removeBuildProgressListener(BuildProgressListener listener) {
        listeners.removeElement(listener);
    }
   
    /**
    Used to send the status to all listeners
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void sendStatus(int progress, String message) {
        
        Log.log( Log.DEBUG, "JCompiler", " sendStatus()" );

        //enumerate through all the BuildProgressListeners and report messages
        for ( int i = 0; i < listeners.size(); ++i ) {
        Log.log( Log.DEBUG, "JCompiler", " sending status of " + progress );
            ( (BuildProgressListener)listeners.elementAt(i) ).reportStatus(progress, message);
        }
        
    }
    
    


    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public String getArgumentsAsString( String[] arguments ) {
        StringBuffer buffer = new StringBuffer("");
        
        for (int i = 0; i < arguments.length; ++i ) {
            buffer.append( arguments[i] + " " );
        }

        return buffer.toString();
    }


    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void run() {


        //create a GUI for the user...

        String[] args = null;
        String[] files = null;
        String[] arguments = null;
        String titleStr = filename;

        PrintStream origOut = System.out;
        PrintStream origErr = System.err;
        ByteArrayOutputStream bytes = null;

        /*
        boolean smartCompile = (pkgCompile && 
            jEdit.getProperty("jcompiler.javapkgcompile.smartcompile").equals("T")) ? true : false;
        */
            
        try {
            File ff = new File(filename);
            String parent = ff.getParent();

            if (parent != null && pkgCompile == true) {


                String dir = JavaUtils.getBaseDirectory( ff.getAbsolutePath() );

                String exts[] = { "java" };

                FileChangeMonitor monitor = this.compiler.getController().getMonitor( dir, exts );


                String list[];
                if (this.rebuild) {
                    list = monitor.getAllFiles();
                } else {
                    list = monitor.getChangedFiles();
                }

                this.compiler.getController().getMonitor( dir, exts ).check();

                // = buildtools.JavaUtils.getFilesFromExtension(dir, exts );

                
                //the title to give the compiled output.
                titleStr = list.length + " file(s) in directory: " + dir;
                

                if (list.length == 0) {
                    this.compiler.showStartCompileMsg(view, titleStr, true);
                    return;
                }

                args = new String[2];

                files = list;
                


            }



            if (args == null) {

                titleStr = filename;
                this.compiler.showStartCompileMsg(view, titleStr, true);
                args = new String[2];
                args[1] = filename;
                String [] newfiles = { filename };
                files = newfiles;

            }
            


            // CLASSPATH Setting!!
            args[0] = "-classpath";
            if (jEdit.getProperty("jcompiler.usejavacp").equals("T"))
            {
                args[1] = System.getProperty("java.class.path");
            }
            else {
                args[1] = jEdit.getProperty("jcompiler.classpath");
            }

            if (jEdit.getProperty("jcompiler.addpkg2cp").equals("T"))
            {
                try
                {

                    String pkgName = JavaUtils.getPackageName(filename);


                    // If no package stmt found then pkgName would be null
                    if (parent != null && pkgName == null) {
                        args[1] = args[1] + System.getProperty("path.separator") + parent;
                    }

                    else if (parent != null && pkgName != null)
                    {

                        String pkgPath = pkgName.replace('.', System.getProperty("file.separator").charAt(0));
                        if (parent.endsWith(pkgPath)) {
                            parent = parent.substring(0, parent.length() - pkgPath.length() - 1);
                            args[1] = args[1] + System.getProperty("path.separator") + parent;
                        }           
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
    

            this.compiler.showStartCompileMsg(view, titleStr, false);


            Vector vectorArgs = new Vector();
            
            vectorArgs.addElement( args[0] );
            vectorArgs.addElement( args[1] );
            
            if ( jEdit.getProperty("jcompiler.showdeprecated").equals("T") ) {
                vectorArgs.addElement("-deprecation");
            }

            if ( jEdit.getProperty( "jcompiler.specifyoutputdirectory").equals("T") ) {
                vectorArgs.addElement("-d");
                vectorArgs.addElement(jEdit.getProperty( "jcompiler.outputdirectory"));
            }
            

            //now add the files...
            for (int i = 0; i < files.length; ++i) {
                vectorArgs.addElement(files[i]);                
            }

            arguments = new String[vectorArgs.size()];
            vectorArgs.copyInto(arguments);
            

            this.sendStatus( 50, "Starting compiler" );

            System.out.println( "JCompiler:  compiling with arguments:  " + getArgumentsAsString( arguments ) );

            bytes = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream( bytes );

            System.setOut(ps);
            System.setErr(ps);

            sm.setAllowExit(false);
            
            sun.tools.javac.Main.main( arguments );

            
            //WARNING:  don't put code here as sun.tools.javac.Main.main might throw
            //an exception
            
            //JCompilerPlugin.progress.setValue(100);
            //JCompilerPlugin.progress.setString("Compile done");

        } catch (SecurityException donothing) {
            
            //don't do anything here because sun.tools.javac.Main.main will 
            //always try and exit.

        } catch (RuntimeException e) {

            if ( ! (e instanceof SecurityException) ) {

                System.err.println( 
                "ERROR:  Sun's javac just threw a runtime exceptions.  " + 
                "Please report this to the current JCompiler maintainer." );

                e.printStackTrace();
            }

        } catch (Exception e) { 
            System.setOut(origOut); 
            System.setErr(origErr); 
            e.printStackTrace();

        } finally {
            System.setOut(origOut); 
            System.setErr(origErr); 

            this.sendStatus( 100, "Done" );
        }

        sm.setAllowExit(true);

        
        this.compiler.setCompilerOutput( view, titleStr , new String(bytes.toByteArray()) );
    }


}

