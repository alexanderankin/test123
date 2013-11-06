/*
 * XalanXPathAdapterTest.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Eric Le Lay
 *
 * The XSLT plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 2.0, for example used by the Xalan package."
 */
package xslt;

import static org.fest.assertions.Assertions.assertThat;
import static org.gjt.sp.jedit.testframework.TestUtils.openFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.TestData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

/**
 * unit tests of the Xalan XPath engine adapter
 * $Id$
 */
@RunWith(JEditRunner.class)
public class XalanXPathAdapterTest{
	@Rule
	public TestData testData = new TestData();
    
    @Test
    public void testElement() throws Exception{
    	final File xsl = new File(testData.get(),"simple/transform.xsl");
    	Buffer b = openFile(xsl.getPath());
    	Pause.pause(1000);
    	Document source  = DocumentCache.getFromCache(new XalanXPathAdapter(), b);
    	
    	XalanXPathAdapter xpath = new XalanXPathAdapter();
    	
    	Map<String,String> prefixes = new HashMap<String,String>();
    	prefixes.put("xsl","http://www.w3.org/1999/XSL/Transform");
    	
    	XPathAdapter.Result res = xpath.evaluateExpression(source, prefixes, "/xsl:stylesheet");
    	
    	assertEquals("node-set of size 1",res.getType());
    	assertThat(res.getStringValue()).contains("hello");
    	
    	assertTrue(res.isNodeSet());
    	assertEquals(1,res.size());
    	
    	XPathAdapter.XPathNode n = res.get(0);
    	assertTrue(n.hasExpandedName());
    	assertFalse(n.hasDomValue());
    	assertEquals("element",n.getType());
    	assertEquals("xsl:stylesheet",n.getName());
    	
    	XMLFragmentsString frags = res.toXMLFragmentsString();
    	assertEquals(1,frags.getFragmentCount());
    }
    
    @Test
    public void testComment() throws Exception{
    	final File xsl = new File(testData.get(),"base_uri_bug/base-uri-bug.xsl");
    	Buffer b = openFile(xsl.getPath());
    	Pause.pause(1000);
    	Document source  = DocumentCache.getFromCache(new XalanXPathAdapter(), b);
    	
    	XalanXPathAdapter xpath = new XalanXPathAdapter();
    	
    	Map<String,String> prefixes = new HashMap<String,String>();
    	prefixes.put("xsl","http://www.w3.org/1999/XSL/Transform");
    	
    	XPathAdapter.Result res = xpath.evaluateExpression(source, prefixes, "//comment()");
    	
    	assertEquals("node-set of size 1",res.getType());
    	assertThat(res.getStringValue()).contains("this stylesheet was submitted");
    	
    	assertTrue(res.isNodeSet());
    	assertEquals(1,res.size());
    	
    	XPathAdapter.XPathNode n = res.get(0);
    	assertFalse(n.hasExpandedName());
    	assertTrue(n.hasDomValue());
    	assertThat(n.getDomValue()).contains("this stylesheet was submitted");
    	assertEquals("comment",n.getType());
    	assertEquals("",n.getName());
    	
    	XMLFragmentsString frags = res.toXMLFragmentsString();
    	assertEquals(1,frags.getFragmentCount());
    }
    
    @Test
    public void testNumber() throws Exception{
    	final File xsl = new File(testData.get(),"simple/transform.xsl");
    	Buffer b = openFile(xsl.getPath());
    	Pause.pause(1000);
    	Document source  = DocumentCache.getFromCache(new XalanXPathAdapter(), b);
    	
    	XalanXPathAdapter xpath = new XalanXPathAdapter();
    	
    	Map<String,String> prefixes = new HashMap<String,String>();
    	prefixes.put("xsl","http://www.w3.org/1999/XSL/Transform");
    	
    	XPathAdapter.Result res = xpath.evaluateExpression(source, prefixes, "1+1");
    	
    	assertEquals("number",res.getType());
    	assertEquals("2",res.getStringValue());
    	
    	assertFalse(res.isNodeSet());
    	assertEquals(0,res.size());
    	
    }
    
    @Test
    public void testEmptySequence() throws Exception{
    	final File xsl = new File(testData.get(),"simple/transform.xsl");
    	Buffer b = openFile(xsl.getPath());
    	Pause.pause(1000);
    	Document source  = DocumentCache.getFromCache(new XalanXPathAdapter(), b);
    	
    	XalanXPathAdapter xpath = new XalanXPathAdapter();
    	
    	Map<String,String> prefixes = new HashMap<String,String>();
    	prefixes.put("xsl","http://www.w3.org/1999/XSL/Transform");
    	
    	XPathAdapter.Result res = xpath.evaluateExpression(source, prefixes, "/xsl:transform");
    	
    	assertEquals("node-set of size 0",res.getType());
    	assertEquals("",res.getStringValue());
    	
    	assertTrue(res.isNodeSet());
    	assertEquals(0,res.size());
    	
    	XMLFragmentsString frags = res.toXMLFragmentsString();
    	assertEquals(0,frags.getFragmentCount());
    	
    	res.toString();
    }
    
    @Test
    public void testNamespaceContext(){
    	Map<String,String> prefixes = new HashMap<String,String>();
    	prefixes.put("xsl","http://www.w3.org/1999/XSL/Transform");
    	prefixes.put("","http://www.w3.org/1999/XSL/Transform");
    	prefixes.put("a","urn:a");
    	XalanXPathAdapter.PrefixResolverImpl ctx = new XalanXPathAdapter.PrefixResolverImpl(prefixes);
    	
    	assertEquals("http://www.w3.org/1999/XSL/Transform",ctx.getNamespaceForPrefix("xsl"));
    	assertEquals("http://www.w3.org/1999/XSL/Transform",ctx.getNamespaceForPrefix(""));
    	assertEquals("http://www.w3.org/1999/XSL/Transform",ctx.getNamespaceForPrefix("xsl",null));
    	assertNull(ctx.getBaseIdentifier());
    	assertFalse(ctx.handlesNullPrefixes());
    }
}
