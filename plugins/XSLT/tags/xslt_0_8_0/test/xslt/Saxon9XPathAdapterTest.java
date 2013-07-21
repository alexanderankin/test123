/*
 * Saxon9XPathAdapterTest.java
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
 * unit tests of the Saxon 9 XPath engine adapter
 * $Id$
 */
@RunWith(JEditRunner.class)
public class Saxon9XPathAdapterTest{

	@Rule
	public TestData testData = new TestData();

    
    @Test
    public void testElement() throws Exception{
    	final File xsl = new File(testData.get(),"simple/transform.xsl");
    	Buffer b = openFile(xsl.getPath());
    	Pause.pause(1000);
    	Document source  = DocumentCache.getFromCache(b);
    	
    	Saxon9XPathAdapter xpath = new Saxon9XPathAdapter();
    	
    	Map<String,String> prefixes = new HashMap<String,String>();
    	prefixes.put("xsl","http://www.w3.org/1999/XSL/Transform");
    	
    	XPathAdapter.Result res = xpath.evaluateExpression(source, prefixes, "/xsl:stylesheet");
    	
    	assertEquals("element(Q{http://www.w3.org/1999/XSL/Transform}stylesheet)",res.getType());
    	assertThat(res.getStringValue()).contains("hello");
    	
    	assertTrue(res.isNodeSet());
    	assertEquals(1,res.size());
    	
    	XPathAdapter.XPathNode n = res.get(0);
    	assertTrue(n.hasExpandedName());
    	assertFalse(n.hasDomValue());
    	assertEquals("element(Q{http://www.w3.org/1999/XSL/Transform}stylesheet)",n.getType());
    	assertEquals("xsl:stylesheet",n.getName());
    	
    	XMLFragmentsString frags = res.toXMLFragmentsString();
    	assertEquals(1,frags.getFragmentCount());
    }
    
    @Test
    public void testComment() throws Exception{
    	final File xsl = new File(testData.get(),"base_uri_bug/base-uri-bug.xsl");
    	Buffer b = openFile(xsl.getPath());
    	Pause.pause(1000);
    	Document source  = DocumentCache.getFromCache(b);
    	
    	Saxon9XPathAdapter xpath = new Saxon9XPathAdapter();
    	
    	Map<String,String> prefixes = new HashMap<String,String>();
    	prefixes.put("xsl","http://www.w3.org/1999/XSL/Transform");
    	
    	XPathAdapter.Result res = xpath.evaluateExpression(source, prefixes, "//comment()");
    	
    	assertEquals("comment()",res.getType());
    	assertThat(res.getStringValue()).contains("this stylesheet was submitted");
    	
    	assertTrue(res.isNodeSet());
    	assertEquals(1,res.size());
    	
    	XPathAdapter.XPathNode n = res.get(0);
    	assertFalse(n.hasExpandedName());
    	assertTrue(n.hasDomValue());
    	assertEquals("comment()",n.getType());
    	assertEquals("",n.getName());

    	XMLFragmentsString frags = res.toXMLFragmentsString();
    	assertEquals(1,frags.getFragmentCount());
    }
    
    @Test
    public void testNumberSequence() throws Exception{
    	final File xsl = new File(testData.get(),"simple/transform.xsl");
    	Buffer b = openFile(xsl.getPath());
    	Pause.pause(1000);
    	Document source  = DocumentCache.getFromCache(b);
    	
    	Saxon9XPathAdapter xpath = new Saxon9XPathAdapter();
    	
    	Map<String,String> prefixes = new HashMap<String,String>();
    	prefixes.put("xsl","http://www.w3.org/1999/XSL/Transform");
    	
    	XPathAdapter.Result res = xpath.evaluateExpression(source, prefixes, "(1+1,1 div 2)");
    	
    	assertEquals("sequence of xs:decimal",res.getType());
    	assertEquals("2 0.5",res.getStringValue());
    	
    	assertTrue(res.isNodeSet());
    	assertEquals(2,res.size());
    	
    	XPathAdapter.XPathNode n = res.get(0);
    	assertFalse(n.hasExpandedName());
    	assertTrue(n.hasDomValue());
    	assertEquals("xs:integer",n.getType());
    	assertEquals("2",n.getDomValue());
    	assertEquals(null,n.getName());

    	XMLFragmentsString frags = res.toXMLFragmentsString();
    	assertEquals(2,frags.getFragmentCount());
    }
    
    @Test
    public void testEmptySequence() throws Exception{
    	final File xsl = new File(testData.get(),"simple/transform.xsl");
    	Buffer b = openFile(xsl.getPath());
    	Pause.pause(1000);
    	Document source  = DocumentCache.getFromCache(b);
    	
    	Saxon9XPathAdapter xpath = new Saxon9XPathAdapter();
    	
    	Map<String,String> prefixes = new HashMap<String,String>();
    	prefixes.put("xsl","http://www.w3.org/1999/XSL/Transform");
    	
    	XPathAdapter.Result res = xpath.evaluateExpression(source, prefixes, "/xsl:transform");
    	
    	assertEquals("empty sequence",res.getType());
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
    	Saxon9XPathAdapter.NamespaceContextImpl ctx = new Saxon9XPathAdapter.NamespaceContextImpl(prefixes);
    	
    	assertEquals("http://www.w3.org/1999/XSL/Transform",ctx.getNamespaceURI("xsl"));
    	assertEquals("http://www.w3.org/1999/XSL/Transform",ctx.getNamespaceURI(""));
    	assertNotNull(ctx.getNamespaceURI("xmlns"));
    	assertNotNull(ctx.getNamespaceURI("xml"));
    	Iterator<String> it = ctx.getPrefixes("http://www.w3.org/1999/XSL/Transform");
    	List<String> l = new ArrayList<String>();
    	while(it.hasNext())l.add(it.next());
    	assertThat(l).containsOnly("xsl","");
    	assertEquals("a",ctx.getPrefix("urn:a"));
    	
    	assertEquals("",ctx.getNamespaceURI("toto"));
    	assertEquals(null,ctx.getPrefix("urn:toto"));
    	
    	assertEquals("xml",ctx.getPrefix("http://www.w3.org/XML/1998/namespace"));
    	assertEquals("xmlns",ctx.getPrefix("http://www.w3.org/2000/xmlns/"));
    	try{
    		ctx.getNamespaceURI(null);
    		fail("should throw an exception");
    	}catch(IllegalArgumentException iae){
    		// that's expected
    	}
    	try{
    		ctx.getPrefix(null);
    		fail("should throw an exception");
    	}catch(IllegalArgumentException iae){
    		// that's expected
    	}
    }
}
