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
import java.awt.*;
//}}}

//{{{ 	jEdit
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.options.PluginOptions;

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
import org.fest.swing.finder.DialogByTitleFinder;
import static org.fest.swing.fixture.TableCell.row;
//import org.fest.swing.cell.JTreeCellReader;
//}}}

import cswilly.spell.ValidationDialog;


///}}}

import static cswilly.jeditPlugins.spell.TestUtils.*;

/**
 * Test the functionality of the Options pane
 *  - resistant to sink/invalid/inexistant executable
 *	- test UI
 */
public class SpellCheckOptionPaneTest
{
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
	public void testExePath(){
		jEdit.setProperty(SpellCheckPlugin.ASPELL_EXE_PROP,exePath);

		TestUtils.jeditFrame().menuItemWithPath("Plugins","Plugin Options...").select();
		
		DialogFixture optionsDialog = WindowFinder.findDialog(PluginOptions.class).withTimeout(5000).using(TestUtils.robot());
		
		optionsDialog.tree().selectPath(new TreePath(new String[]{"Plugins","Spell Check"}));
		JPanelFixture pane = optionsDialog.panel(new GenericTypeMatcher<SpellCheckOptionPane>(){
			@Override protected boolean isMatching(SpellCheckOptionPane ignored) {
				return true;
			}
		});
		
		JTextComponentFixture f = pane.textBox("AspellPath");
		
		f.select(f.text()).deleteText().enterText(testsDir+"/sink.sh");
		f.requireText(testsDir+"/sink.sh");
		pane.button("Refresh").click();
		try{Thread.sleep(11000);}catch(InterruptedException ie){}
		//TestUtils.robot().printer().printComponents(System.out);
		DialogFixture alertDialog = TestUtils.findDialogByTitle("I/O Error");
		alertDialog.close();
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		alertDialog.requireNotVisible();
		f.requireText(testsDir+"/sink.sh");

		// test with non existant
		f.select(f.text()).deleteText().enterText(testsDir+"/NOT_THERE.sh");
		pane.button("Refresh").click();
		
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		alertDialog = TestUtils.findDialogByTitle("I/O Error");
		alertDialog.button(AbstractButtonTextMatcher.withText(JButton.class,"OK")).click();	

		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		alertDialog.requireNotVisible();


		// test with wrong
		f.select(f.text()).deleteText().enterText(testsDir+"/spellcheck_ok.sh");
		pane.button("Refresh").click();
		
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		alertDialog = TestUtils.findDialogByTitle("I/O Error");
		alertDialog.close();
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		alertDialog.requireNotVisible();


		optionsDialog.button(AbstractButtonTextMatcher.withText(JButton.class,"OK")).click();
		
		assertEquals(testsDir+"/spellcheck_ok.sh",jEdit.getProperty(SpellCheckPlugin.ASPELL_EXE_PROP));
	}
	
	@Test
	public void testModes(){
		jEdit.setProperty(SpellCheckPlugin.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.ASPELL_MARKUP_MODE_PROP,"aspellManualMarkupMode");
		
		
		TestUtils.jeditFrame().menuItemWithPath("Plugins","Plugin Options...").select();
		
		DialogFixture optionsDialog = WindowFinder.findDialog(PluginOptions.class).withTimeout(5000).using(TestUtils.robot());
		
		optionsDialog.tree().selectPath(new TreePath(new String[]{"Plugins","Spell Check"}));
		JPanelFixture pane = optionsDialog.panel(new GenericTypeMatcher<SpellCheckOptionPane>(){
			@Override protected boolean isMatching(SpellCheckOptionPane ignored) {
				return true;
			}
		});
		
		/* buttons in place */
		for(SpellCheckPlugin.AspellMarkupMode mode: SpellCheckPlugin.AspellMarkupMode.values()){
			pane.radioButton(mode.toString()).requireVisible();
			assertTrue(pane.radioButton(mode.toString()).text().length()>0);
		}
		pane.radioButton(SpellCheckPlugin.AspellMarkupMode.MANUAL_MARKUP_MODE.toString()).requireSelected();
		
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		
		/* buttons effective */
		pane.radioButton(SpellCheckPlugin.AspellMarkupMode.NO_MARKUP_MODE.toString()).click();
		pane.radioButton(SpellCheckPlugin.AspellMarkupMode.MANUAL_MARKUP_MODE.toString()).requireNotSelected();
		
		optionsDialog.button(AbstractButtonTextMatcher.withText(JButton.class,"OK")).click();
		
		assertEquals(SpellCheckPlugin.AspellMarkupMode.NO_MARKUP_MODE.toString(),
			jEdit.getProperty(SpellCheckPlugin.ASPELL_MARKUP_MODE_PROP));
	}

	@Test public void testFilters(){
		jEdit.setProperty(SpellCheckPlugin.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.ASPELL_MARKUP_MODE_PROP,"aspellManualMarkupMode");

		TestUtils.jeditFrame().menuItemWithPath("Plugins","Plugin Options...").select();
		
		DialogFixture optionsDialog = WindowFinder.findDialog(PluginOptions.class).withTimeout(5000).using(TestUtils.robot());
		
		TestUtils.robot().settings().timeoutToBeVisible(10000);
		JTreeFixture tree = optionsDialog.tree();
		/*tree.cellReader(new JTreeCellReader(){
				public String valueAt(JTree tree, Object modelValue){
					//System.err.println("TREE:"+tree+" : "+tree.isRootVisible());
					System.err.println("NODE:"+modelValue.getClass()+"  "+modelValue);
					if(modelValue instanceof OptionGroup){
						OptionGroup o = (OptionGroup)modelValue;
						System.err.println("NAME:"+o.getName());
						System.err.println("Label:"+o.getLabel());
						return o.getLabel();
					}
					return modelValue.toString();
				}
		})*/;
		//tree.targetCastedTo(JTree.class).setRootVisible(false);
		//tree.selectPath("Plugins/SpellCheck");
		tree.selectPath(new TreePath(new String[]{"Plugins","Spell Check"}));
		JPanelFixture pane = optionsDialog.panel(new GenericTypeMatcher<SpellCheckOptionPane>(){
			@Override protected boolean isMatching(SpellCheckOptionPane ignored) {
				return true;
			}
		});
		
		
		/* table in place */
		JTableFixture table = pane.table("filtersTable");
		table.requireVisible();
		Mode[]modes = jEdit.getModes();
		
		String firstMode=modes[0].getName();
		int indexHtml = -1;
		
		for(int i=0;i<modes.length;i++){
			assertEquals(modes[i].getName(),table.cell(row(i).column(0)).content());
			if("html".equals(modes[i].getName()))indexHtml=i;
		}
		
		assertTrue(indexHtml!=-1);
		
		
		table.cell(row(0).column(1)).click();
		
		pane.comboBox("filtersCombo").requireSelection("AUTO").selectItem("sgml");

		assertEquals("sgml",table.cell(row(0).column(1)).content());
		
		//test default (in SpellCheck.properties) filter
		table.cell(row(indexHtml).column(1)).click();
		pane.comboBox("filtersCombo").requireSelection("sgml");
		
		optionsDialog.button(AbstractButtonTextMatcher.withText(JButton.class,"OK")).click();
		
		assertEquals("sgml",jEdit.getProperty(SpellCheckPlugin.FILTERS_PROP+"."+firstMode));
		
	}
	
	@Test
	public void testAdditional()
	{
		jEdit.setProperty(SpellCheckPlugin.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.ASPELL_MARKUP_MODE_PROP,"aspellManualMarkupMode");

		TestUtils.jeditFrame().menuItemWithPath("Plugins","Plugin Options...").select();
		
		DialogFixture optionsDialog = WindowFinder.findDialog(PluginOptions.class).withTimeout(5000).using(TestUtils.robot());
		
		optionsDialog.tree().selectPath(new TreePath(new String[]{"Plugins","Spell Check"}));
		
		
		//additional parameters
		optionsDialog.textBox("AdditionalParameters").requireEditable().requireEmpty();
		optionsDialog.textBox("AdditionalParameters").enterText("TEST this");
		
		//spellcheck on save
		optionsDialog.checkBox("SpellCheckOnSave").requireNotSelected().click();
		

		optionsDialog.button(AbstractButtonTextMatcher.withText(JButton.class,"OK")).click();

		//effective?
		assertEquals("TEST this", jEdit.getProperty(SpellCheckPlugin.ASPELL_OTHER_PARAMS_PROP));
		assertTrue(jEdit.getBooleanProperty(SpellCheckPlugin.SPELLCHECK_ON_SAVE_PROP));
		jEdit.setProperty(SpellCheckPlugin.ASPELL_OTHER_PARAMS_PROP,"");
		jEdit.setBooleanProperty(SpellCheckPlugin.SPELLCHECK_ON_SAVE_PROP,false);
		//this was funny : unsetProperty() called, assertEquals(null) succeeds, but
		//property is still saved
		//assertEquals(null, jEdit.getProperty(SpellCheckPlugin.ASPELL_OTHER_PARAMS_PROP));
		// so re run option pane to save an empty value for other params 
		TestUtils.jeditFrame().menuItemWithPath("Plugins","Plugin Options...").select();
		optionsDialog = WindowFinder.findDialog(PluginOptions.class).withTimeout(5000).using(TestUtils.robot());
		optionsDialog.tree().selectPath(new TreePath(new String[]{"Plugins","Spell Check"}));
		optionsDialog.textBox("AdditionalParameters").requireEmpty();
		optionsDialog.button(AbstractButtonTextMatcher.withText(JButton.class,"OK")).click();
		
		
	}
}
