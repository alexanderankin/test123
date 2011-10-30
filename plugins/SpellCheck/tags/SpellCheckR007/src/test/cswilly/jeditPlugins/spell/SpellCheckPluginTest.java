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

//{{{ 	Java Classpath
import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.concurrent.atomic.AtomicReference;

//}}}

//{{{ 	jEdit
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.options.PluginOptions;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.textarea.Selection;

//}}}

//{{{	junit
//annotations
import org.junit.*;
//usual classes
    import static org.junit.Assert.*;
//}}}

//{{{	FEST...
import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.timing.Pause;
import org.fest.swing.edt.*;
//}}}

import cswilly.spell.ValidationDialog;
import cswilly.spell.SpellException;


//{{{	FEST...
import errorlist.ErrorSource;
import errorlist.ErrorSourceUpdate;
//}}}
///}}}

import org.gjt.sp.jedit.testframework.*;
import org.gjt.sp.jedit.testframework.TestUtils;
import static org.gjt.sp.jedit.testframework.TestUtils.*;
import static cswilly.jeditPlugins.spell.TestUtils.ENV_ASPELL_EXE;

/**
 * test high-level functions of the plugin:
 *	- Option Pane
 *  - Spell-check on save
 *	- interactive spell-checking
 *  - stop()
 *  - spellCheck with lang...
 *  - set buffer's language
 */
public class SpellCheckPluginTest
{	

	@BeforeClass
	public  static void setUpjEdit(){
		System.err.println("beforeClass");
		TestUtils.beforeClass();
	}

	@AfterClass
	public static void tearDownjEdit(){
		System.err.println("afterClass");
		TestUtils.afterClass();
	}
	
	@Test
	public void testOptions(){
		System.err.println("testOptions");
		
		PluginOptionsFixture optionsDialog = TestUtils.pluginOptions();
			
		//TestUtils.selectPath(optionsDialog.tree(),new String[]{"Plugins","Spell Check","General"});
		
		JPanelFixture pane = optionsDialog.optionPane("Spell Check/General","spellcheck.general");
		
		optionsDialog.close();
		//fail("I don't want to succeed");
		//don't do that, as it quits the VM
		//TestUtils.jeditFrame().menuItem(AbstractButtonTextMatcher.withText(JMenuItem.class,"Exit")).select();
		// try{
		// 	runJeditThread.join(10000);
		// }catch(InterruptedException ie){
		// 	fail("don't interrupt me !");
		// }
		// assertTrue(!runJeditThread.isAlive());
	}

	@Test
	public void testInteractiveSpellCheck(){
		System.err.println("testInteractiveSpellCheck");
		String exePath = System.getProperty(ENV_ASPELL_EXE);
		assertTrue("Forgot to set env. variable '"+ENV_ASPELL_EXE+"'",exePath!=null);
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.MAIN_LANGUAGE_PROP,"en");
		jEdit.setProperty(SpellCheckPlugin.ENGINE_MANAGER_PROP,"Aspell");
		final View view = TestUtils.view();
		final Buffer buff = TestUtils.newFile();
		buff.insert(0,"The quick brown foxe");
		//Pause.pause(120000);
		Thread spellThread = new Thread(){
			public void run(){
				SpellCheckPlugin.checkBuffer(view,buff);
			}
		};
		spellThread.start();
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.list().selectItem("fox");
		spellDialog.button("Change").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());
		assertEquals("The quick brown fox",buff.getText(0,buff.getLength()));
	}
	
	@Test
	public void testShowCustomLangSpellDialog(){
		System.err.println("testShowCustomLangSpellDialog");
		String exePath = System.getProperty(ENV_ASPELL_EXE);
		assertTrue("Forgot to set env. variable '"+ENV_ASPELL_EXE+"'",exePath!=null);
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.MAIN_LANGUAGE_PROP,"en-w_accents");

		final Buffer buff = TestUtils.newFile();
		System.err.println("hello");
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					buff.setProperty(SpellCheckPlugin.BUFFER_LANGUAGE_PROP,"en");
					buff.insert(0,"Les licences de la plupart des logiciels\nsont concues pour vous enlever toute libertée");
				}
		});
		final AtomicReference<SpellException> except = new AtomicReference<SpellException>(null);
		Thread spellThread = new Thread(){
			public void run(){
				try{
				SpellCheckPlugin.showCustomLangSpellDialog(TestUtils.view(),buff);
				}catch(SpellException spe){
					except.set(spe);
					spe.printStackTrace(System.err);
				}
			}
		};
		spellThread.start();
		DialogFixture langDialog = WindowFinder.findDialog(EnhancedDialog.class).withTimeout(5000).using(TestUtils.robot());
		Pause.pause(1000);//let dictionaries be loaded

		langDialog.comboBox().selectItem("fr");
		langDialog.button(org.fest.swing.core.matcher.JButtonMatcher.withText("OK")).click();
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(20000).using(TestUtils.robot());

		Pause.pause(1000);//let dictionaries be loaded
		assertEquals("fr",buff.getProperty(SpellCheckPlugin.BUFFER_LANGUAGE_PROP));
		spellDialog.list().selectItem("conçues");
		spellDialog.button("Change").click();
		Pause.pause(1000);//let dictionaries be loaded
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.list().selectItem("liberté");
		spellDialog.button("Change").click();
		try{spellThread.join(5000);}catch(InterruptedException ie){}
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());
		assertEquals(null,except.get());
		assertEquals("Les licences de la plupart des logiciels\nsont conçues pour vous enlever toute liberté",buff.getText(0,buff.getLength()));
		assertEquals("en",buff.getProperty(SpellCheckPlugin.BUFFER_LANGUAGE_PROP));
	}

	@Test
	public void testSpellCheckOnSave(){
		System.err.println("testSpellCheckOnSave");
		String testsDir = System.getProperty(ENV_TESTS_DIR);
		assertTrue("Forgot to set env. variable '"+ENV_TESTS_DIR+"'",testsDir!=null);

		String exePath = System.getProperty(ENV_ASPELL_EXE);
		assertTrue("Forgot to set env. variable '"+ENV_ASPELL_EXE+"'",exePath!=null);
		
		String path = testsDir+"/spellTest.txt";
		
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.MAIN_LANGUAGE_PROP,"en");
		jEdit.setBooleanProperty(SpellCheckPlugin.SPELLCHECK_ON_SAVE_PROP,true);
		jEdit.setProperty(SpellCheckPlugin.ENGINE_MANAGER_PROP,"Aspell");
		
		TestUtils.openFile(path);
		
		TestUtils.jEditFrame().menuItemWithPath("File","Save").click();
		
		final AtomicReference<Boolean> atr = new AtomicReference<Boolean>(Boolean.FALSE);
		EBComponent eb = new EBComponent(){
			public void handleMessage(EBMessage ebm){
				if(ebm instanceof ErrorSourceUpdate){
					System.err.println("got error update : "+ebm);
					if(ErrorSourceUpdate.ERROR_SOURCE_ADDED.equals(((ErrorSourceUpdate)ebm).getWhat()))
						atr.set(Boolean.TRUE);
				}
			}
		};
		
		EditBus.addToBus(eb);
		
		Pause.pause(10000);
		
		assertEquals(Boolean.TRUE,atr.get());
		
		ErrorSource[] sources = ErrorSource.getErrorSources();
		ErrorSource spellError = null;
		for(int i=0;i<sources.length;i++){
			if(sources[i] instanceof ErrorListValidator){
				spellError = sources[i];
			}
		}
		assertNotNull(spellError);
		
		assertTrue(6==spellError.getFileErrorCount(path));
		EditBus.removeFromBus(eb);
	}
	
	
	@Test
	public void testStop(){
		System.err.println("testStop");
		
		final PluginJAR jar = jEdit.getPlugin(SpellCheckPlugin.class.getName()).getPluginJAR();
		try{
		SwingUtilities.invokeAndWait(
			new Runnable(){
				public void run(){
					jar.deactivatePlugin(false);
				}
			});
		}catch(Exception ie){}
		ErrorSource[] sources = ErrorSource.getErrorSources();
		ErrorSource spellError = null;
		for(int i=0;i<sources.length;i++){
			if(sources[i] instanceof ErrorListValidator){
				spellError = sources[i];
			}
		}
		
		assertNull(spellError);
		
	}

	@Test
	public void testMultipleBuffers(){
		System.err.println("testMultipleBuffers");
		String exePath = System.getProperty(ENV_ASPELL_EXE);
		assertTrue("Forgot to set env. variable '"+ENV_ASPELL_EXE+"'",exePath!=null);
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.MAIN_LANGUAGE_PROP,"en");
		jEdit.setProperty(SpellCheckPlugin.ENGINE_MANAGER_PROP,"Aspell");

		
		final View view = TestUtils.view();
		
		TestUtils.newFile();
		final Buffer buffer1 = view.getBuffer();

		try{
		SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					buffer1.insert(0,"The wiek comes to an end");
				}
		});
		}catch(Exception ie){}

		TestUtils.newFile();

		try{
		SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					view.splitHorizontally();
				}
		});
		}catch(Exception ie){}

		
		final String oldText1 = buffer1.getText(0,buffer1.getLength());
		
		final Buffer buffer2 = buffer1.getNext();

		Pause.pause(2000);
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					view.getEditPanes()[0].setBuffer(buffer1);
					EditPane pane2 = view.getEditPanes()[1];
					pane2.setBuffer(buffer2);
					buffer2.insert(0,"The qwick brown foxe");
					pane2.getTextArea().setSelection(new Selection.Range(0,10));
				}
		});


		//select the second pane and spell-check in the first
		//(throws an exception) 
		
		final AtomicReference<Boolean> except = new AtomicReference<Boolean>(Boolean.FALSE);
		Thread spellThread = new Thread(){
			public void run(){
				try{
					SpellCheckPlugin.checkBuffer(view,buffer1);
				}catch(IllegalArgumentException iae){
					except.set(Boolean.TRUE);
					try{
					SpellCheckPlugin.checkBuffer(view,buffer2);
					}catch(Exception e){
						e.printStackTrace(System.err);
					}
				}
			}
		};
		spellThread.start();
		
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		
		assertEquals("SpellCheckPlugin didn't throw an exception on view/buffer mismatch",Boolean.TRUE,except.get());
		
		Pause.pause(1000);
		spellDialog.list().selectItem("quick");
		spellDialog.button("Change").click();
		//update : foxe not spell-checked as it's out of selection
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());
		assertEquals("The quick brown foxe",buffer2.getText(0,buffer2.getLength()));

		assertEquals("The wiek comes to an end",buffer1.getText(0,buffer1.getLength()));
	}
	
	@Test
	public void testMarkupModes(){
		System.err.println("testMarkupModes");
		String exePath = System.getProperty(ENV_ASPELL_EXE);
		assertTrue("Forgot to set env. variable '"+ENV_ASPELL_EXE+"'",exePath!=null);

		String testDir = System.getProperty(ENV_TESTS_DIR);
		assertTrue("Forgot to set env. variable '"+ENV_TESTS_DIR+"'",testDir!=null);

		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.MAIN_LANGUAGE_PROP,"en");

		final View view = TestUtils.view();
		Pause.pause(5000);
		view.unsplit();
		final Buffer buff = TestUtils.openFile(testDir+"/latex-file.tex");

		//with "none" filter
		jEdit.setProperty(AspellEngineManager.ASPELL_MARKUP_MODE_PROP,
			AspellEngineManager.AspellMarkupMode.NO_MARKUP_MODE.toString());
		
		Thread spellThread = new Thread(){
			public void run(){
				SpellCheckPlugin.checkBuffer(view,buff);
			}
		};
		spellThread.start();
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.textBox("originalWord").requireText("documentclass");
		spellDialog.button("Cancel").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());
		
		
		//with manual markup mode
		jEdit.setProperty(AspellEngineManager.ASPELL_MARKUP_MODE_PROP,
			AspellEngineManager.AspellMarkupMode.MANUAL_MARKUP_MODE.toString());
		assertEquals("tex",
					jEdit.getProperty(AspellEngineManager.FILTERS_PROP+".latex"));

		spellThread = new Thread(){
			public void run(){
				SpellCheckPlugin.checkBuffer(view,buff);
			}
		};
		spellThread.start();
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(5000).using(TestUtils.robot());
		spellDialog.textBox("originalWord").requireText("mispelled");
		spellDialog.button("Cancel").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
	}
	
	@Test
	public void testIgnoreAll(){
		System.err.println("testIgnoreAll");
		String exePath = System.getProperty(ENV_ASPELL_EXE);
		assertTrue("Forgot to set env. variable '"+ENV_ASPELL_EXE+"'",exePath!=null);

		String testDir = System.getProperty(ENV_TESTS_DIR);
		assertTrue("Forgot to set env. variable '"+ENV_TESTS_DIR+"'",testDir!=null);

		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.MAIN_LANGUAGE_PROP,"en");

		final View view = TestUtils.view();

		final Buffer buffer = TestUtils.newFile();

		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					buffer.insert(0,"The wiek comes to an end\nIndeed it's the end of the wiek");
		}});
		
		final String oldText = buffer.getText(0,buffer.getLength());

		Pause.pause(1000);

		
		Thread spellThread = new Thread(){
			public void run(){
				SpellCheckPlugin.checkBuffer(view,buffer);
			}
		};
		spellThread.start();
		DialogFixture spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.textBox("originalWord").requireText("wiek");
		spellDialog.button("Ignore All").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());
		
		assertEquals(oldText,buffer.getText(0,buffer.getLength()));
		
		//persistent accross invocations ?
		spellThread = new Thread(){
			public void run(){
				SpellCheckPlugin.checkBuffer(view,buffer);
			}
		};
		spellThread.start();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());
		
		//test clear...
		TestUtils.jEditFrame().menuItemWithPath("Plugins","Spell Check","Clear Ignored Words").click();
		
		spellThread = new Thread(){
			public void run(){
				SpellCheckPlugin.checkBuffer(view,buffer);
			}
		};
		spellThread.start();
		spellDialog = WindowFinder.findDialog(ValidationDialog.class).withTimeout(10000).using(TestUtils.robot());
		spellDialog.textBox("originalWord").requireText("wiek");
		spellDialog.button("Cancel").click();
		try{
			spellThread.join(5000);
		}catch(InterruptedException ie){}
		assertTrue("spell-checking didn't finish", !spellThread.isAlive());
		
		assertEquals(oldText,buffer.getText(0,buffer.getLength()));
	}
}
