/*
 * BuffersOutputDirectoryTest.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.translate;

// {{{ jUnit imports 
import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.data.TableCell;
import org.fest.swing.finder.*;
import org.fest.swing.edt.*;
import org.fest.swing.timing.*;
import org.fest.swing.core.matcher.JButtonMatcher;

import static org.fest.assertions.Assertions.*;

import org.gjt.sp.jedit.testframework.Log;

import static org.gjt.sp.jedit.testframework.TestUtils.*;
import static org.gjt.sp.jedit.testframework.EBFixture.*;
import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.testframework.TestUtils;

// }}}

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.Buffer;

import java.io.*;

import com.thaiopensource.relaxng.output.OutputDirectory;


/**
 * unit tests for BuffersOutputDirectory
 * $Id$
 */
public class BuffersOutputDirectoryTest{
	private static File testData;
	
    @BeforeClass
    public static void setUpjEdit() throws IOException{
        TestUtils.beforeClass();
        testData = new File(System.getProperty("test_data")).getCanonicalFile();
        assertTrue(testData.exists());
    }
    
    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }
    
    @Test
    public void testReference() throws IOException{
    	final BuffersOutputDirectory bod = new BuffersOutputDirectory(view(),"input.xml","output.xsd");
    	
    	assertEquals("toto.xsd",bod.reference(null,"toto.xml"));
    	assertEquals("output.xsd",bod.reference(null,"input.xml"));
    	//will come up with a new name
    	assertEquals("toto1.xsd",bod.reference(null,"a/toto.xml"));
    	
    	assertEquals("ahah.xsd",bod.reference(null,"somedir/ahah.xml"));
    }
    
    @Test
    public void testOpen() throws IOException{
    	final File in = new File(testData,"input.xml");
    	final File out = new File(testData,"input.xsd");
    	final BuffersOutputDirectory bod = new BuffersOutputDirectory(view(),in.getPath(),out.getPath());
		
    	assertEquals(0,bod.getOutputCount());
    	
    	OutputDirectory.Stream output = bod.open(in.getPath(),null);
    	output.getWriter().write("HELLO, world");
    	output.getWriter().flush();
    	output.getWriter().close();
    	
    	Buffer b = jEdit.getBuffer(out.getPath());
    	assertNotNull(b);
    	assertEquals("HELLO, world",b.getText(0,b.getLength()));
    	assertEquals(1,bod.getOutputCount());
    	
    	close(view(),b);
    }
    
    @Test
    public void testModeProperties(){
    	Mode m = jEdit.getMode("xml");
    	m.loadIfNecessary();
    	
    	Object oldIndent = m.getProperty("indentSize");
    	Object oldLineLength = m.getProperty("maxLineLen");
    	
    	m.setProperty("indentSize",Integer.valueOf(3));
    	m.setProperty("maxLineLen",Integer.valueOf(33));
    	
    	final BuffersOutputDirectory bod = new BuffersOutputDirectory(view(),"input.dtd","output.xsd");
    	
    	// they are no-op
    	bod.setIndent(4);
    	bod.setEncoding("AAA");
    	
    	// real test
    	assertEquals(3,bod.getIndent());
    	assertEquals(33,bod.getLineLength());
    	assertEquals("\n",bod.getLineSeparator());
    	
    	
    	// clean-up
    	m.setProperty("indentSize",oldIndent);
    	m.setProperty("maxLineLen",oldLineLength);
    }

}
