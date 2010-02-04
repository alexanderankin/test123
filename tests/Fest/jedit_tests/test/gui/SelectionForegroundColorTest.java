package test.gui;

import static org.junit.Assert.assertTrue;

import java.awt.Color;

import javax.swing.SwingUtilities;

import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.options.GlobalOptions;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SelectionForegroundColorTest
{
    @BeforeClass
    public static void setUpjEdit() {
        TestUtils.beforeClass();
    }

    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }

    @Test
    public void testOptionPane() {
        // set the jEdit properties for the option pane to known values
        jEdit.setBooleanProperty("view.selectionFg", false);
        jEdit.setColorProperty("view.selectionFgColor", Color.white);

        // open the options and select the options pane
        TestUtils.jEditFrame().menuItemWithPath("Utilities",
        	"Global Options...").click();
        DialogFixture optionsDialog = WindowFinder.findDialog(
        	GlobalOptions.class).withTimeout(5000).using(TestUtils.robot());
        TestUtils.selectPath(optionsDialog.tree(), new String[] {
        	"jEdit", "Text Area"});
        JPanelFixture pane = optionsDialog.panel("textarea");
        assertTrue("TextAreaOptionPane not found", pane != null);

        // test that all checkboxes are present and click them
        JCheckBoxFixture checkbox = pane.checkBox("selectionFg");
        assertTrue("Cannot find selectionFg checkbox in TextAreaOptionPane",
        	checkbox != null);
        checkbox.requireNotSelected();
        checkbox.click();

        // click the OK button on the options dialog
        TestUtils.clickButton(optionsDialog, "OK");

        // wait a second to make sure jEdit has time to save the properties
        Pause.pause( 1000 );

        // test that the properties were set correctly
        boolean b = jEdit.getBooleanProperty("view.selectionFg", false);
        assertTrue("selectionFg is not checked", b);
        
        try
		{
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
			        // test the selection foreground color on different ranges
			        String [] lines = new String[] {
			        		"#include <stdio.h>",
			        		"int main(int argc, char *argv[])",
			        		"{",
			        		"\tint i, a, b;",
			        		"\tfor (i=0; i<10; i++)",
			        		"\t{",
			        		"\t\tprintf(\"%d: aaaaaaaabbbbbbbbbbb ccccccccdddddddddd eeeeeeeeeeefffffffffffffff ggggggggggggggggghhhhhhhhhhhhhhh\", i);",
			        		"\t\ta = 5; b = 6; a++; b++; a += b; b += a; a *= b; b *= a; a = 5; b = 6; a++; b++; a += b; b += a; a *= b; b *= a;",
			        		"\t}",
			        		"\tprintf; for; next; continue; while; do; break; some_long_identifier, [even_longer_identifier]",
			        		"}"
			        };
			        View view = TestUtils.view();
			        JEditTextArea ta = view.getTextArea();
			        JEditBuffer buf = ta.getBuffer();
			        for (String line: lines)
			        	buf.insert(buf.getLength(), line + "\n");
			        buf.setMode("c");
			        ta.repaint();
			        
			        // select some text segments
			        String [] subStrings = new String[] {
			        	"main", "int", "intf", "<1", "+)", "\"%", "aabb", "eeff", "long_id",
			        	"ier]"
			        };
			        for (String sub: subStrings)
			        {
			        	for (int i = 0; i < buf.getLineCount(); i++)
			        	{
			        		String line = buf.getLineText(i);
			        		int index = line.indexOf(sub);
			        		if (index == -1)
			        			continue;
			        		index += buf.getLineStartOffset(i);
			        		ta.addToSelection(new Selection.Range(index, index + sub.length()));
			        	}
			        }
				}
			});
		}
        catch (Exception e)
		{
			e.printStackTrace();
		}
        Pause.pause(20000);
    }

}
