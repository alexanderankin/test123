package test.gui;

import static java.awt.event.InputEvent.*;
import static java.awt.event.KeyEvent.*;
import javax.swing.*;

import org.junit.*;
import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.edt.*;
import org.fest.swing.timing.Pause;

import org.gjt.sp.jedit.testframework.TestUtils;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.options.GlobalOptions;

public class StatusBarTest {
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
        jEdit.setBooleanProperty( "view.status.show-caret-linenumber", false );
        jEdit.setBooleanProperty( "view.status.show-caret-dot", false );
        jEdit.setBooleanProperty( "view.status.show-caret-virtual", false );
        jEdit.setBooleanProperty( "view.status.show-caret-offset", false );
        jEdit.setBooleanProperty( "view.status.show-caret-bufferlength", false );

        // open the options and select the options pane
        TestUtils.jEditFrame().menuItemWithPath( "Utilities", "Global Options..." ).click();
        DialogFixture optionsDialog = WindowFinder.findDialog( GlobalOptions.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        TestUtils.selectPath( optionsDialog.tree(), new String[] {"jEdit", "Status Bar"} );
        JPanelFixture pane = optionsDialog.panel( "status" );
        assertTrue( "StatusBarOptionPane not found", pane != null );

        // test that all checkboxes are present and click them
        JCheckBoxFixture checkbox = pane.checkBox( "showCaretLineNumber" );
        assertTrue( "Cannot find showCaretLineNumber checkbox in StatusBarOptionPane", checkbox != null );
        checkbox.requireNotSelected();
        checkbox.click();

        checkbox = pane.checkBox( "showCaretDot" );
        assertTrue( "Cannot find showCaretDot checkbox in StatusBarOptionPane", checkbox != null );
        checkbox.requireNotSelected();
        checkbox.click();

        checkbox = pane.checkBox( "showCaretVirtual" );
        assertTrue( "Cannot find showCaretVirtual checkbox in StatusBarOptionPane", checkbox != null );
        checkbox.requireNotSelected();
        checkbox.click();

        checkbox = pane.checkBox( "showCaretOffset" );
        assertTrue( "Cannot find showCaretOffset checkbox in StatusBarOptionPane", checkbox != null );
        checkbox.requireNotSelected();
        checkbox.click();

        checkbox = pane.checkBox( "showCaretBufferLength" );
        assertTrue( "Cannot find showCaretBufferLength checkbox in StatusBarOptionPane", checkbox != null );
        checkbox.requireNotSelected();
        checkbox.click();

        // click the OK button on the options dialog
        optionsDialog.button(
            new GenericTypeMatcher<JButton>( JButton.class ) {
                public boolean isMatching( JButton button ) {
                    return "OK".equals( button.getText() );
                }
            }
        ).click();

        // wait a second to make sure jEdit has time to save the properties
        Pause.pause( 1000 );

        // test that the properties were set correctly
        boolean b = jEdit.getBooleanProperty( "view.status.show-caret-linenumber", false );
        assertTrue( "showCaretLineNumber is not checked", b );
        b = jEdit.getBooleanProperty( "view.status.show-caret-dot", false );
        assertTrue( "showCaretDot is not checked", b );
        b = jEdit.getBooleanProperty( "view.status.show-caret-virtual", false );
        assertTrue( "showCaretVirtual is not checked", b );
        b = jEdit.getBooleanProperty( "view.status.show-caret-offset", false );
        assertTrue( "showCaretOffset is not checked", b );
        b = jEdit.getBooleanProperty( "view.status.show-caret-bufferlength", false );
        assertTrue( "showCaretBufferLength is not checked", b );
    }

    @Test
    public void testStatusBar() {
        // turn off each item in the caret status one by one.  I don't think
        // there is a need to test all the various combinations of on and off.

        // start with everything on
        jEdit.setBooleanProperty( "view.status.show-caret-linenumber", true );
        jEdit.setBooleanProperty( "view.status.show-caret-dot", true );
        jEdit.setBooleanProperty( "view.status.show-caret-virtual", true );
        jEdit.setBooleanProperty( "view.status.show-caret-offset", true );
        jEdit.setBooleanProperty( "view.status.show-caret-bufferlength", true );

        // general case, everything should be showing
        JPanelFixture statusBar = TestUtils.jEditFrame().panel( "StatusBar" );
        assertTrue( "Can't find StatusBar in view", statusBar != null );
        JLabelFixture caretStatus = statusBar.label( "caretStatus" );
        assertTrue( "Can't find caretStatus in StatusBar", caretStatus != null );
        caretStatus.requireText( "1,1 (0/0)" );

        // no line number
        TestUtils.jEditFrame().menuItemWithPath( "Utilities", "Global Options..." ).click();
        DialogFixture optionsDialog = WindowFinder.findDialog( GlobalOptions.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        TestUtils.selectPath( optionsDialog.tree(), new String[] {"jEdit", "Status Bar"} );
        JPanelFixture pane = optionsDialog.panel( "status" );
        assertTrue( "StatusBarOptionPane not found", pane != null );
        JCheckBoxFixture checkbox = pane.checkBox( "showCaretLineNumber" );
        assertTrue( "Cannot find showCaretLineNumber checkbox in StatusBarOptionPane", checkbox != null );
        checkbox.requireSelected();
        checkbox.click();
        optionsDialog.button(
            new GenericTypeMatcher<JButton>( JButton.class ) {
                public boolean isMatching( JButton button ) {
                    return "OK".equals( button.getText() );
                }
            }
        ).click();
        Pause.pause( 500 );
        caretStatus.requireText( "1 (0/0)" );

        // no dot
        TestUtils.jEditFrame().menuItemWithPath( "Utilities", "Global Options..." ).click();
        optionsDialog = WindowFinder.findDialog( GlobalOptions.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        TestUtils.selectPath( optionsDialog.tree(), new String[] {"jEdit", "Status Bar"} );
        pane = optionsDialog.panel( "status" );
        assertTrue( "StatusBarOptionPane not found", pane != null );
        checkbox = pane.checkBox( "showCaretDot" );
        assertTrue( "Cannot find showCaretDot checkbox in StatusBarOptionPane", checkbox != null );
        checkbox.requireSelected();
        checkbox.click();
        optionsDialog.button(
            new GenericTypeMatcher<JButton>( JButton.class ) {
                public boolean isMatching( JButton button ) {
                    return "OK".equals( button.getText() );
                }
            }
        ).click();
        Pause.pause( 500 );
        caretStatus.requireText( "(0/0)" );

        // no offset
        TestUtils.jEditFrame().menuItemWithPath( "Utilities", "Global Options..." ).click();
        optionsDialog = WindowFinder.findDialog( GlobalOptions.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        TestUtils.selectPath( optionsDialog.tree(), new String[] {"jEdit", "Status Bar"} );
        pane = optionsDialog.panel( "status" );
        assertTrue( "StatusBarOptionPane not found", pane != null );
        checkbox = pane.checkBox( "showCaretOffset" );
        assertTrue( "Cannot find showCaretOffset checkbox in StatusBarOptionPane", checkbox != null );
        checkbox.requireSelected();
        checkbox.click();
        optionsDialog.button(
            new GenericTypeMatcher<JButton>( JButton.class ) {
                public boolean isMatching( JButton button ) {
                    return "OK".equals( button.getText() );
                }
            }
        ).click();
        Pause.pause( 500 );
        caretStatus.requireText( "(0)" );

        // no buffer length
        TestUtils.jEditFrame().menuItemWithPath( "Utilities", "Global Options..." ).click();
        optionsDialog = WindowFinder.findDialog( GlobalOptions.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        TestUtils.selectPath( optionsDialog.tree(), new String[] {"jEdit", "Status Bar"} );
        pane = optionsDialog.panel( "status" );
        assertTrue( "StatusBarOptionPane not found", pane != null );
        checkbox = pane.checkBox( "showCaretBufferLength" );
        assertTrue( "Cannot find showCaretBufferLength checkbox in StatusBarOptionPane", checkbox != null );
        checkbox.requireSelected();
        checkbox.click();
        optionsDialog.button(
            new GenericTypeMatcher<JButton>( JButton.class ) {
                public boolean isMatching( JButton button ) {
                    return "OK".equals( button.getText() );
                }
            }
        ).click();
        Pause.pause( 500 );
        caretStatus.requireText( "" );
    }

    @Test
    public void testStatusBarTheWayILikeIt() {
        jEdit.setBooleanProperty( "view.status.show-caret-linenumber", true );
        jEdit.setBooleanProperty( "view.status.show-caret-dot", true );
        jEdit.setBooleanProperty( "view.status.show-caret-virtual", false );
        jEdit.setBooleanProperty( "view.status.show-caret-offset", false );
        jEdit.setBooleanProperty( "view.status.show-caret-bufferlength", false );

        // open the options and select the options pane
        TestUtils.jEditFrame().menuItemWithPath( "Utilities", "Global Options..." ).click();
        DialogFixture optionsDialog = WindowFinder.findDialog( GlobalOptions.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        TestUtils.selectPath( optionsDialog.tree(), new String[] {"jEdit", "Status Bar"} );
        JPanelFixture pane = optionsDialog.panel( "status" );
        assertTrue( "StatusBarOptionPane not found", pane != null );

        // click the OK button on the options dialog
        optionsDialog.button(
            new GenericTypeMatcher<JButton>( JButton.class ) {
                public boolean isMatching( JButton button ) {
                    return "OK".equals( button.getText() );
                }
            }
        ).click();

        JPanelFixture statusBar = TestUtils.jEditFrame().panel( "StatusBar" );
        assertTrue( "Can't find StatusBar in view", statusBar != null );
        JLabelFixture caretStatus = statusBar.label( "caretStatus" );
        assertTrue( "Can't find caretStatus in StatusBar", caretStatus != null );
        caretStatus.requireText( "1,1 " );
    }

    @Test
    public void testValueDisplay() {
        // make sure all parts are displayed
        testOptionPane();

        final Buffer buffer = TestUtils.newFile();
        final String text = "0\n1\n2\n3\n\t4\n5\n6\n7\n8\n9\n";
        try {
            SwingUtilities.invokeAndWait(
                new Runnable() {
                    public void run() {
                        buffer.insert( 0, text );
                        // make sure buffer properties are set correctly for this test
                        buffer.setStringProperty( "lineSeparator", "\n" );
                        buffer.setIntegerProperty( "tabSize", 8 );                        
                        buffer.setBooleanProperty( "noTabs", false );
                    }
                }
            );
        }
        catch ( Exception e ) {
            fail( "Unable to insert text into buffer" );
        }

        JPanelFixture statusBar = TestUtils.jEditFrame().panel( "StatusBar" );
        assertTrue( "Can't find StatusBar in view", statusBar != null );
        JLabelFixture caretStatus = statusBar.label( "caretStatus" );
        assertTrue( "Can't find caretStatus in StatusBar", caretStatus != null );

        // after text insert, caret should be at bottom, move it to the top
        TestUtils.view().getTextArea().setCaretPosition( 0 );
        Pause.pause(500);
        caretStatus.requireText( "1,1 (0/21)" );

        TestUtils.robot().pressAndReleaseKeys( VK_DOWN );
        caretStatus.requireText( "2,1 (2/21)" );

        TestUtils.robot().pressAndReleaseKeys( VK_DOWN, VK_DOWN, VK_DOWN, VK_END );
        caretStatus.requireText( "5,3-10 (10/21)" );
    }
}