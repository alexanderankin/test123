/*
 * CacheTest.java
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
package xml.cache;

// {{{ jUnit imports 
import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.*;
import org.fest.swing.edt.*;
import org.fest.swing.timing.*;

import static org.fest.assertions.Assertions.*;

import org.gjt.sp.jedit.testframework.Log;

import static xml.XMLTestUtils.*;
import static org.gjt.sp.jedit.testframework.EBFixture.*;
import org.gjt.sp.jedit.testframework.TestUtils;
import static org.gjt.sp.jedit.testframework.TestUtils.*;

// }}}

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Registers;

import java.io.*;
import javax.xml.XMLConstants;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import org.gjt.sp.jedit.gui.CompletionPopup;

import xml.PathUtilities;

/**
 * unit tests for the Cache mechanism
 * $Id$
 */
public class CacheTest{
	private static File testData;
	private Cache cache;
	
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
    
    @Before
    public void setup(){
    	cache = Cache.instance();
		cache.clear();
    }
    
    @After
    public void tearDown(){
		cache.clear();
    }
    
	@Test
	public void testPutGetClear(){
		Object value = new Object();
		CacheEntry en = cache.put("path","key",value);
		assertEquals("path",en.getPath());
		assertEquals("key",en.getKey());
		assertEquals(value,en.getCachedItem());
		CacheEntry en2 = new CacheEntry("path","key","another value");
		assertEquals(en,en2);
		
		CacheEntry v = cache.get("path","key");
		assertEquals(value,v.getCachedItem());
		assertEquals(en,v);
		
		// no overwrite
		CacheEntry en3 = cache.put("path","key","another value");
		v = cache.get("path","key");
		assertEquals(value,v.getCachedItem());
		assertEquals(en,v);
		
		// another entry
		cache.put("someOtherPath","key", "some other value");
		assertEquals("some other value",cache.get("someOtherPath","key").getCachedItem());
		cache.put("path","someOtherkey", "yet another value");
		assertEquals("yet another value",cache.get("path","someOtherkey").getCachedItem());
		
		assertEquals(null,cache.get("not there", "key"));
		
		// clear cache
		cache.clear();
		assertNull(cache.get("path","key"));
	}
	
	@Test
	public void testMonitor(){
		File test = new File(testData,"with_prefix/test.xml");
		Buffer b = openFile(test.getPath());
		Pause.pause(2000);
		close(view(),b);
		// clears on open
		cache.put(test.getPath(),"key", "value");
		b = openFile(test.getPath());
		Pause.pause(2000);
		assertNull(cache.get(test.getPath(),"key"));
		
		// close requesting buffer
		CacheEntry en = cache.put(test.getPath(),"key","value");
		en.getRequestingBuffers().add(b);
		CacheEntry dep = cache.put("path","key","related");
		en.getRelated().add(dep);
		
		close(view(),b);
		Pause.pause(1000);
		assertNull(cache.get(test.getPath(),"key"));
		assertNull(cache.get("path","key"));

		// modify the buffer
		final Buffer b2 = openFile(test.getPath());
		Pause.pause(1000);
		en = cache.put(test.getPath(),"key","value");
		dep = cache.put("path","key","value");
		en.getRelated().add(dep);
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					b2.insert(1, "HELLO");
				}
		});
		Pause.pause(1000);
		assertNull(cache.get(test.getPath(),"key"));
		assertNull(cache.get("path","key"));
		close(view(),b2);
	}
	
	@Test
	public void testXSD(){
		File test = new File(testData,"import_schema/instance.xml");
		File importxsd = new File(testData,"import_schema/import.xsd");
		File sourcexsd = new File(testData,"import_schema/source.xsd");

		Buffer b = openFile(test.getPath());
		
		parseAndWait();
		
		assertNotNull(cache.get(importxsd.getPath(),XMLConstants.W3C_XML_SCHEMA_NS_URI));
		assertNotNull(cache.get(sourcexsd.getPath(),XMLConstants.W3C_XML_SCHEMA_NS_URI));
		assertNotNull(cache.get(importxsd.getPath(),"CompletionInfo"));
		// both URL and file work
		assertNotNull(cache.get(sourcexsd.getPath(),"CompletionInfo"));
		assertNotNull(cache.get(PathUtilities.pathToURL(sourcexsd.getPath()),"CompletionInfo"));
		
		Pause.pause(5000);
		// open source.xsd => invalidate everything
		final Buffer b2 = openFile(sourcexsd.getPath());
		Pause.pause(5000);
		assertNull(cache.get(importxsd.getPath(),XMLConstants.W3C_XML_SCHEMA_NS_URI));
		assertNull(cache.get(sourcexsd.getPath(),XMLConstants.W3C_XML_SCHEMA_NS_URI));
		assertNull(cache.get(importxsd.getPath(),"CompletionInfo"));
		assertNull(cache.get(PathUtilities.pathToURL(sourcexsd.getPath()),"CompletionInfo"));
	}
	
	@Test
	public void testRNG(){
		File test = new File(testData,"parentRef/instance.xml");
		File actual = new File(testData,"parentRef/actual_table.rng");
		File table = new File(testData,"parentRef/table.rng");

		Buffer b = openFile(test.getPath());
		
		parseAndWait();
		
		assertNotNull(cache.get(actual.getPath(),"Schema"));
		assertNotNull(cache.get(actual.getPath(),"CompletionInfo"));
		
		Pause.pause(5000);

		// open table.rng => invalidate CompletionInfo and schema component
		Buffer b2 = openFile(table.getPath());
		Pause.pause(5000);
		assertNull(cache.get(actual.getPath(),"CompletionInfo"));
		close(view(),b2);
		
		// FIXME: should work with table.rng
		// open actual_table.rng => invalidate everything
		b2 = openFile(actual.getPath());
		Pause.pause(5000);
		assertNull(cache.get(actual.getPath(),"Schema"));
		close(view(),b2);
		
		// reparse instance
		parseAndWait();

		assertNotNull(cache.get(actual.getPath(),"Schema"));
		assertNotNull(cache.get(actual.getPath(),"CompletionInfo"));
		
		Pause.pause(5000);
		close(view(),b);
		Pause.pause(5000);
		
		assertNull(cache.get(actual.getPath(),"Schema"));
		assertNull(cache.get(actual.getPath(),"CompletionInfo"));
	}

	@Test
	public void testDTD() throws IOException{
		File test = new File(testData,"dtd/actions.xml");
		String actionsdtd = xml.Resolver.instance().resolveEntityToPath(null,null,null,"actions.dtd");

		Buffer b = openFile(test.getPath());
		
		parseAndWait();
		
		assertNotNull(cache.get(actionsdtd,XMLConstants.XML_DTD_NS_URI));
		assertNotNull(cache.get(actionsdtd,"CompletionInfo"));
		
		// open actions.dtd => invalidate everything
		final Buffer b2 = openFile(actionsdtd);
		Pause.pause(5000);
		assertNull(cache.get(actionsdtd,XMLConstants.XML_DTD_NS_URI));
		assertNull(cache.get(actionsdtd,"CompletionInfo"));
	}
}
