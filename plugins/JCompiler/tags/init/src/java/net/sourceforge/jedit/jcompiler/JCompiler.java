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


public class JCompiler extends EditAction implements BuildProgressListener {

	NoExitSecurityManager sm;
	boolean	pkgCompile;
    boolean rebuild = false;
    

	public JCompiler(NoExitSecurityManager sm, String pluginName, boolean pkgCompile) {
		super(pluginName);
		this.pkgCompile = pkgCompile;
		this.sm = sm;
	}

	public JCompiler(NoExitSecurityManager sm, String pluginName, boolean pkgCompile, boolean rebuild)
	{
		super(pluginName);
		this.pkgCompile = pkgCompile;
		this.sm = sm;
        this.rebuild = rebuild;
	}
	
	public void actionPerformed(ActionEvent evt) 
	{
		View view = getView(evt);
		Buffer buf = view.getBuffer();
		boolean autoSaveBuf;
		boolean autoSaveAll;
		if (pkgCompile)
		{
			autoSaveBuf = jEdit.getProperty("jcompiler.javapkgcompile.autosave").equals("T");
			autoSaveAll = jEdit.getProperty("jcompiler.javapkgcompile.autosaveall").equals("T");
		} 
		else
		{
			autoSaveBuf = jEdit.getProperty("jcompiler.javacompile.autosave").equals("T");
			autoSaveAll = jEdit.getProperty("jcompiler.javacompile.autosaveall").equals("T");
		}
		if (autoSaveAll)
		{
			Buffer[] arr = jEdit.getBuffers();
			for (int i = 0; i < arr.length; i++)
			{
				if (arr[i].isDirty())
				{
					arr[i].save(view, null);
				}
			}
		}
		if (autoSaveAll == false && autoSaveBuf == true && buf.isDirty() == true)
		{
			buf.save(view, null);
		}

		if (autoSaveAll == false && autoSaveBuf == false && buf.isDirty() == true)
		{
				int result = JOptionPane.showConfirmDialog(view,
											"Save changes to " + buf.getName() + "?",
											"File Not Saved",
											JOptionPane.YES_NO_CANCEL_OPTION,
											JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.CANCEL_OPTION)
				{
					return;
				}
				if (result == JOptionPane.YES_OPTION)
				{
					buf.save(view, null);
				}				
		}
		
		String path = buf.getPath();
		if (path == null || path.equals("") || !path.endsWith(".java")) 
		{
			return;
		}


		CompilerThread cc = getCompilerThread(view, path);
        cc.addBuildProgressListener(this);
		cc.start();

	}

	protected CompilerThread getCompilerThread(View view, String path) {
		return new CompilerThread(view, path, sm, pkgCompile, rebuild);
	}




    //BuildProgressListener
    public void reportNewBuild() {}
    public void reportBuildDone( boolean success ) {}


    public void reportError(String file, int line, String errorMessage) {}
    public void reportMessage(String message){}

    public void reportStatus(int progress, String message) {
        Log.log( Log.DEBUG, "JCompiler", " reportProgress()" );
        SwingUtilities.invokeLater(new BuildUpdater( progress, message ) );
    }
    
    
    
    
}

class CompilerThread extends Thread {


	String filename;
	String compileResult;
	NoExitSecurityManager sm;
	View view;
	boolean	pkgCompile;
    boolean rebuild = false;

    //BEGIN FINAL BUILDER OBJECT CODE

    private Vector listeners = new Vector();
    
    public void addBuildProgressListener(BuildProgressListener listener) {
        listeners.addElement(listener);
    }

    public void removeBuildProgressListener(BuildProgressListener listener) {
        listeners.removeElement(listener);
    }
   
    public void sendStatus(int progress, String message) {
        
        Log.log( Log.DEBUG, "JCompiler", " sendStatus()" );

        //enumerate through all the BuildProgressListeners and report messages
        for ( int i = 0; i < listeners.size(); ++i ) {
        Log.log( Log.DEBUG, "JCompiler", " sending status of " + progress );
            ( (BuildProgressListener)listeners.elementAt(i) ).reportStatus(progress, message);
        }
        
    }
    
    //END FINAL BUILDER OBJECT CODE
    
    public CompilerThread(  View view, 
                            String path, 
                            NoExitSecurityManager sm, 
                            boolean pkgCompile, 
                            boolean rebuild) 	{
                                
        this(view, path, sm, pkgCompile);
        this.rebuild = rebuild;

    }
	
	public CompilerThread(View view, String path, NoExitSecurityManager sm, boolean pkgCompile) {
        super();
        this.setPriority( Thread.MIN_PRIORITY );
		this.view = view;
		this.sm = sm;
		this.pkgCompile = pkgCompile;
		filename = path;
        JCompilerPlugin.progress.setValue(0);

	}


    public String getArgumentsAsString( String[] arguments ) {
        StringBuffer buffer = new StringBuffer("");
        
        for (int i = 0; i < arguments.length; ++i ) {
            buffer.append( arguments[i] + " " );
        }

        return buffer.toString();
    }


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

                FileChangeMonitor monitor = JCompilerPlugin.controller.getMonitor( dir, exts );


                String list[];
                if (this.rebuild) {
                    list = monitor.getAllFiles();
                } else {
                    list = monitor.getChangedFiles();
                }

                JCompilerPlugin.controller.getMonitor( dir, exts ).check();

                // = buildtools.JavaUtils.getFilesFromExtension(dir, exts );

                
                //the title to give the compiled output.
				titleStr = list.length + " file(s) in directory: " + dir;
                

				if (list.length == 0) {
					JCompilerPlugin.showStartCompileMsg(view, titleStr, true);
					return;
				}

				args = new String[2];

                files = list;
                


 			}



			if (args == null) {

                titleStr = filename;
        		JCompilerPlugin.showStartCompileMsg(view, titleStr, true);
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
	

			JCompilerPlugin.showStartCompileMsg(view, titleStr, false);


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

        
		JCompilerPlugin.setCompilerOutput( view, titleStr , new String(bytes.toByteArray()) );
	}


}

class JavaFilenameFilter implements java.io.FilenameFilter
{
	
	public boolean accept(File dir, String name)
	{

 		if (name.endsWith(".java") == false)
		{
			return false;
		}
		String clazzName = name.substring(0, name.length()-5) + ".class";

		File clazzFile = new File(dir, clazzName);
		if (clazzFile.exists() == false)
		{
			return true;
		}
		File srcFile = new File(dir, name);
		if (srcFile.exists() == false)
		{
			return false;
		}
		long srcTime = srcFile.lastModified(); 
		long clsTime = clazzFile.lastModified();
		if (srcTime >= clsTime)
		{
			return true;
		}
		return false;
	}


}



