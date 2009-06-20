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


public class BufferLocalTest {
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
        jEdit.setBooleanProperty( "bufferlocal.removeStale", false );
        jEdit.setIntegerProperty( "bufferlocal.staleTime", 30 );

        // open the plugin options and select the options pane
        TestUtils.jEditFrame().menuItemWithPath( "Plugins", "Plugin Options..." ).click();
        DialogFixture optionsDialog = WindowFinder.findDialog( PluginOptions.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        TestUtils.selectPath( optionsDialog.tree(), new String[] {"Plugins", "BufferLocal"} );
        JPanelFixture pane = optionsDialog.panel( "bufferlocal" );
        assertTrue( "BufferLocal option pane not found", pane != null );

        // test that the checkbox is unchecked and then check the checkbox
        JCheckBoxFixture checkbox = pane.checkBox();
        assertTrue( "Cannot find checkbox in BufferLocal option pane", checkbox != null );
        checkbox.requireNotSelected();
        checkbox.click();

        // check that the spinner has the right value, then spin it to 45
        JSpinnerFixture spinner = pane.spinner();
        assertTrue( "Cannot find spinner in BufferLocal option pane", spinner != null );
        spinner.requireValue( 30 );
        spinner.increment( 15 );

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
        boolean checked = jEdit.getBooleanProperty( "bufferlocal.removeStale", false );
        int time = jEdit.getIntegerProperty( "bufferlocal.staleTime", 30 );

        assertTrue( "Close files checkbox is not checked and it should be", checked );
        assertTrue( "Minutes spinner value is not 45 and it should be 45", time == 45 );
    }

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
}