package cswilly.jeditPlugins.spell;


//{{{ Imports

//{{{ 	Java Classpath
import javax.swing.*;
import javax.swing.tree.TreePath;
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
//}}}

import cswilly.spell.ValidationDialog;


///}}}


public class SpellCheckPluginTest
{
	public static final String ENV_ASPELL_EXE	  = "test-jedit.aspell-exe";
	

	@BeforeClass
	public static void setUpjEdit(){
		TestUtils.setUpjEdit();
	}

	@AfterClass
	public static  void tearDownjEdit(){
		TestUtils.tearDownjEdit();
	}
	@Test
	public void testOptions(){
		
		TestUtils.jeditFrame().menuItemWithPath("Plugins","Plugin Options...").select();
		
		DialogFixture optionsDialog = WindowFinder.findDialog(PluginOptions.class).withTimeout(5000).using(TestUtils.robot());
		
		optionsDialog.tree().selectPath(new TreePath(new Object[]{"Plugins","Spell Check"}));
		JPanelFixture pane = optionsDialog.panel(new GenericTypeMatcher<SpellCheckOptionPane>(){
			@Override protected boolean isMatching(SpellCheckOptionPane ignored) {
				return true;
			}
		});
		optionsDialog.close();
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
		String exePath = System.getProperty(ENV_ASPELL_EXE);
		assertTrue("Forgot to set env. variable '"+ENV_ASPELL_EXE+"'",exePath!=null);
		jEdit.setProperty(SpellCheckPlugin.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(SpellCheckPlugin.ASPELL_LANG_PROP,"en");
		final View view = TestUtils.jeditFrame().targetCastedTo(View.class);
		jEdit.newFile(view);
		final Buffer buff = view.getBuffer();
		buff.insert(0,"The quick brown foxe");
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
}
