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
//}}}

import cswilly.spell.ValidationDialog;


///}}}


public class SpellCheckOptionPaneTest
{
	public static final String ENV_ASPELL_EXE	  = "test-jedit.aspell-exe";
	public static final String ENV_TESTS_DIR	  = "test-tests.dir";
	

	@BeforeClass
	public static void setUpjEdit(){
		TestUtils.setUpjEdit();
	}

	@AfterClass
	public static  void tearDownjEdit(){
		TestUtils.tearDownjEdit();
	}
	
	
	@Test
	public void testExePath(){
		String testsDir = System.getProperty(ENV_TESTS_DIR);
		assertTrue("Forgot to set env. variable '"+ENV_TESTS_DIR+"'",testsDir!=null);

		TestUtils.jeditFrame().menuItemWithPath("Plugins","Plugin Options...").select();
		
		DialogFixture optionsDialog = WindowFinder.findDialog(PluginOptions.class).withTimeout(5000).using(TestUtils.robot());
		
		optionsDialog.tree().selectPath(new TreePath(new Object[]{"Plugins","Spell Check"}));
		JPanelFixture pane = optionsDialog.panel(new GenericTypeMatcher<SpellCheckOptionPane>(){
			@Override protected boolean isMatching(SpellCheckOptionPane ignored) {
				return true;
			}
		});
		
		// test with sink.sh
		pane.textBox("AspellPath").doubleClick().deleteText().enterText(testsDir+"/sink.sh");
		pane.textBox("AspellPath").requireText(testsDir+"/sink.sh");
		pane.button("Refresh").click();
		
		try{
			Thread.sleep(12000);
		}catch(InterruptedException ie){
		}
		TestUtils.robot().printer().printComponents(System.out);
		JDialog dialog1 = (JDialog)TestUtils.robot().finder().find(new ComponentMatcher(){
				private boolean found = false;
				public boolean matches(Component comp){
					if(found)return false;
					if(comp instanceof JDialog){
						System.err.println("got this window : "+((JDialog)comp).getTitle());
						found=true;
						return true;
					}else{
						System.out.println("discarded:"+comp);
						return false;
					}
				}
		});
		try{
			Thread.sleep(500);
		}catch(InterruptedException ie){}
		new DialogFixture(TestUtils.robot(),dialog1).close();
		JDialog dialog2 = (JDialog)TestUtils.robot().finder().find(new ComponentMatcher(){
				private boolean found = false;
				private String title = "I/O Error";
				public boolean matches(Component comp){
					if(found)return false;
					if(comp instanceof Dialog){
						System.err.println("got this window : '"+comp+"'");
						if(title.equals(((Dialog)comp).getTitle()))System.err.println("Right name !");
						found=true;
						return true;
					}else{
						System.out.println("discarded:"+comp);
						return false;
					}
				}
		});
		new DialogFixture(TestUtils.robot(),dialog2).close();
		pane.textBox("AspellPath").requireText(testsDir+"/sink.sh");
		try{
			Thread.sleep(500);
		}catch(InterruptedException ie){}

		// test with non existant
		JTextComponentFixture f = pane.textBox("AspellPath");
		while(!"".equals(f.text()))f.deleteText();
		f.enterText(testsDir+"/NOT_THERE.sh");
		pane.textBox("AspellPath").requireText(testsDir+"/NOT_THERE.sh");
		pane.button("Refresh").click();
		
		DialogFixture alertDialog = DialogByTitleFinder.findByTitle("I/O Error").withTimeout(12000).using(TestUtils.robot());
		alertDialog.close();

		optionsDialog.button(AbstractButtonTextMatcher.withText(JButton.class,"OK")).click();
	}
	

}
