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
import java.util.*;

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
import org.fest.swing.edt.*; 
import org.fest.swing.timing.*;
//}}}

///}}}

import cswilly.spell.ValidationDialog;
import cswilly.spell.SpellException;
import cswilly.spell.Result;
import cswilly.spell.WordListValidator;
import cswilly.spell.AspellEngine;
import cswilly.spell.MockEngine;

import static org.gjt.sp.jedit.testframework.TestUtils.*;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.*;
import static cswilly.jeditPlugins.spell.TestUtils.ENV_ASPELL_EXE;


/**
 * Test the functions of BufferDialogValidator
 *	- test the validation dialog UI and results
 *	- test with multiple buffers in the same view (test not working yet)
 */
public class BufferDialogValidatorTest{
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
	public void testDialog(){
		System.err.println("testDialog");
		final BufferDialogValidator valid = new BufferDialogValidator();

		View view = TestUtils.view(); 
		final Buffer buffer = TestUtils.newFile();
		buffer.insert(0,"The qwick brown foxe");
		final String oldText = buffer.getText(0,buffer.getLength());
				
		valid.setTextArea(view.getTextArea());
		final List<Result> res = new LinkedList<Result>();
		res.add(new Result(1,Result.OK,null,"The"));
		res.add(new Result(5,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(11,Result.OK,null,"brown"));
		res.add(new Result(17,Result.SUGGESTION,Arrays.asList(new String[]{"Fox","fox","Foxes"}),"foxe"));
		
		List<Result> oldRes =  new ArrayList<Result>(res);
		for(int i=0;i<res.size();i++){
			try{
			oldRes.add((Result)res.get(i).clone());
			}catch(Exception e){}
		}
		
		MockEngine engine = new MockEngine();
		engine.addResults(res);
		valid.setEngine(engine);
		valid.setEngineForSuggest(new MockEngine());
		final AtomicReference<Boolean> cancel = new AtomicReference<Boolean>(Boolean.FALSE);
		final AtomicReference<SpellException> except = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
					cancel.set(!valid.spellcheck());
				}catch(SpellException spe){
					except.set(spe);
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		
		spellDialog.textBox("originalWord").requireText("qwick");
		spellDialog.textBox("changeTo").requireText("wick");
		
		spellDialog.list().selectItem("quick");
		spellDialog.textBox("changeTo").requireText("quick");
		spellDialog.list().selectItem("wick");
		spellDialog.textBox("changeTo").requireText("wick");

		spellDialog.button("Change").click();
		Pause.pause(2000);
		spellDialog.list().selectItem("fox");
		spellDialog.button("Change").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue(!cancel.get());
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());

/*		no more applicable, as BufferDialogValidator doesn't modify the list.
		it applies modifications to the buffer.
		assertEquals(4,res.size());
		assertEquals(Result.OK,res.get(0).getType());
		assertEquals(Result.SUGGESTION,res.get(1).getType());
		assertEquals(1,res.get(1).getSuggestions().size());
		assertEquals("wick",res.get(1).getSuggestions().get(0));
		assertEquals(Result.OK,res.get(2).getType());
		assertEquals(Result.SUGGESTION,res.get(3).getType());
		assertEquals(1,res.get(3).getSuggestions().size());
		assertEquals("Fox",res.get(3).getSuggestions().get(0));
*/		
		TestUtils.close(view,buffer);
	}

	@Test
	public void testNullDictIgnoreAll(){
		System.err.println("testNullDictIgnoreAll");
		final BufferDialogValidator valid = new BufferDialogValidator();

		
		final View view = TestUtils.view();
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
	
		Pause.pause(1000);
		
		final Buffer buffer = view.getBuffer();
		buffer.insert(0,"The qwick qwick foxe");
		final String oldText = buffer.getText(0,buffer.getLength());
		
		valid.setUserDictionary(null);
		valid.setIgnoreAll(null);

		valid.setTextArea(view.getTextArea());
		final List<Result> res = new LinkedList<Result>();
		res.add(new Result(1,Result.OK,null,"The"));
		res.add(new Result(5,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(11,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(17,Result.SUGGESTION,Arrays.asList(new String[]{"Fox","fox","Foxes"}),"foxe"));
		
		List<Result> oldRes =  new ArrayList<Result>(res);
		for(int i=0;i<res.size();i++){
			try{
			oldRes.add((Result)res.get(i).clone());
			}catch(Exception e){}
		}
		
		MockEngine engine = new MockEngine();
		engine.addResults(res);
		valid.setEngine(engine);
		valid.setEngineForSuggest(new MockEngine());

		final AtomicReference<Boolean> cancel = new AtomicReference<Boolean>(Boolean.FALSE);
		final AtomicReference<SpellException> except = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
				cancel.set(!valid.spellcheck());
				}catch(SpellException spe){
					except.set(spe);
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());

		spellDialog.textBox("originalWord").requireText("qwick");
		spellDialog.button("Add").click();
		Pause.pause(2000);

		spellDialog.textBox("originalWord").requireText("qwick");
		spellDialog.button("Ignore All").click();
		Pause.pause(2000);

		spellDialog.textBox("originalWord").requireText("foxe");
		spellDialog.button("Change").click();
		Pause.pause(2000);

		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue(!cancel.get());
		assertEquals(null,except.get());
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());

		buffer.undo(view.getTextArea());
		SwingUtilities.invokeLater(
		new Runnable(){
			public void run(){
			jEdit.closeBuffer(view,buffer);
			}
		});
		Pause.pause(2000);
		TestUtils.findDialogByTitle("File Not Saved").button(org.fest.swing.core.matcher.JButtonMatcher.withText("Non")).click();
	}
	
	@Test
	public void testUserDict(){
		System.err.println("testUserDict");
		final BufferDialogValidator valid = new BufferDialogValidator();

		
		final View view = TestUtils.view();
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
	
		Pause.pause(1000);
		
		final Buffer buffer = view.getBuffer();
		buffer.insert(0,"The qwick qwick foxe");
		final String oldText = buffer.getText(0,buffer.getLength());
		WordListValidator wl = new WordListValidator();
		wl.addWord("qwick");
		valid.setUserDictionary(wl);
		valid.setIgnoreAll(null);
		valid.setTextArea(view.getTextArea());
		final List<Result> res = new LinkedList<Result>();
		res.add(new Result(1,Result.OK,null,"The"));
		res.add(new Result(5,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(11,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(17,Result.SUGGESTION,Arrays.asList(new String[]{"Fox","fox","Foxes"}),"foxe"));
		
		List<Result> oldRes =  new ArrayList<Result>(res.size());
		for(int i=0;i<res.size();i++){
			try{
			oldRes.add((Result)res.get(i).clone());
			}catch(Exception e){}
		}
		
		MockEngine engine = new MockEngine();
		engine.addResults(res);
		valid.setEngine(engine);
		valid.setEngineForSuggest(new MockEngine());

		final AtomicReference<Boolean> cancel = new AtomicReference<Boolean>(Boolean.FALSE);
		final AtomicReference<SpellException> except = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
				cancel.set(!valid.spellcheck());
				}catch(SpellException spe){
					except.set(spe);
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());

		spellDialog.textBox("originalWord").requireText("foxe");
		spellDialog.button("Add").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue(!cancel.get());
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());

		assertEquals(2,wl.getAllWords().size());
		
		TestUtils.close(view,buffer);
	}
	
	@Test
	public void testCancel(){
		System.err.println("testCancel");
		AspellEngine engine = null;
		try{
			engine = new AspellEngine( Arrays.asList(new String[]{exePath,"--lang","en","pipe"}), "UTF-8", true);
		}catch(SpellException spe){
			fail("shouldn't throw an exception");
		}
		WordListValidator userDict = new WordListValidator();
		
		final BufferDialogValidator valid = new BufferDialogValidator();
		valid.setEngine(engine);
		valid.setEngineForSuggest(new MockEngine());
		valid.setUserDictionary(userDict);
		
		final Buffer buffer = TestUtils.openFile(testsDir+"/spellTest.txt");
		//do that so there is one change before spell-checking
		//(to catch an undue undo ;-))
		GuiActionRunner.execute(new GuiTask(){
		protected void executeInEDT(){
			buffer.insert(0,"hello,\n");
		}});
		String oldText = buffer.getText(0,buffer.getLength());
		
		valid.setTextArea(TestUtils.view().getTextArea());

		final AtomicReference<SpellException> exp = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
				valid.spellcheck();
				}catch(SpellException spe){
					exp.set(spe);
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.button("Add").click();
		Pause.pause(1000);
		spellDialog.button("Change").click();
		Pause.pause(1000);
		//spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.button("Cancel").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertNull(exp.get());
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());

		assertEquals(oldText,buffer.getText(0,buffer.getLength()));
		assertEquals(0,userDict.getAllWords().size());
	}
	
	@Test
	public void testCommit(){
		System.err.println("testCommit");
		AspellEngine engine = null;
		try{
			engine = new AspellEngine( Arrays.asList(new String[]{exePath, "--lang","en","pipe"}), "UTF-8", true);
		}catch(SpellException spe){
			fail("shouldn't throw an exception");
		}

		final BufferDialogValidator valid = new BufferDialogValidator();
		valid.setEngine(engine);
		valid.setEngineForSuggest(new MockEngine());
		
		final View view = TestUtils.view();
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
	
		Pause.pause(1000);
		
		final Buffer buffer = view.getBuffer();
		GuiActionRunner.execute(new GuiTask(){
		protected void executeInEDT(){
			buffer.insert(0,"The qwick brown foxe");
		}});
		
		String oldText = buffer.getText(0,buffer.getLength());
		
		valid.setTextArea(view.getTextArea());
		final AtomicReference<SpellException> exp = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
				 valid.spellcheck();
				}catch(SpellException spe){
					exp.set(spe);
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.list().selectItem("quick");
		spellDialog.button("Change").click();
		Pause.pause(2000);
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.list().selectItem("fox");
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
		System.err.println("testScroll");
		AspellEngine engine = null;
		try{
			engine = new AspellEngine(Arrays.asList(new String[]{exePath,"--lang","en","pipe"}), "UTF-8", true);
		}catch(SpellException spe){
			fail("shouldn't throw an exception");
		}
		final BufferDialogValidator valid = new BufferDialogValidator();
		valid.setEngine(engine);
		valid.setEngineForSuggest(new MockEngine());
		
		final View view = TestUtils.view();
		
		final Buffer buffer = TestUtils.newFile();
		
		GuiActionRunner.execute(new GuiTask(){
		protected void executeInEDT(){
			buffer.insert(0,"The qwick brown foxe\n");
		
			for(int i=0;i<50;i++)
				buffer.insert(buffer.getLength(),"some correct text\n");
			buffer.insert(buffer.getLength(),"Wronge text\n");		
		}});


		String oldText = buffer.getText(0,buffer.getLength());
		
		valid.setTextArea(view.getTextArea());
		final AtomicReference<SpellException> exp = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
				valid.spellcheck();
				}catch(SpellException spe){
					exp.set(spe);
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.list().selectItem("quick");
		spellDialog.button("Change").click();
		Pause.pause(2000);
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.list().selectItem("fox");
		spellDialog.button("Change").click();
		Pause.pause(2000);
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
	
	@Test
	public void testPrevious(){
		System.err.println("testPrevious");
		final Buffer buf = TestUtils.newFile();
		final String text = 
			 "/*\n"
			+"* Revision: 13069 \n"
			+"* Date: 2008-07-20 20:32:33 +0200 (Ven 20 jul 2008) \n"
			+"* Author: kerik-sf ";
		
		try{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				buf.insert(0,text);}});
		}catch(Exception e){}

		AspellEngine engine = null;
		try{
			engine = new AspellEngine(Arrays.asList(new String[]{ exePath, "--lang","en","pipe"}), "UTF-8", true);
		}catch(SpellException spe){
			fail("shouldn't throw an exception");
		}
		final BufferDialogValidator valid = new BufferDialogValidator();
		valid.setEngine(engine);
		valid.setEngineForSuggest(new MockEngine());
		valid.setUserDictionary(new WordListValidator());
		valid.setTextArea(TestUtils.view().getTextArea());
		valid.setIgnoreAll(new WordListValidator());
		
		final AtomicReference<SpellException> exp = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
				valid.spellcheck();
				}catch(SpellException spe){
					exp.set(spe);
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.textBox("originalWord").requireText("Ven");
		spellDialog.button("Change").click();
		Pause.pause(1000);
		spellDialog.textBox("originalWord").requireText("jul");
		spellDialog.button("Add").click();
		Pause.pause(1000);
		spellDialog.textBox("originalWord").requireText("kerik");
		spellDialog.button("Previous").click();
		Pause.pause(1000);
		spellDialog.textBox("originalWord").requireText("jul");
		spellDialog.button("Change").click();
		Pause.pause(1000);
		spellDialog.textBox("originalWord").requireText("kerik");
		spellDialog.button("Ignore").click();


		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertNull(exp.get());
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());

		assertTrue(!text.equals(buf.getText(0,buf.getLength())));
		buf.undo(TestUtils.view().getTextArea());
		assertEquals(text,buf.getText(0,buf.getLength()));
	}
}
