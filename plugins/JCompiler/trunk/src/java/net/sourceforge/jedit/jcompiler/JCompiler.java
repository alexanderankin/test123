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
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.*;
import org.gjt.sp.jedit.textarea.*;


//GUI support
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.Element;


//build support
import net.sourceforge.jedit.buildtools.*;
import net.sourceforge.jedit.buildtools.msg.*;


//standard java stuff
import java.util.Vector;
import java.io.*;
import java.lang.*;

import net.sourceforge.jedit.pluginholder.*;


/**
A jEdit plugin that allows the user to perform compiles

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/
public class JCompiler extends HoldablePlugin {


    private JScrollPane          spane                   = new JScrollPane();
    private JList                errorList               = new JList();
    private View                 view                    = null;
    private Object[]             emptyList               = new Object[1];
    private Vector               model                   = new Vector();
    private String               lastCompiledFile        = null;
    public  FileChangeController controller              = new FileChangeController();
    public  JProgressBar         progress                = new JProgressBar();
    
    
    private NoExitSecurityManager sm;
    private boolean pkgCompile;
    private boolean rebuild = false;
    

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public JProgressBar getProgress() {
        return this.progress;
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public FileChangeController getController() {
        return this.controller;
    }
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void init(Config config) {
        
        this.setLayout(new BorderLayout());
        this.add(spane, BorderLayout.CENTER);
        
        progress.setMinimum(0);
        progress.setMaximum(100);
        progress.setValue(0);
        //progress.setBorder( BorderFactory.createRaisedBevelBorder() );

        this.add(progress, BorderLayout.SOUTH);


        MouseListener mouseListener = new CompilerMouseAdapter( this );


        errorList.addMouseListener( new MouseHandler( this ) );

        errorList.addMouseListener( mouseListener );
        spane.getViewport().setView(errorList);
        
        
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public boolean isRequiredStandalone() {
        return true;
    }
    

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void setEditorFile(String file, int lineNumber) {

        View v = this.getView(); 
        Buffer buffer = jEdit.openFile( v, 
                                        new File( file ).getParent() , 
                                        file, 
                                        false, 
                                        false );
        if (buffer == null) {
            return;
        }

        JEditTextArea textArea = v.getTextArea();
        Element map = buffer.getDefaultRootElement();

        //Element element = map.getElement( lineNum - 1 );

        
        Element element;
        if ( lineNumber > 0 ) {
            element = map.getElement( lineNumber - 1 );
        } else {
            return;
        }
        
        
        if(element != null) {
            view.getTextArea().setCaretPosition(element.getStartOffset());
            view.getTextArea().select(element.getStartOffset(), element.getEndOffset()-1);
            //view.toFront();
            view.requestFocus();
            return;
        }


    }


    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void setCompilerOutput(View view, String javaFile, String compilerResult) {

        setCompilerOutputWin(view, javaFile, compilerResult);
    
    }
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void showStartCompileMsgWin(View v, String filename, boolean compileDone) {

        /*

        if (view != v ) {

            if (dlg != null) {
                dlg.setVisible(false);
                dlg.dispose();
            }

            view = v;
            dlg = new JDialog(v);
            dlg.getContentPane().add(frm);
            dlg.setBounds(500, 180, 600, 180);
        }
        */
        
        errorList.setListData(emptyList);

        
        /*
        FIX ME... find some way to update JCompiler...
        
        if (compileDone) {
            this.getParent().setTitle("Compile Done : " + filename);
        } else {
            dlg.setTitle("Starting to compile : " + filename);
        }
        */

    }

    
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public View getView() {
        return view;
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public String getLastCompiledFile() {
        return lastCompiledFile;
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public Vector getModel() {
        return model;
    }
    
    public JList getErrorList() {
        return this.errorList;
    }


    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void showStartCompileMsg(View v, String filename, boolean compileDone) {

        showStartCompileMsgWin(v, filename, compileDone);

    }

    /**
    Given a string... will load it in the error window.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void setErrorData(String data) {
        
        Vector v = new Vector();
        

        try {

            BufferedReader in = new BufferedReader( new StringReader( data ) );

            for (String line = in.readLine(); line != null; line = in.readLine()) {
                v.addElement(line);
            }
        
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        model = v;
        errorList.setListData(v);
       
    }
    
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void setCompilerOutputWin(View view, String javaFile, String compilerResult) {

        setErrorData( compilerResult );
        
        lastCompiledFile = javaFile;


        /*
        //FIX ME

        if (dlg != null) {
            dlg.setTitle("Compile done : " + javaFile);
            dlg.setVisible(true);
        }
        
        */

    }
    
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public NoExitSecurityManager getSecurityManager() {
        return this.sm;
    }
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void setSecurityManager(NoExitSecurityManager sm) {
        this.sm = sm;
    }
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public boolean getPackageCompile() {
        return this.pkgCompile;
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void setPackageCompile(boolean  pkgCompile) {
        this.pkgCompile = pkgCompile;
    }
    
    public boolean getRebuild() {
        return this.rebuild;
    }
    
    public void setRebuild(boolean rebuild) {
        this.rebuild = rebuild;
    }
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void actionPerformed(ActionEvent evt) {

        View view = EditAction.getView(evt);
        Buffer buf = view.getBuffer();
        boolean autoSaveBuf;
        boolean autoSaveAll;

        if (pkgCompile) {
            autoSaveBuf = jEdit.getProperty("jcompiler.javapkgcompile.autosave").equals("T");
            autoSaveAll = jEdit.getProperty("jcompiler.javapkgcompile.autosaveall").equals("T");
        } else {
            autoSaveBuf = jEdit.getProperty("jcompiler.javacompile.autosave").equals("T");
            autoSaveAll = jEdit.getProperty("jcompiler.javacompile.autosaveall").equals("T");
        }

        if (autoSaveAll) {

            Buffer[] arr = jEdit.getBuffers();
            for (int i = 0; i < arr.length; i++) {
                if (arr[i].isDirty()) {
                    arr[i].save(view, null);
                }
            }
        }

        if (autoSaveAll == false && autoSaveBuf == true && buf.isDirty() == true) {
            buf.save(view, null);
        }

        if (autoSaveAll == false && autoSaveBuf == false && buf.isDirty() == true) {
                int result = JOptionPane.showConfirmDialog(view,
                                            "Save changes to " + buf.getName() + "?",
                                            "File Not Saved",
                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                            JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                if (result == JOptionPane.YES_OPTION) {
                    buf.save(view, null);
                }               
        }
        
        String path = buf.getPath();
        if (path == null || path.equals("") || !path.endsWith(".java"))
        {
            return;
        }


        CompilerThread cc = getCompilerThread(view, path);
        cc.start();

    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    protected CompilerThread getCompilerThread(View view, String path) {

        return new CompilerThread( view, 
                                   this, 
                                   path, 
                                   sm, 
                                   pkgCompile, 
                                   rebuild );
    }


}





