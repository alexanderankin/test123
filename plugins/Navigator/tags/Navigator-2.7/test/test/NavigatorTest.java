package test;

import java.io.File;

import java.awt.Point;
import static java.awt.event.InputEvent.*;
import static java.awt.event.KeyEvent.*;
import javax.swing.*;
import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.edt.*;
import org.fest.swing.timing.Pause;

import org.gjt.sp.jedit.testframework.Log;
import org.gjt.sp.jedit.testframework.TestUtils;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.options.PluginOptions;
import org.gjt.sp.jedit.textarea.TextArea;

import ise.plugin.nav.*;

public class NavigatorTest {
    @BeforeClass
    public static void setUpjEdit() {
        TestUtils.beforeClass();
    }

    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }

    @Before
    public void beforeTest() {
        jEdit.getPlugin( NavigatorPlugin.class.getName() ).getPluginJAR().activatePluginIfNecessary();
    }

    @After
    public void afterTest() {
        jEdit.getPlugin( NavigatorPlugin.class.getName() ).getPluginJAR().deactivatePlugin( false );
    }


    @Test
    public void testOptionPane() {
        Log.log( "testOptionPane" );

        // set the jEdit properties for the option pane to known values
        jEdit.setBooleanProperty( "navigator.groupByFile", false );
        jEdit.setBooleanProperty( "navigator.showOnToolbar", false );
        jEdit.setIntegerProperty( "navigator.maxStackSize", 512 );
        jEdit.setIntegerProperty( "navigator.scope", NavigatorPlugin.EDITPANE_SCOPE );

        // open the plugin options and select the options pane
        TestUtils.jEditFrame().menuItemWithPath( "Plugins", "Plugin Options..." ).click();
        DialogFixture optionsDialog = WindowFinder.findDialog( PluginOptions.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        TestUtils.selectPath( optionsDialog.tree(), new String[] {"Plugins", "Navigator"} );
        JPanelFixture pane = optionsDialog.panel( "navigator" );
        assertTrue( "Navigator option pane not found", pane != null );

        // test that the groupByFile checkbox is unchecked and then check the checkbox
        JCheckBoxFixture groupByFile = pane.checkBox( "groupByFile" );
        assertTrue( "Cannot find groupByFile checkbox in Navigator option pane", groupByFile != null );
        groupByFile.requireNotSelected();
        groupByFile.click();

        // test that the showOnToolbar checkbox is unchecked and then check the checkbox
        JCheckBoxFixture showOnToolbar = pane.checkBox( "showOnToolbar" );
        assertTrue( "Cannot find showOnToolbar checkbox in Navigator option pane", showOnToolbar != null );
        showOnToolbar.requireNotSelected();
        showOnToolbar.click();

        // test that the view scope radio button is not selected and that the editpane
        // radio button is selected
        JRadioButtonFixture viewScope = pane.radioButton( "viewScope" );
        JRadioButtonFixture editPaneScope = pane.radioButton( "editPaneScope" );
        assertTrue( "Cannot find viewScope radio button in Navigator option pane", viewScope != null );
        assertTrue( "Cannot find editPaneScope radio button in Navigator option pane", editPaneScope != null );
        viewScope.requireNotSelected();
        editPaneScope.requireSelected();

        // check that the maxStackSize has the right value
        JTextComponentFixture maxStackSize = pane.textBox( "maxStackSize" );
        assertTrue( "Cannot find maxStackSize text field in Navigator option pane", maxStackSize != null );
        maxStackSize.requireText( "512" );

        // change stack size value
        maxStackSize.deleteText();
        maxStackSize.enterText( "256" );
        maxStackSize.requireText( "256" );

        // click the OK button on the plugin options dialog
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
        assertTrue( "navigator.groupByFile is not true and it should be",
                jEdit.getBooleanProperty( "navigator.groupByFile", false ) );
        assertTrue( "navigator.showOnToolbar is not true and it should be",
                jEdit.getBooleanProperty( "navigator.showOnToolbar", false ) );
        assertTrue( "navigator.maxStackSize should be 256, not 512",
                256 == jEdit.getIntegerProperty( "navigator.maxStackSize", 512 ) );
        assertTrue( "navigator.scope is not " + NavigatorPlugin.EDITPANE_SCOPE,
                NavigatorPlugin.EDITPANE_SCOPE == jEdit.getIntegerProperty( "navigator.scope", NavigatorPlugin.VIEW_SCOPE ) );
    }


    @Test
    public void testNumberTextField() {
        // for Navigator, min value is 1, max value is Integer.MAX_VALUE
        // open the plugin options and select the options pane
        TestUtils.jEditFrame().menuItemWithPath( "Plugins", "Plugin Options..." ).click();
        DialogFixture optionsDialog = WindowFinder.findDialog( PluginOptions.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        TestUtils.selectPath( optionsDialog.tree(), new String[] {"Plugins", "Navigator"} );
        JPanelFixture pane = optionsDialog.panel( "navigator" );
        assertTrue( "Navigator option pane not found", pane != null );

        JTextComponentFixture maxStackSize = pane.textBox( "maxStackSize" );
        assertTrue( "Cannot find maxStackSize text field in Navigator option pane", maxStackSize != null );
        NumberTextField ntf = maxStackSize.targetCastedTo( NumberTextField.class );
        int current_value = ntf.getValue();

        // test entring numbers
        maxStackSize.deleteText();
        maxStackSize.enterText( "256" );
        maxStackSize.requireText( "256" );

        // test entering alpha characters
        maxStackSize.enterText( "abc" );
        maxStackSize.requireText( "256" );

        // test min and max values
        assertTrue( "Minimum value is not 1", ntf.getMinValue() == 1 );
        assertTrue( "Maximum value is not Integer.MAX_VALUE", ntf.getMaxValue() == Integer.MAX_VALUE );

        // test empty field returns min value
        maxStackSize.deleteText();
        maxStackSize.enterText( "0" );
        maxStackSize.requireText( "" );
        assertTrue( "Value is not 1", ntf.getValue() == ntf.getMinValue() );

        // test can't enter negative numbers
        maxStackSize.deleteText();
        maxStackSize.enterText( "-10" );
        maxStackSize.requireText( "10" );
        assertTrue( "Value is not 10", ntf.getValue() == 10 );

        // click the OK button on the plugin options dialog
        maxStackSize.deleteText();
        maxStackSize.enterText( String.valueOf( current_value ) );
        optionsDialog.button(
            new GenericTypeMatcher<JButton>( JButton.class ) {
                public boolean isMatching( JButton button ) {
                    return "OK".equals( button.getText() );
                }
            }
        ).click();
    }

    @Test
    public void testMovement() {
        TestUtils.jEditFrame().menuItemWithPath( "File", "Close All" ).click();
        Buffer buffer = createNumberBuffer();
        View view = TestUtils.view();
        assertTrue( "buffer is not showing", view.getBuffer().equals( buffer ) );

        // clear navigator history
        TestUtils.jEditFrame().menuItemWithPath( "Plugins" ).click();
        TestUtils.robot().pressAndReleaseKeys( VK_DOWN, VK_DOWN, VK_DOWN, VK_DOWN, VK_RIGHT, VK_DOWN, VK_DOWN, VK_DOWN, VK_DOWN, VK_DOWN, VK_ENTER );
        
        Stack<Integer> lines = new Stack<Integer>();
        TextArea textArea = view.getEditPane().getTextArea();
        for ( int i = 1; i < 6; i++ ) {
            Point p = new Point( 50, 14 * i );
            int offset = textArea.xyToOffset( 50, 14 * i );
            int line = textArea.getLineOfOffset( offset ) + 1;
            lines.push( line );
            TestUtils.robot().click( textArea, p );
        }
     
        lines.pop();  // remove current from list
        for ( int i = 1; i < 5; i++ ) {
            TestUtils.jEditFrame().menuItemWithPath( "Plugins" ).click();
            TestUtils.jEditFrame().menuItemWithPath( "Plugins", "Navigator", "Back" ).click();
            int caretLine = textArea.getCaretLine();
            int backLine = lines.pop();
            assertTrue( "wrong line, should be " + backLine + ", but is " + caretLine, caretLine == backLine );
        }
    }

    @Test
    public void test2811070() {
        // a test for tracker 2811070:
        // The latest trunk version of Navigator does not use the history list correctly.
        // E.g. if I start jEdit, buffer A is active, and I switch to buffer B - I expect
        // to be back to go back to A with Navigator, but there's no "back".
        // If I then switch to buffer C - I expect one "back" to go to B, and a second
        // "back" to go to A, but Navigator only allows one "back" which jumps directly to A.
        // If I later click "forward", it jumps to B. But there is no second "forward" to jump to C.

        // prep for the test...
        TestUtils.jEditFrame().menuItemWithPath( "File", "Close All" ).click();
        View view = TestUtils.view();
        
        // make 3 buffers:
        Buffer bufferA = createRandomBuffer();
        String pathA = bufferA.getPath();
        view.getEditPane().getTextArea().setCaretPosition(100);
        
        Buffer bufferB = createRandomBuffer();
        String pathB = bufferB.getPath();
        view.getEditPane().getTextArea().setCaretPosition(100);
        
        Buffer bufferC = createRandomBuffer();
        String pathC = bufferC.getPath();
        view.getEditPane().getTextArea().setCaretPosition(100);

        // close the buffers        
        jEdit._closeBuffer(view, bufferA);
        Pause.pause(500);
        jEdit._closeBuffer(view, bufferB);
        Pause.pause(500);
        jEdit._closeBuffer(view, bufferC);
        Pause.pause(500);
        
        // clear navigator history
        // This is puzzling, the menu items for individual plugins can't be
        // found by FEST using the menuItemWithPath method. Finding the 'Clear' 
        // menu item the first time by using the arrow keys works, then 
        // subsequent calls to the menu work with menuItemWithPath.
        TestUtils.jEditFrame().menuItemWithPath( "Plugins" ).click();
        TestUtils.robot().pressAndReleaseKeys( VK_DOWN, VK_DOWN, VK_DOWN, VK_DOWN, VK_RIGHT, VK_DOWN, VK_DOWN, VK_DOWN, VK_DOWN, VK_DOWN, VK_ENTER );
        
        // re-open the buffers, now ready for testing
        jEdit.openFile(view, pathA);
        Pause.pause(500);
        jEdit.openFile(view, pathB);
        Pause.pause(500);
        jEdit.openFile(view, pathC);
        Pause.pause(500);
       
        // test back and forward
        assertTrue( "bufferC is not showing", view.getBuffer().getPath().equals( pathC ) );
        TestUtils.jEditFrame().menuItemWithPath( "Plugins", "Navigator", "Back" ).click();
        Pause.pause( 500 );
        assertTrue( "bufferB is not showing", view.getBuffer().getPath().equals( pathB ) );
        TestUtils.jEditFrame().menuItemWithPath( "Plugins", "Navigator", "Back" ).click();
        Pause.pause( 500 );
        assertTrue( "bufferA is not showing", view.getBuffer().getPath().equals( pathA ) );
        TestUtils.jEditFrame().menuItemWithPath( "Plugins", "Navigator", "Forward" ).click();
        Pause.pause( 500 );
        assertTrue( "bufferB is not showing", view.getBuffer().getPath().equals( pathB ) );
        TestUtils.jEditFrame().menuItemWithPath( "Plugins", "Navigator", "Forward" ).click();
        Pause.pause( 500 );
        assertTrue( "bufferC is not showing", view.getBuffer().getPath().equals( pathC ) );
    }

    // create a buffer with a number per line
    private Buffer createNumberBuffer() {
        String contents = "0\n1\n2\n3\n4\n5\n6\n7\n8\n9\n0\n";
        return createBuffer( contents );
    }

    // create and save a buffer with some random text in it.
    private Buffer createRandomBuffer() {
        String sourceChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ            \n";
        StringBuilder sb = new StringBuilder();
        long range = ( long ) sourceChars.length();
        Random random = new Random();
        for ( int i = 0; i < 800; i++ ) {
            long fraction = ( long ) ( range * random.nextDouble() );
            int randomNumber = ( int ) ( fraction );
            sb.append( sourceChars.charAt( randomNumber ) );
        }
        return createBuffer( sb.toString() );
    }

    private Buffer createBuffer( final String contents ) {
        final Buffer buffer;
        File file;
        try {
            file = File.createTempFile( "navigator", ".test" );
            buffer = TestUtils.newFile();
            SwingUtilities.invokeAndWait(
                new Runnable() {
                    public void run() {
                        buffer.insert( 0, contents );
                    }
                }
            );
            buffer.save( TestUtils.view(), file.getAbsolutePath() );
            Pause.pause( 1000 );
            return buffer;
        }
        catch ( Exception e ) {
            fail( "Unable to insert text into buffer" );
            return null;
        }
    }
}