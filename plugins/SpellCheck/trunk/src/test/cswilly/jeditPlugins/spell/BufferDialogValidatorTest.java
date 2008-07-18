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
//}}}

///}}}

import cswilly.spell.ValidationDialog;
import cswilly.spell.SpellException;
import cswilly.spell.Result;
import cswilly.spell.WordListValidator;
import cswilly.spell.AspellEngine;
import cswilly.spell.MockEngine;

import static cswilly.jeditPlugins.spell.TestUtils.*;


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
		final BufferDialogValidator valid = new BufferDialogValidator();

		View view = TestUtils.jeditFrame().targetCastedTo(View.class); 
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
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
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
		final BufferDialogValidator valid = new BufferDialogValidator();

		
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
		try{Thread.sleep(2000);}catch(InterruptedException ie){}

		spellDialog.textBox("originalWord").requireText("qwick");
		spellDialog.button("Ignore All").click();
		try{Thread.sleep(2000);}catch(InterruptedException ie){}

		spellDialog.textBox("originalWord").requireText("foxe");
		spellDialog.button("Change").click();
		try{Thread.sleep(2000);}catch(InterruptedException ie){}

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
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		TestUtils.findDialogByTitle("File Not Saved").button(AbstractButtonTextMatcher.withText(JButton.class,"Non")).click();
	}
	
	@Test
	public void testUserDict(){
		final BufferDialogValidator valid = new BufferDialogValidator();

		
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
		AspellEngine engine = null;
		try{
			engine = new AspellEngine( exePath, new String[]{"--lang","en","pipe"});
		}catch(SpellException spe){
			fail("shouldn't throw an exception");
		}
		
		final BufferDialogValidator valid = new BufferDialogValidator();
		valid.setEngine(engine);
				
		final Buffer buffer = TestUtils.openFile(testsDir+"/spellTest.txt");
		//do that so there is one change before spell-checking
		//(to catch an undue undo ;-))
		buffer.insert(0,"hello,\n");
		try{Thread.sleep(1000);}catch(InterruptedException ie){}
		String oldText = buffer.getText(0,buffer.getLength());
		
		valid.setTextArea(TestUtils.jeditFrame().targetCastedTo(View.class).getTextArea());

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
		AspellEngine engine = null;
		try{
			engine = new AspellEngine( exePath, new String[]{"--lang","en","pipe"});
		}catch(SpellException spe){
			fail("shouldn't throw an exception");
		}

		final BufferDialogValidator valid = new BufferDialogValidator();
		valid.setEngine(engine);
		
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
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
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
		AspellEngine engine = null;
		try{
			engine = new AspellEngine( exePath, new String[]{"--lang","en","pipe"});
		}catch(SpellException spe){
			fail("shouldn't throw an exception");
		}
		final BufferDialogValidator valid = new BufferDialogValidator();
		valid.setEngine(engine);
		
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
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.list().selectItem("fox");
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
