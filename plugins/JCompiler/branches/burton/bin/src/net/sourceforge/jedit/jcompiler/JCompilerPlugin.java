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

//jedit support 
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.textarea.*;


//java stuff
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.text.Element;
import java.lang.*;


//build support
import net.sourceforge.jedit.buildtools.*;
import net.sourceforge.jedit.buildtools.msg.*;

public class JCompilerPlugin extends EBPlugin {


    private static Color                clr                     = Color.black;
    private static JDialog              dlg                     = new JDialog();
    private static JPanel               frm                     = new JPanel();
    private static JScrollPane          spane                   = new JScrollPane();
    private static JList                errorList               = new JList();
    private static View                 view                    = null;
    private static Object[]             emptyList               = new Object[1];
    private static Vector               model                   = new Vector();
    private static String               lastCompiledFile        = null;
    public  static FileChangeController controller              = new FileChangeController();
    public  static JProgressBar         progress                = new JProgressBar();

    //context sensitive menu
    
    static {
        frm.setLayout(new BorderLayout());
        frm.add(spane, BorderLayout.CENTER);
        
        progress.setMinimum(0);
        progress.setMaximum(100);
        progress.setValue(0);
        //progress.setBorder( BorderFactory.createRaisedBevelBorder() );

        frm.add(progress, BorderLayout.SOUTH);


        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {


                if (e.getClickCount() == 2) {

                    
                    int index = errorList.locationToIndex(e.getPoint());

                    Vector vec = JCompilerPlugin.getModel();
                    
                    try {

                        //if the location that they clicked on was larger than
                        //the registered number of lines

                        String err;
                        if ( index < vec.size() && index != -1) {
                            err = (String) vec.elementAt(index);
                        } else {
                            return;
                        }

                        BuildMessage message = BuildMessage.getBuildMessage( err );


                        //only continue if this is a supported BuildMessage type
                        if ( message.getType() == BuildMessage.TYPE_UNKNOWN ) {
                            return;
                        }

                        if ( message.getType() == BuildMessage.TYPE_EXCEPTION ) {
                            
                            JCompilerPlugin.dlg.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                            //type to decompile this class...
                            EditBus.send( new DecompileClassMessage( null, message.getTarget(), message ) );

                            return;
                        }
                        
                        
                        JCompilerPlugin.setEditorFile( message.getTarget(), message.getLineNumber() );


                    } catch (Exception exp) { 
                        exp.printStackTrace();
                    }
                    
                }
            }
        };


        errorList.addMouseListener( new MouseHandler() );

        errorList.addMouseListener( mouseListener );
        spane.getViewport().setView(errorList);
    }

    public static void setEditorFile(String file, int lineNumber) {

        View v = JCompilerPlugin.getView(); 
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

    
    
    public static View getView() {
        return view;
    }

    public static String getLastCompiledFile() {
        return lastCompiledFile;
    }

    public static Vector getModel() {
        return model;
    }


    public static void showStartCompileMsg(View v, String filename, boolean compileDone) {

        showStartCompileMsgWin(v, filename, compileDone);

    }

    public static void setCompilerOutput(View view, String javaFile, String compilerResult) {

        setCompilerOutputWin(view, javaFile, compilerResult);
    
    }
    
    public static synchronized void showStartCompileMsgWin(View v, String filename, boolean compileDone) {


        if (view != v || dlg == null) {

            if (dlg != null) {
                dlg.setVisible(false);
                dlg.dispose();
            }

            view = v;
            dlg = new JDialog(v);
            dlg.getContentPane().add(frm);
            dlg.setBounds(500, 180, 600, 180);
        }
        errorList.setListData(emptyList);
        if (compileDone) {
            dlg.setTitle("Compile Done : " + filename);
        } else {
            dlg.setTitle("Starting to compile : " + filename);
        }
        dlg.setVisible(true);
    }

    public static synchronized void setCompilerOutputWin(View view, String javaFile, String compilerResult) {

        setErrorData( compilerResult );
        
        lastCompiledFile = javaFile;

        if (dlg != null) {
            dlg.setTitle("Compile done : " + javaFile);
            dlg.setVisible(true);
        }


    }
    

    /**
    Given a string... will load it in the error window.
    
    */
    public static void setErrorData(String data) {
        
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
    

    public String getName(){
        return "jcompilerplugin";
    }

    public void start() {
        try {
            NoExitSecurityManager sm = NoExitSecurityManager.getNoExitSM();
            java.lang.System.setSecurityManager(sm);

            jEdit.addAction( new JCompiler(sm, "jcompiler", false) );
            jEdit.addAction( new JCompiler(sm, "jpkgcompiler", true) );
            jEdit.addAction( new JCompiler(sm, "jpkgrebuild", true, true) );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void createMenuItems(View view, Vector menus, Vector menuItems) {
        menus.addElement(GUIUtilities.loadMenu(view, "jcompiler-menu"));

    }
    
    public void createOptionPanes(OptionsDialog od) {
        od.addOptionPane( new JCompilerPane()  );
    }
    
    public void loadStackTrace(String trace) {
        
    }

    /**
    Handle message for decompile requests..

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public void handleMessage(EBMessage message) {

        if (message instanceof DecompileClassMessage) {
            
            BuildMessage bm = (BuildMessage)((DecompileClassMessage)message).getMessage();

            DecompileClassMessage decompile = (DecompileClassMessage)message;
            
            //System.out.println( "JCompiler found an instance of the decompile message:  " + decompile.getFileName() );

            JCompilerPlugin.setEditorFile( decompile.getFileName(), bm.getLineNumber() );

            JCompilerPlugin.dlg.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        }

    }



	static class MouseHandler extends MouseAdapter implements ActionListener {

        JMenuItem stackTrace = null;
        JMenuItem one = null;
        JMenuItem two = null;
        JMenuItem three = null;

        JPopupMenu menu = null;


		public void mousePressed(MouseEvent evt) {

			if((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
				showFullMenu( evt.getX(), evt.getY() );

		}


        private void showFullMenu(int x, int y)	{
    
            this.stackTrace = new JMenuItem("Load stack trace...");
            this.one = new JMenuItem( jEdit.getProperty( "jcompiler.label" ) );
            this.two = new JMenuItem( jEdit.getProperty( "jpkgcompiler.label") );
            this.three = new JMenuItem( jEdit.getProperty( "jpkgrebuild.label" ) );


            this.menu = new JPopupMenu();
 


   
            stackTrace.addActionListener(this);
            one.addActionListener(this);
            two.addActionListener(this);
            three.addActionListener(this);            
            
            menu.add(one);
            menu.add(two);
            menu.add(three);

            menu.addSeparator();

            menu.add(stackTrace);
            menu.show( JCompilerPlugin.errorList, x, y);
            
        }

        public void actionPerformed(ActionEvent e) {
            
 

           if ( e.getSource() == this.stackTrace ) {
                new StackTraceDialog( JCompilerPlugin.getView() );
            } else if ( e.getSource() == this.one) {

                new JCompiler(NoExitSecurityManager.getNoExitSM(), "jcompiler", false).actionPerformed(e);

            } else if ( e.getSource() == this.two) {

                new JCompiler(NoExitSecurityManager.getNoExitSM(), "jpkgcompiler", true).actionPerformed(e);
                
            } else if ( e.getSource() == this.three) {

                new JCompiler(NoExitSecurityManager.getNoExitSM(), "jpkgrebuild", true, true).actionPerformed(e);
                
            }


            
        }

	}
}
