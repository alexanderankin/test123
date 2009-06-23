package test;

import java.io.File;

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

import org.gjt.sp.jedit.testframework.Log;
import org.gjt.sp.jedit.testframework.TestUtils;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.options.PluginOptions;

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
        JCheckBoxFixture groupByFile = pane.checkBox( "groupByFile");
                assertTrue( "Cannot find groupByFile checkbox in Navigator option pane", groupByFile != null );
                groupByFile.requireNotSelected();
                groupByFile.click();

                // test that the showOnToolbar checkbox is unchecked and then check the checkbox
                JCheckBoxFixture showOnToolbar = pane.checkBox("showOnToolbar" );
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
        NumberTextField ntf = maxStackSize.targetCastedTo(NumberTextField.class);
        int current_value = ntf.getValue();
        
        // test entring numbers
        maxStackSize.deleteText();
        maxStackSize.enterText( "256" );
        maxStackSize.requireText( "256" );
        
        // test entering alpha characters
        maxStackSize.enterText("abc");
        maxStackSize.requireText("256");
        
        // test min and max values
        assertTrue("Minimum value is not 1", ntf.getMinValue() == 1);
        assertTrue("Maximum value is not Integer.MAX_VALUE", ntf.getMaxValue() == Integer.MAX_VALUE);
        
        // test empty field returns min value
        maxStackSize.deleteText();
        maxStackSize.enterText("0");
        maxStackSize.requireText("");
        assertTrue("Value is not 1", ntf.getValue() == ntf.getMinValue());
        
        // test can't enter negative numbers
        maxStackSize.deleteText();
        maxStackSize.enterText("-10");
        maxStackSize.requireText("10");
        assertTrue("Value is not 10", ntf.getValue() == 10);
        
        // click the OK button on the plugin options dialog
        maxStackSize.deleteText();
        maxStackSize.enterText(String.valueOf(current_value));
        optionsDialog.button(
            new GenericTypeMatcher<JButton>( JButton.class ) {
                public boolean isMatching( JButton button ) {
                    return "OK".equals( button.getText() );
                }
            }
        ).click();
    }
    /*
        @Test
        public void testBufferMinder() {
            // open 2 files, then wait.  The first file opened should be automatically
            // closed after 1 minute.
            jEdit.setBooleanProperty( "bufferlocal.removeStale", true );
            jEdit.setIntegerProperty( "bufferlocal.staleTime", 1 );
            Pause.pause( 1000 );
            EditBus.send( new PropertiesChanged( null ) );
     
            try {
                File fileA = File.createTempFile( "bufferlocal", ".test" );
                final Buffer bufferA = TestUtils.newFile();
                final String textA =
                    "* This program is free software; you can redistribute it and/or\n"
                    + "* modify it under the terms of the GNU General Public License\n"
                    + "* as published by the Free Software Foundation; either version 2\n"
                    + "\n"
                    + "* of the License, or any later version.";
     
                try {
                    SwingUtilities.invokeAndWait(
                        new Runnable() {
                            public void run() {
                                bufferA.insert( 0, textA );
                            }
                        }
                    );
                }
                catch ( Exception e ) {
                    fail( "Unable to insert text into buffer" );
                }
                bufferA.save( TestUtils.view(), fileA.getAbsolutePath() );
     
                File fileB = File.createTempFile( "bufferlocal", ".test" );
                final Buffer bufferB = TestUtils.newFile();
                final String textB = "Wait... this test takes 65 seconds";
                try {
                    SwingUtilities.invokeAndWait(
                        new Runnable() {
                            public void run() {
                                bufferB.insert( 0, textB );
                            }
                        }
                    );
                }
                catch ( Exception e ) {
                    fail( "Unable to insert text into buffer" );
                }
                bufferB.save( TestUtils.view(), fileB.getAbsolutePath() );
     
                // wait for BufferLocal to do its thing
                Pause.pause( 65000 );
     
                // check that the first buffer is closed
                Buffer bufferA2 = jEdit._getBuffer( fileA.getAbsolutePath() );
                assertTrue( "Buffer should be closed and it isn't", bufferA2 == null );
            }
            catch ( Exception e ) {
                e.printStackTrace();
                fail( "Unable to create temp file" );
            }
        }
     
        @Test
        public void testRestoreSettings() {
            // create a file, set some buffer properties, close the file, reopen
            // the file, then confirm the buffer properties were restored
            try {
                File fileA = File.createTempFile( "bufferlocal", ".test" );
                final Buffer bufferA = TestUtils.newFile();
                final String textA =
                    "* This program is free software; you can redistribute it and/or\n"
                    + "* modify it under the terms of the GNU General Public License\n"
                    + "* as published by the Free Software Foundation; either version 2\n"
                    + "\n"
                    + "* of the License, or any later version.";
     
                try {
                    SwingUtilities.invokeAndWait(
                        new Runnable() {
                            public void run() {
                                bufferA.insert( 0, textA );
                            }
                        }
                    );
                }
                catch ( Exception e ) {
                    fail( "Unable to insert text into buffer" );
                }
                bufferA.save( TestUtils.view(), fileA.getAbsolutePath() );
     
                // change the buffer settings
                TestUtils.jEditFrame().menuItemWithPath( "Utilities", "Buffer Options..." ).click();
                DialogFixture bufferDialog = TestUtils.findDialogByTitle( "Buffer Options" );
                assertTrue( "Can't find buffer dialog", bufferDialog != null );
                
                // set these programatically rather than with the gui since none of the comboboxes
                // in BufferOptionPane have names
                bufferA.setIntegerProperty( "maxLineLength", 40 );
                bufferA.setIntegerProperty( "tabSize", 7 );
                bufferA.setIntegerProperty( "indentSize", 11 );
                bufferA.setBooleanProperty( "noTabs", false );
     
                // close and reopen the buffer
                jEdit._closeBuffer( TestUtils.view(), bufferA );
                
                // give buffer local time to store settings for this buffer
                Pause.pause( 1000 );
                
                // reopen the buffer
                Buffer bufferA2 = jEdit.openFile( TestUtils.view(), fileA.getAbsolutePath() );
                
                // give buffer local time to restore the settings for this buffer
                Pause.pause( 1000 );
                
                // check the buffer settings
                int linelength = bufferA2.getIntegerProperty( "maxLineLength", 0 );
                int tabsize = bufferA2.getIntegerProperty( "tabSize", 0 );
                int indentsize = bufferA2.getIntegerProperty( "indentSize", 0 );
                boolean notabs = bufferA2.getBooleanProperty( "noTabs", true );
                assertTrue("linelength is not 40, it is " + linelength, linelength == 40);
                assertTrue("tabsize is not 7, it is " + tabsize, tabsize == 7);
                assertTrue("indentsize is not 11, it is " + indentsize, indentsize == 11);
                assertTrue("notabs is not false", notabs == false);
            }
            catch ( Exception e ) {
                e.printStackTrace();
                fail( "Unable to create temp file" );
            }
        }
    */
}