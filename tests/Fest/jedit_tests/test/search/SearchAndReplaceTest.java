
package test.search;

import java.awt.Component;
import static java.awt.event.InputEvent.*;
import static java.awt.event.KeyEvent.*; 
import javax.swing.*;

import org.junit.*;
import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.edt.*;

import org.gjt.sp.jedit.testframework.Log;
import org.gjt.sp.jedit.testframework.TestUtils;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.search.SearchDialog;

public class SearchAndReplaceTest {
    @BeforeClass
    public static void setUpjEdit() {
        TestUtils.beforeClass();
    }

    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }


    @Test
    public void testFindAndReplace() {
        Log.log( "testFindAndReplace" );
        final Buffer buffer = TestUtils.newFile();
        final String text =
            "* This program is free software; you can redistribute it and/or\n"
            + "* modify it under the terms of the GNU General Public License\n"
            + "* as published by the Free Software Foundation; either version 2\n"
            + "\n"
            + "* of the License, or any later version.";

        try {
            SwingUtilities.invokeAndWait(
                new Runnable() {
                    public void run() {
                        buffer.insert( 0, text );
                    }
                }
            );
        }
        catch ( Exception e ) {}

        TestUtils.jEditFrame().menuItemWithPath("Search","Find...").click();

        /*
        Log.log("next press CRTL+F to pop search dialog");
        
        TestUtils.robot().pressKey(VK_CONTROL);
        TestUtils.robot().pressKey(VK_F);
        TestUtils.robot().releaseKey(VK_F);
        TestUtils.robot().releaseKey(VK_CONTROL);
        */
        Log.log("next find search dialog");
        DialogFixture findDialog = WindowFinder.findDialog( SearchDialog.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        Log.log("next enter text in to find box");
        findDialog.textBox( "find" ).enterText( "program" );
        Log.log("next enter text in replace box");
        findDialog.textBox( "replace" ).enterText( "application" );
        Log.log("next click button");
        findDialog.button(
            new GenericTypeMatcher(JButton.class) {
                public boolean isMatching( Component b ) {
                    return "Replace All".equals( ((JButton)b).getText() );
                }
            }
        ).click();
        
        Log.log("next check text has been replaced");
        try {
            SwingUtilities.invokeLater(
                new Runnable(){
                    public void run() {
                        buffer.readLock();
                        String bufferText = buffer.getText(0, buffer.getLength());
                        buffer.readUnlock();
                        String[] lines = bufferText.split( "\n" );
                        assertEquals( "* This application is free software; you can redistribute it and/or", lines[ 0 ] );
                        
                    }
                });
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        TestUtils.close( TestUtils.view(), buffer );
    }
    
    @Test
    public void testFindAndReplaceWithShortcut() {
        Log.log( "testFindAndReplace" );
        final Buffer buffer = TestUtils.newFile();
        final String text =
            "* This program is free software; you can redistribute it and/or\n"
            + "* modify it under the terms of the GNU General Public License\n"
            + "* as published by the Free Software Foundation; either version 2\n"
            + "\n"
            + "* of the License, or any later version.";

        try {
            SwingUtilities.invokeAndWait(
                new Runnable() {
                    public void run() {
                        buffer.insert( 0, text );
                    }
                }
            );
        }
        catch ( Exception e ) {}

        Log.log("next press CRTL+F to pop search dialog");
        
        TestUtils.robot().pressKey(VK_CONTROL);
        TestUtils.robot().pressKey(VK_F);
        TestUtils.robot().releaseKey(VK_F);
        TestUtils.robot().releaseKey(VK_CONTROL);
        Log.log("next find search dialog");
        DialogFixture findDialog = WindowFinder.findDialog( SearchDialog.class ).withTimeout( 5000 ).using( TestUtils.robot() );
        Log.log("next enter text in to find box");
        findDialog.textBox( "find" ).enterText( "program" );
        Log.log("next enter text in replace box");
        findDialog.textBox( "replace" ).enterText( "application" );
        Log.log("next click button");
        findDialog.button(
            new GenericTypeMatcher(JButton.class) {
                public boolean isMatching( Component b ) {
                    return "Replace All".equals( ((JButton)b).getText() );
                }
            }
        ).click();
        
        
        Log.log("next check text has been replaced");
        try {
            SwingUtilities.invokeLater(
                new Runnable(){
                    public void run() {
                        buffer.readLock();
                        String bufferText = buffer.getText(0, buffer.getLength());
                        buffer.readUnlock();
                        String[] lines = bufferText.split( "\n" );
                        assertEquals( "* This application is free software; you can redistribute it and/or", lines[ 0 ] );
                        
                    }
                });
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        TestUtils.close( TestUtils.view(), buffer );
    }
}