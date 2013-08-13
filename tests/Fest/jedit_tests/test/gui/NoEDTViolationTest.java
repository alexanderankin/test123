package test.gui;

import static java.awt.event.KeyEvent.*;
import javax.swing.*;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.timing.Pause;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;

import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.testframework.TestUtils;

import org.gjt.sp.jedit.*;

/**
 * tests that no illegal (even if harmless) access to the gui 
 * is done outside the EDT.
 * {@link http://docs.codehaus.org/display/FEST/Testing+that+access+to+Swing+components+is+done+in+the+EDT}
 */
public class NoEDTViolationTest {

	@BeforeClass
	public static void installRepaintManager() {
		FailOnThreadViolationRepaintManager.install();		
		// starts a new jEdit instance
		TestUtils.setupNewjEdit();
	}
	
	@AfterClass
	public static void shutdown(){
		// quit the jEdit instance
		TestUtils.tearDownNewjEdit();
	}
	
    @Test
    public void testThatItStarts() {
        // yeah !
   }
}