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

package cswilly.spell;


//{{{ Imports

import java.io.*;
import java.util.concurrent.atomic.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

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

import cswilly.jeditPlugins.spell.TestUtils;


/**
 * Test the functions of ValidationDialog
 *	- displays a result correctly
 *	- list of suggestions
 *	- result of button-clicking
 */
public class ValidationDialogTest{
	
	@BeforeClass
	public static void setUpjEdit(){
		TestUtils.setUpjEdit();
		//TestUtils.robot().settings().delayBetweenEvents(200);
	}

	@AfterClass
	public static  void tearDownjEdit(){
		TestUtils.tearDownjEdit();
	}

	
	@Test
	public void testDisplay(){
		final ValidationDialog valid = new ValidationDialog(TestUtils.view());

		final Result resS  = new Result(5,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick");
		
		Thread spellThread = new Thread(){
			public void run(){
				valid.getUserAction(resS.getOriginalWord(),resS.getSuggestions(),false,false);
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(2000).using(TestUtils.robot());
		spellDialog.textBox("originalWord").requireText("qwick");
		spellDialog.textBox("changeTo").requireText("wick");

		List<String> sugg = Arrays.asList(spellDialog.list().contents());
		List<String> origSugg = resS.getSuggestions();

		assertEquals(origSugg.size(),sugg.size());

		for(int i=0;i<origSugg.size();i++)
			assertTrue(sugg.contains(origSugg.get(i)));
		
		spellDialog.button("Change").click();
		
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue("validation didn't return", !spellThread.isAlive());

		final Result resNONE  = new Result(5,Result.NONE,null,"qwack");

		spellThread = new Thread(){
			public void run(){
				valid.getUserAction(resNONE.getOriginalWord(),resNONE.getSuggestions(),false,false);
			}
		};
		spellThread.start();

		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.textBox("originalWord").requireText("qwack");
		spellDialog.textBox("changeTo").requireText("qwack");

		sugg = Arrays.asList(spellDialog.list().contents());

		assertEquals(1,sugg.size());
		
		spellDialog.button("Change").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue("validation didn't return", !spellThread.isAlive());
	}

	@Test
	public void testList(){
		final ValidationDialog valid = new ValidationDialog(TestUtils.view());

		final Result resS  = new Result(5,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick");
		
		Thread spellThread = new Thread(){
			public void run(){
				valid.getUserAction(resS.getOriginalWord(),resS.getSuggestions(),false,false);
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
		
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertEquals("wick",valid.getSelectedWord());
		assertTrue("validation didn't return", !spellThread.isAlive());
	}
	
	/** this one is very weak...
	 */
	@Test
	public void testReturn(){
		final ValidationDialog valid = new ValidationDialog(TestUtils.view());

		final Result resS  = new Result(5,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick");
		
		final AtomicReference<ValidationDialog.UserAction> action = new AtomicReference<ValidationDialog.UserAction>(null);
		
		Thread spellThread = new Thread(){
			public void run(){
				action.set(valid.getUserAction(resS.getOriginalWord(),resS.getSuggestions(),false,false));
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		
		spellDialog.button("Change").click();
		
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue("validation didn't return", !spellThread.isAlive());
				
		assertEquals(ValidationDialog.CHANGE,action.get());

		action.set(null);

		spellThread = new Thread(){
			public void run(){
				action.set(valid.getUserAction(resS.getOriginalWord(),resS.getSuggestions(),false,false));
			}
		};
		spellThread.start();
		
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		
		spellDialog.button("Add").click();
		
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue("validation didn't return", !spellThread.isAlive());
				
		assertEquals(ValidationDialog.ADD,action.get());

		// Test dialog closed
		action.set(null);
		
		spellThread = new Thread(){
			public void run(){
				action.set(valid.getUserAction(resS.getOriginalWord(),resS.getSuggestions(),false,false));
			}
		};
		spellThread.start();
		
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		
		spellDialog.close();
		
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue("validation didn't return", !spellThread.isAlive());
				
		assertEquals(ValidationDialog.CANCEL,action.get());

		//test Escape
		action.set(null);
		spellThread = new Thread(){
			public void run(){
				action.set(valid.getUserAction(resS.getOriginalWord(),resS.getSuggestions(),false,false));
			}
		};
		spellThread.start();
		
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		
		spellDialog.pressKey(KeyEvent.VK_ESCAPE);
		spellDialog.releaseKey(KeyEvent.VK_ESCAPE);
		
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue("validation didn't return", !spellThread.isAlive());
				
		assertEquals(ValidationDialog.CANCEL,action.get());
	}


	@Test
	public void testPrevious(){
		final ValidationDialog valid = new ValidationDialog(TestUtils.view());

		final Result resS  = new Result(5,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick");
		
		final AtomicReference<ValidationDialog.UserAction> action = new AtomicReference<ValidationDialog.UserAction>(null);
		
		Thread spellThread = new Thread(){
			public void run(){
				action.set(valid.getUserAction(resS.getOriginalWord(),resS.getSuggestions(),false,false));
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.button("Previous").requireDisabled();
		
		spellDialog.button("Change").click();
		
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue("validation didn't return", !spellThread.isAlive());
				
		assertEquals(ValidationDialog.CHANGE,action.get());

		action.set(null);

		spellThread = new Thread(){
			public void run(){
				action.set(valid.getUserAction(resS.getOriginalWord(),resS.getSuggestions(),false,true));
			}
		};
		spellThread.start();
		
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		
		spellDialog.button("Previous").requireEnabled();
		spellDialog.button("Cancel").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		
		assertTrue("validation didn't return", !spellThread.isAlive());
				
		assertEquals(ValidationDialog.CANCEL,action.get());

		action.set(null);
	}
}
