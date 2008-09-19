/*
* $Revision$
* $Date$
* $Author$
*
* Copyright (C) 2008 Eric Le Lay
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

package cswilly.jeditPlugins.spell;


//{{{ Imports

import java.io.*;
import java.awt.Dialog;
import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.JButton;

import javax.swing.tree.*;
import javax.swing.JTree;
import java.util.Arrays;

//{{{ 	jEdit
import org.gjt.sp.jedit.*;

//}}}

//{{{	junit
import org.junit.*;
import static org.junit.Assert.*;
//}}}

//{{{	FEST...
import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.driver.BasicJTreeCellReader;
//}}}

///}}}

/**
 * provides methods to start jEdit and to dispose of the robot.
 */
public class TestUtils{

	public static final String ENV_JEDIT_SETTINGS = "test-jedit.settings";
	
	//common environment variables
	public static final String ENV_ASPELL_EXE	  = "test-jedit.aspell-exe";
	public static final String ENV_TESTS_DIR	  = "test-tests.dir";
	public static final String ENV_IN_JEDIT		  = "test-in-jedit";

	private static FrameFixture jeditFrame;
	private static RobotFixture robot;
	private static EmergencyAbortListener listener;  

	private static boolean injEdit = false;
	public static void setUpjEdit(){
		
		System.out.println("Setting jedit up");

		String inJedit = System.getProperty(ENV_IN_JEDIT);
		injEdit = "yes".equals(injEdit);
		
		if(injEdit){
			try{
				robot = RobotFixture.robotWithCurrentAwtHierarchy();
				listener = EmergencyAbortListener.registerInToolkit();
				
			try{
			robot.printer().printComponents(new PrintStream(new FileOutputStream("/Users/elelay/temp/client2/jEdit/SpellCheck/print-comps")));
			}catch(FileNotFoundException fnfe){}
			jeditFrame = new FrameFixture(robot, jEdit.getActiveView());//DOESN'T WORK : WindowFinder.findFrame(View.class).using(robot);
			//jeditFrame = new FrameFixture(robot,jEdit.newView(jeditFrame.targetCastedTo(View.class)));
			}catch(RuntimeException re){
				System.out.println(re.toString());
			}
		}else{
		 	robot = RobotFixture.robotWithNewAwtHierarchy();
			String settings = System.getProperty(ENV_JEDIT_SETTINGS);
			assertTrue("Forgot to set env. variable '"+ENV_JEDIT_SETTINGS+"'",settings!=null);
			
			final String[] args = {"-settings="+settings,"-norestore","-noserver","-nobackground"};
			Thread runJeditThread = new Thread(){
				public void run(){
					jEdit.main(args);
				}
			};
			runJeditThread.start();
			jeditFrame = WindowFinder.findFrame(View.class).withTimeout(40000).using(robot);
			try{
				Class c = Class.forName(SpellCheckPlugin.class.getName());
			}catch(ClassNotFoundException cnfe){
				fail("Couldn't find plugin's class");
			}
		}
		System.out.println("Setup done");
	}

	public static Robot robot(){
		return robot;
	}

	public static FrameFixture jeditFrame(){
		return jeditFrame;
	}
	
	public static  void tearDownjEdit(){
		listener.unregister();  
		if(injEdit){
			//jeditFrame.cleanUp();
			robot.releaseMouseButtons();
			ScreenLock.instance().release(robot);
			System.out.println("tearDown done in jEdit");
		}else{
			robot.cleanUp();
		}
		robot = null;
		jeditFrame = null;

	}
	
	public static DialogFixture findDialogByTitle(String title){
			return new DialogFixture(robot(), (Dialog)robot().finder().find(new FirstDialogMatcher(title)));
	}
	
	public static void close(final View view, final Buffer buffer){
		SwingUtilities.invokeLater(
		new Runnable(){
			public void run(){
			jEdit.closeBuffer(view,buffer);
			}
		});
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		if(buffer.isDirty()){
			TestUtils.findDialogByTitle("File Not Saved").button(AbstractButtonTextMatcher.withText(JButton.class,"Non")).click();
		}
	}

	public static Buffer openFile(final String filename){
		final View view = jeditFrame().targetCastedTo(View.class);
		try{
		SwingUtilities.invokeAndWait(
			new Runnable(){
				public void run(){
					jEdit.openFile(view,filename);
				}
			});
		}catch(Exception e){
			System.err.println(e);
		}
	
		try{Thread.sleep(1000);}catch(InterruptedException ie){}
		return view.getBuffer();
	}

	public static Buffer newFile(){
		final View view = jeditFrame().targetCastedTo(View.class);
		try{
		SwingUtilities.invokeAndWait(
			new Runnable(){
				public void run(){
					jEdit.newFile(view);
				}
			});
		}catch(Exception e){
			System.err.println(e);
		}
	
		try{Thread.sleep(1000);}catch(InterruptedException ie){}
		return view.getBuffer();
	}
	
	public static View view(){
		return jeditFrame().targetCastedTo(View.class);
	}
	
	public static void replaceText(JTextComponentFixture f,String text){
		f.select(f.text()).deleteText().enterText(text);
	}
	
	public static TreePath getPathForStrings(JTreeFixture treeFixture,String[]path){
		
		JTree tree = treeFixture.targetCastedTo(JTree.class);
		BasicJTreeCellReader rdr = new BasicJTreeCellReader();
		TreeModel mdl = tree.getModel();
		
		
		Object parent = mdl.getRoot();
		TreePath returnPath = new TreePath(parent);
		for(int i=0;i<path.length;i++){
			boolean found=false;
			for(int iChild=0;!found&&iChild<mdl.getChildCount(parent);iChild++){
				Object val = mdl.getChild(parent,iChild);
				String lbl = rdr.valueAt(tree,val);
				if(path[i].equals(lbl)){
					returnPath = returnPath.pathByAddingChild(val);
					parent = val;
					found=true;
				}
			}
			if(!found){
				throw new IllegalArgumentException("Couldn't find path"+Arrays.asList(path)+", stopped at level "+i);
			}
		}
		return returnPath;
	}
	
	public static void selectPath(JTreeFixture treeFixture, String[]path){
		JTree tree = treeFixture.targetCastedTo(JTree.class);
		TreePath finalPath = getPathForStrings(treeFixture,path);
				
		if(!tree.isVisible(finalPath)){
			TreePath parentPath = new TreePath(finalPath.getPathComponent(0)); 
			for(int i=1;i<finalPath.getPathCount();i++){
				TreePath curPath = parentPath.pathByAddingChild(finalPath.getPathComponent(i));
				if(!tree.isVisible(curPath)){
					treeFixture.toggleRow(tree.getRowForPath(curPath.getParentPath()));
				}
				parentPath = curPath;
			}
		}

		treeFixture.selectRow(tree.getRowForPath(finalPath));

	}
}
