/*
 * RelaxNGCompletionTest.java
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
package xml.parser;

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
import org.gjt.sp.jedit.testframework.TestUtils;

import static xml.XMLTestUtils.*;
// }}}

import java.io.*;
import java.net.*;
import java.util.*;

import xml.completion.*;
import xml.completion.ElementDecl.AttributeDecl;

/**
 * $Id$
 */
public class SchemaToCompletionTest{
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
    public void testParentRef(){
    	File f = new File(testData,"parentRef/actual_table.rng");
		
    	Map<String,CompletionInfo> rng = SchemaToCompletion.rngSchemaToCompletionInfo(null,f.getPath(),null,null);
		assertEquals(1,rng.size());
		assertTrue(rng.containsKey(""));
		CompletionInfo info = rng.get("");
		assertEquals(1,info.elementHash.size());
		assertThat(info.elementHash.keySet()).containsOnly("doc");
		ElementDecl doc = info.elementHash.get("doc");
		assertThat(doc.content).contains("table","p");
		ElementDecl em = doc.elementHash.get("p").elementHash.get("em");
		// true loop in the model mirroring the loop in the schema
		assertSame(em, em.elementHash.get("em"));
		ElementDecl td = doc.elementHash.get("table").elementHash.get("tr").elementHash.get("td");
		assertThat(td.content).containsOnly("em");
    }
    
    @Test
    public void testRelaxNG(){
    	File f = new File(testData,"../xml/dtds/relaxng.rng");
		
    	Map<String,CompletionInfo> rng = SchemaToCompletion.rngSchemaToCompletionInfo(null,f.getPath(),null,null);
		assertEquals(1,rng.size());
		assertTrue(rng.containsKey("http://relaxng.org/ns/structure/1.0"));
		CompletionInfo info = rng.get("http://relaxng.org/ns/structure/1.0");
		assertThat(info.elementHash.keySet()).contains("element");
		ElementDecl element = info.elementHash.get("element");
		assertThat(element.content).contains("choice");
		assertTrue(element.attributeHash.containsKey("name"));
		assertTrue(element.attributeHash.containsKey("ns"));
		AttributeDecl a = element.attributeHash.get("name");
		assertEquals("QName",a.type);
    }

    @Test
    public void testLocate(){
    	File f = new File(testData,"../xml/dtds/locate.rng");
		
    	Map<String,CompletionInfo> rng = SchemaToCompletion.rngSchemaToCompletionInfo(null,f.getPath(),null,null);
		assertEquals(1,rng.size());
		assertTrue(rng.containsKey("http://thaiopensource.com/ns/locating-rules/1.0"));
		CompletionInfo info = rng.get("http://thaiopensource.com/ns/locating-rules/1.0");
		assertThat(info.elementHash.keySet()).containsOnly("locatingRules");
		ElementDecl locatingRules = info.elementHash.get("locatingRules");
		assertTrue(locatingRules.attributeHash.containsKey("base"));
		assertThat(locatingRules.content).contains("include");
		ElementDecl include = locatingRules.elementHash.get("include");
		assertTrue(include.attributeHash.containsKey("base"));
		AttributeDecl base = include.attributeHash.get("base");
		assertEquals("http://www.w3.org/XML/1998/namespace",base.namespace);
		assertEquals("anyURI",base.type);
		assertFalse(base.required);
		AttributeDecl rules = include.attributeHash.get("rules");
		assertEquals("",rules.namespace);
		assertEquals("anyURI",rules.type);
		assertTrue(rules.required);
	}
	@Test
	public void testOptionalRef(){
    	File f = new File(testData,"optionalRef/schema.rng");
		
    	Map<String,CompletionInfo> rng = SchemaToCompletion.rngSchemaToCompletionInfo(null,f.getPath(),null,null);
		assertEquals(1,rng.size());
		assertTrue(rng.containsKey(""));
		CompletionInfo info = rng.get("");
		assertThat(info.elementHash.keySet()).containsOnly("doc");
		ElementDecl doc = info.elementHash.get("doc");
		assertTrue(doc.attributeHash.containsKey("inline"));
		assertFalse(doc.getAttribute("inline").required);
		assertThat(doc.content).contains("needsinline");
		ElementDecl e = doc.elementHash.get("needsinline");
		assertTrue(e.attributeHash.containsKey("inline"));
		AttributeDecl inline = e.attributeHash.get("inline");
		assertTrue(inline.required);
    }
}
