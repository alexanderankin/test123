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
import java.awt.*;
import java.beans.*;
//}}}

//{{{ 	jEdit
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.options.PluginOptions;
import org.gjt.sp.jedit.gui.EnhancedDialog;

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
//}}}


///{{{ SpellCheck
import org.gjt.sp.jedit.testframework.TestUtils;
import static org.gjt.sp.jedit.testframework.TestUtils.*;
import static cswilly.jeditPlugins.spell.TestUtils.ENV_ASPELL_EXE;
import cswilly.spell.SpellException;
///}}}

import common.gui.OkCancelButtons;
///}}}


/**
 * test the UI for selecting buffer's language
 *	- normal operation : the user selects a language
 *  - error	: aspell outputs wrong dictionary names
 */
public class DictionaryPickerTest
{	

	@BeforeClass
	public  static void setUpjEdit(){
		TestUtils.beforeClass();
	}

	@AfterClass
	public static void tearDownjEdit(){
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
	public void testSelect(){
		String exePath = System.getProperty(ENV_ASPELL_EXE);
		assertTrue("Forgot to set env. variable '"+ENV_ASPELL_EXE+"'",exePath!=null);
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.MAIN_LANGUAGE_PROP,"en");

		
		final DictionaryPicker dp = new DictionaryPicker(new AspellEngineManager(),"en");
		
		final JDialog dialog = dp.asDialog(TestUtils.view());
		
		Thread pickThread = new Thread(){
			public void run(){
				dp.getRefreshAction().actionPerformed(null);
				dialog.setVisible(true);
			}
		};
		
		pickThread.start();
		
		DialogFixture langDialog = WindowFinder.findDialog(EnhancedDialog.class).withTimeout(5000).using(TestUtils.robot());
		try{Thread.sleep(5000);}catch(InterruptedException ie){}//let dictionaries be loaded
		
		langDialog.comboBox().selectItem("fr");
		langDialog.button(org.fest.swing.core.matcher.JButtonMatcher.withText("OK")).click();
		
		try{pickThread.join(5000);}catch(InterruptedException ie){}
		if(pickThread.isAlive())fail("Didn't terminate");
		assertTrue("confirm didn't work",dp.getPropertyStore().get(DictionaryPicker.CONFIRMED_PROP)!=null);
		assertEquals("fr",dp.getPropertyStore().get(DictionaryPicker.LANG_PROP));
		assertEquals(null,dp.getPropertyStore().get(DictionaryPicker.ERROR_PROP));
	}

	@Test
	public void testError(){
		String exePath = System.getProperty(ENV_TESTS_DIR);
		assertTrue("Forgot to set env. variable '"+ENV_TESTS_DIR+"'",exePath!=null);
		exePath+="/welcome_then_sink.sh";
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.MAIN_LANGUAGE_PROP,"en");

		
		final DictionaryPicker dp = new DictionaryPicker(new AspellEngineManager(),"en");
		

		final JDialog dialog = dp.asDialog(TestUtils.view());
		
		final AtomicReference<String> error = new AtomicReference<String>(null);
		dp.getPropertyStore().addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent pe){
					if(DictionaryPicker.ERROR_PROP.equals(pe.getPropertyName()) && pe.getNewValue()!=null)
						error.set(pe.getNewValue().toString());
				}
		});

		Thread pickThread = new Thread(){
			public void run(){
				dp.getRefreshAction().actionPerformed(null);
				dialog.setVisible(true);
			}
		};
		
		pickThread.start();
		
		DialogFixture langDialog = WindowFinder.findDialog(EnhancedDialog.class).withTimeout(5000).using(TestUtils.robot());
		try{Thread.sleep(5000);}catch(InterruptedException ie){}//let dictionaries be loaded
		
		
		assertTrue("didn't get an exception", error.get()!=null);
		assertEquals(error.get(),langDialog.textBox("error-report").text());
		langDialog.button(org.fest.swing.core.matcher.JButtonMatcher.withText("Cancel")).click();

	}
	@Test
	public void testRefresh(){
		String exePath = System.getProperty(ENV_TESTS_DIR);
		assertTrue("Forgot to set env. variable '"+ENV_TESTS_DIR+"'",exePath!=null);
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath+"/sink.sh");
		jEdit.setProperty(SpellCheckPlugin.MAIN_LANGUAGE_PROP,"en");

		
		final DictionaryPicker dp = new DictionaryPicker(new AspellEngineManager(),"en");
				
		final Object lock = new Object();
		dp.getPropertyStore().addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent pe){
					if(DictionaryPicker.ERROR_PROP.equals(pe.getPropertyName()) && pe.getNewValue()!=null)
						synchronized(lock){
							lock.notify();
						}
				}
		});

		Thread pickThread = new Thread(){
			public void run(){
				synchronized(lock){
					dp.getRefreshAction().actionPerformed(null);
					try{
					lock.wait();
					}catch(InterruptedException ie){}
				}
			}
		};
		
		pickThread.start();
						
		try{Thread.sleep(1000);}catch(InterruptedException ie){}//let sink be launched
		
		assertTrue("sink should be blocking",pickThread.isAlive());
		
		dp.cancel();
		try{Thread.sleep(1000);}catch(InterruptedException ie){}//let sink be killed
		
		assertTrue("sink should be killed",!pickThread.isAlive());

		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath+"/list-dicts.sh");
		try{Thread.sleep(1000);}catch(InterruptedException ie){}


		final JDialog dialog = dp.asDialog(TestUtils.view());
		
		pickThread = new Thread(){
			public void run(){
				dp.getRefreshAction().actionPerformed(null);
				dialog.setVisible(true);
			}
		};
		
		pickThread.start();
		
		DialogFixture langDialog = WindowFinder.findDialog(EnhancedDialog.class).withTimeout(5000).using(TestUtils.robot());
		try{Thread.sleep(5000);}catch(InterruptedException ie){}//let dictionaries be loaded
		
		
		langDialog.comboBox().selectItem("fr");
		langDialog.button(org.fest.swing.core.matcher.JButtonMatcher.withText("OK")).click();

		try{pickThread.join(5000);}catch(InterruptedException ie){}
		if(pickThread.isAlive())fail("Didn't terminate");
		assertTrue("confirm didn't work",dp.getPropertyStore().get(DictionaryPicker.CONFIRMED_PROP)!=null);
		assertEquals("fr",dp.getPropertyStore().get(DictionaryPicker.LANG_PROP));
		assertEquals(null,dp.getPropertyStore().get(DictionaryPicker.ERROR_PROP));

	}
}

