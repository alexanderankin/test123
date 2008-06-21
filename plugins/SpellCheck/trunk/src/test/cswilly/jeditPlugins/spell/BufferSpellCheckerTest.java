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
import java.util.concurrent.atomic.*;
import javax.swing.*;

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
//}}}

///}}}

import cswilly.spell.ValidationDialog;
import cswilly.spell.SpellException;


import static cswilly.jeditPlugins.spell.TestUtils.*;


/**
 * Test the functions of BufferSpellChecker
 *	- test basic validation UI & cancel
 *	- test commit/undo
 */
public class BufferSpellCheckerTest{
	private static String testsDir;
	private static String exePath;
	
	@BeforeClass
	public static void setUpjEdit(){
		testsDir = System.getProperty(ENV_TESTS_DIR);
		assertTrue("Forgot to set env. variable '"+ENV_TESTS_DIR+"'",testsDir!=null);
		exePath = System.getProperty(ENV_ASPELL_EXE);
		assertTrue("Forgot to set env. variable '"+ENV_ASPELL_EXE+"'",exePath!=null);

		TestUtils.setUpjEdit();
	}

	@AfterClass
	public static  void tearDownjEdit(){
		TestUtils.tearDownjEdit();
	}

	@Before
	public void beforeTest(){
		jEdit.getPlugin(SpellCheckPlugin.class.getName()).getPluginJAR().activatePluginIfNecessary();
	}

	@After
	public void afterTest(){
		jEdit.getPlugin(SpellCheckPlugin.class.getName()).getPluginJAR().deactivatePlugin(false);
	}

	@Test
	public void testCancel(){
		final BufferSpellChecker check = new BufferSpellChecker( exePath, new String[]{"--lang","en","pipe"});
		
		final View view = TestUtils.jeditFrame().targetCastedTo(View.class);
		try{
		SwingUtilities.invokeAndWait(
			new Runnable(){
				public void run(){
					jEdit.openFile(view,testsDir+"/spellTest.txt");
				}
			});
		}catch(Exception e){
			System.err.println(e);
		}
	
		try{Thread.sleep(1000);}catch(InterruptedException ie){}
		
		final Buffer buffer = view.getBuffer();
		//do that so there is one change before spell-checking
		//(to catch an undue undo ;-))
		buffer.insert(0,"hello,\n");
		try{Thread.sleep(1000);}catch(InterruptedException ie){}
		String oldText = buffer.getText(0,buffer.getLength());
		
		final AtomicReference<SpellException> exp = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
				check.checkBuffer(view.getTextArea(),buffer);
				}catch(SpellException spe){
					exp.set(spe);
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.button("Change").click();
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.button("Cancel").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertNull(exp.get());
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());

		assertEquals(oldText,buffer.getText(0,buffer.getLength()));
	}
	
	@Test
	public void testCommit(){
		final BufferSpellChecker check = new BufferSpellChecker( exePath, new String[]{"--lang","en","pipe"});
		
		final View view = TestUtils.jeditFrame().targetCastedTo(View.class);
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
		
		final Buffer buffer = view.getBuffer();
		buffer.insert(0,"The qwick brown foxe");
		String oldText = buffer.getText(0,buffer.getLength());
		
		final AtomicReference<SpellException> exp = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
				check.checkBuffer(view.getTextArea(),buffer);
				}catch(SpellException spe){
					exp.set(spe);
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.list().selectItem("fox");
		spellDialog.button("Change").click();
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.list().selectItem("quick");
		spellDialog.button("Change").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertNull(exp.get());
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());

		assertEquals("The quick brown fox",buffer.getText(0,buffer.getLength()));
		buffer.undo(view.getTextArea());
		assertEquals("The qwick brown foxe",buffer.getText(0,buffer.getLength()));
	}
	
	
	@Test
	public void testScroll(){
		final BufferSpellChecker check = new BufferSpellChecker( exePath, new String[]{"--lang","en","pipe"});
		
		final View view = TestUtils.jeditFrame().targetCastedTo(View.class);
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
		
		final Buffer buffer = view.getBuffer();

		buffer.insert(0,"The qwick brown foxe\n");
		for(int i=0;i<50;i++)
			buffer.insert(buffer.getLength(),"some correct text\n");
		buffer.insert(buffer.getLength(),"Wronge text\n");		


		String oldText = buffer.getText(0,buffer.getLength());
		
		final AtomicReference<SpellException> exp = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
				check.checkBuffer(view.getTextArea(),buffer);
				}catch(SpellException spe){
					exp.set(spe);
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.list().selectItem("fox");
		spellDialog.button("Change").click();
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.list().selectItem("quick");
		spellDialog.button("Change").click();
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.button("Change").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertNull(exp.get());
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());

		assertTrue(!oldText.equals(buffer.getText(0,buffer.getLength())));
		buffer.undo(view.getTextArea());
		assertEquals(oldText,buffer.getText(0,buffer.getLength()));
	}
}
