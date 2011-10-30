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
import java.util.*;
import javax.swing.*;

//{{{ 	jEdit
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
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
import cswilly.spell.ChangeWordAction;

import static org.gjt.sp.jedit.testframework.TestUtils.*;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.*;
import static cswilly.jeditPlugins.spell.TestUtils.ENV_ASPELL_EXE;


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
		TestUtils.beforeClass();

	}
	
	@AfterClass
	public static  void tearDownjEdit(){
		TestUtils.afterClass();
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
	public void testIterate(){
		final Buffer buf = TestUtils.newFile();
		final String text = 
		 "* This program is free software; you can redistribute it and/or\n"
		+"* modify it under the terms of the GNU General Public License\n"
		+"* as published by the Free Software Foundation; either version 2\n"
		+"\n"
		+"* of the License, or any later version.";
		
		try{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				buf.insert(0,text);}});
		}catch(Exception e){}
		String[] lines = text.split("\n");
		BufferSpellChecker source = new BufferSpellChecker(TestUtils.view().getTextArea(),true);
		source.setAcceptAllTokens();
		source.start();
		
		assertEquals( "* This program is free software; you can redistribute it and/or",
			source.getNextLine());
		assertEquals(0,source.getLineNumber());

		assertEquals( "* modify it under the terms of the GNU General Public License",
			source.getNextLine());
		assertEquals(1,source.getLineNumber());

		assertEquals( "* as published by the Free Software Foundation; either version 2",
			source.getNextLine());
		assertEquals(2,source.getLineNumber());

		/* empty lines are now skipped */

		assertEquals( "* of the License, or any later version.",
			source.getNextLine());
		assertEquals(4,source.getLineNumber());

		assertEquals( "* as published by the Free Software Foundation; either version 2",
			source.getPreviousLine());
		assertEquals(2,source.getLineNumber());
		
		assertEquals( "* modify it under the terms of the GNU General Public License",
			source.getPreviousLine());
		assertEquals(1,source.getLineNumber());

		assertEquals( "* as published by the Free Software Foundation; either version 2",
			source.getNextLine());
		assertEquals(2,source.getLineNumber());
		
		assertEquals( "* modify it under the terms of the GNU General Public License",
			source.getPreviousLine());
		assertEquals(1,source.getLineNumber());
		
		TestUtils.close(TestUtils.view(),buf);
	}

	@Test
	public void testEmptyApply(){
		final Buffer buf = TestUtils.newFile();
		final JEditTextArea area = TestUtils.view().getTextArea();
		final String text = "This program is free software.\n\n";
		
		try{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				buf.insert(0,text);}});
		}catch(Exception e){}
		BufferSpellChecker source = new BufferSpellChecker(area,true);
		source.start();
		source.done();
		
		try{
		source.apply(new ArrayList<ChangeWordAction>());
		}catch(SpellException e){
			fail("shouldn't throw "+e);
		}
		try{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				buf.undo(area);}});
		}catch(Exception e){}
			
		assertEquals(0,buf.getLength());
	}

	
	@Test
	public void testApply(){
		final Buffer buf = TestUtils.newFile();
		final String text = 
		 "* This programme is payware software; you can redistribute it and/or\n"
		+"* modify it under the terms of the GNU General Public License\n"
		+"* has published by the Free Software Foundation; either version 2\n"
		+"\n"
		+"* of the License, or any later version.";
		
		try{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				buf.insert(0,text);}});
		}catch(Exception e){}

		final JEditTextArea area = TestUtils.view().getTextArea();

		BufferSpellChecker source = new BufferSpellChecker(area,true);
		
		List<ChangeWordAction> changes = new ArrayList<ChangeWordAction>();
		changes.add(new ChangeWordAction(null,0,8,"programme","program"));
		changes.add(new ChangeWordAction(null,0,21,"payware","free"));
		changes.add(new ChangeWordAction(null,2,3,"has","as"));
		
		source.start();
		source.done();
		try{
		source.apply(changes);
		}catch(SpellException e){
			fail("shouldn't throw "+e);
		}
		assertEquals(
		 "* This program is free software; you can redistribute it and/or\n"
		+"* modify it under the terms of the GNU General Public License\n"
		+"* as published by the Free Software Foundation; either version 2\n"
		+"\n"
		+"* of the License, or any later version.",
		buf.getText(0,buf.getLength()));

		try{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				buf.undo(TestUtils.view().getTextArea());}});
		}catch(Exception e){}
			
		assertEquals(text,buf.getText(0,buf.getLength()));
		
		TestUtils.close(TestUtils.view(),buf);
	}

	@Test
	public void testApplyNoStart(){
		final Buffer buf = TestUtils.newFile();
		final String text = 
		 "* This programme is free software; you can redistribute it and/or\n";
		
		try{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				buf.insert(0,text);}});
		}catch(Exception e){}

		final JEditTextArea area = TestUtils.view().getTextArea();

		BufferSpellChecker source = new BufferSpellChecker(area,true);
		
		List<ChangeWordAction> changes = new ArrayList<ChangeWordAction>();
		changes.add(new ChangeWordAction(null,0,8,"programme","program"));
		
		try{
		source.apply(changes);
		fail("should throw an exception");
		}catch(SpellException e){
		}
	}
	
	@Test
	public void testSelection(){
		final Buffer buf = TestUtils.newFile();
		final String text = 
		 "* This program is free software; you can redistribute it and/or\n"
		+"* modify it under the terms of the GNU General Public License\n"
		+"* as published by the Free Software Foundation; either version 2\n"
		+"\n"
		+"* of the License, or any later version.";
		
		try{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				buf.insert(0,text);
			}});
		}catch(Exception e){}
		try{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				TestUtils.view().getTextArea().setSelection(new Selection.Range(135,230));
			}});
		}catch(Exception e){}
		try{Thread.sleep(1000);}catch(Exception e){}
		BufferSpellChecker source = new BufferSpellChecker(TestUtils.view().getTextArea(),false);
		source.setAcceptAllTokens();
		source.start();
		
		assertEquals( "* as published by the Free Software Foundation; either version 2",
			source.getNextLine());

		/* empty lines are now skipped */

		assertEquals( "* of the License, or any later version.",
			source.getNextLine());

		assertEquals( null,
			source.getNextLine());
		
		TestUtils.close(TestUtils.view(),buf);
	}
	
	@Test
	public void testContext(){
		final Buffer buf = TestUtils.openFile(testsDir+"/a.cpp");
		assertEquals("c++",buf.getMode().getName());//just to be sure
		try{Thread.sleep(10000);}catch(Exception e){}

		BufferSpellChecker source = new BufferSpellChecker(TestUtils.view().getTextArea(),true);
		source.start();
		
		source.getNextLine();
		assertEquals(0,source.getLineNumber());

		source.getNextLine();
		assertEquals(3,source.getLineNumber());

		assertEquals(null,source.getNextLine());


		TestUtils.close(TestUtils.view(),buf);
	}
}
