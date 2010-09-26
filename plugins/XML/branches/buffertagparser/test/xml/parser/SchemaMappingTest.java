/*
 * SchemaMappingTest.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Eric Le Lay
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
import org.xml.sax.*;

import static xml.parser.SchemaMapping.*;

/**
 * $Id$
 */
public class SchemaMappingTest{
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
    public void testEmptyMapping(){
    	SchemaMapping m = new SchemaMapping();
    	assertNull(m.getBaseURI());
    	assertNull(m.getSchemaForDocument(null, "actions.xml",
		null,null,"ACTIONS",true));
    }
    
    @Test
    public void testBuiltInSchema()  throws URISyntaxException{
    	URI builtin = getClass().getClassLoader().getResource("xml/dtds/schemas.xml").toURI();
    	SchemaMapping m = SchemaMapping.fromDocument(builtin.toString());
    	assertNotNull(m);
    	
    	// 2 built-in rules
    	assertEquals(new Result(builtin,"locate.rng"),m.getSchemaForDocument(null, "schemas.xml",
		null,null,"locatingRules",true));
    	assertEquals(new Result(builtin,"locate.rng"),m.getSchemaForDocument(null, "schemas.xml",
		null,null,"a",true));
		
		// non-matching rule
    	assertNull(m.getSchemaForDocument(null, "actions.xml",
		null,null,"ACTIONS",true));
		
    }
    
    @Test
    public void testAddRule() throws MalformedURLException, URISyntaxException{
    	SchemaMapping m = new SchemaMapping();

    	
    	// test taken into account
    	m.addRule(new DocumentElementRule(null,null, "ACTIONS", "actions.rng", false));
    	assertEquals(new Result(null,"actions.rng"),m.getSchemaForDocument(null, "actions.xml",
		null,null,"ACTIONS",true));
		
    	m.addRule(new URIResourceRule(new URI("file:///"),"actions.xml", "else.rng", false));

    	// test taken into account
    	assertEquals(new Result(new URI("file:///"),"else.rng"),m.getSchemaForDocument(null, "actions.xml",
		null,null,"TOTO",true));

		// test precedence ( both match, but first one wins)
    	assertEquals(new Result(null,"actions.rng"),m.getSchemaForDocument(null, "actions.xml",
		null,null,"ACTIONS",true));
		
    }

    @Test
    public void testAddTypeId(){
    	SchemaMapping m = new SchemaMapping();
    	
    	// test undefined typeId
    	m.addRule(new DocumentElementRule(null,null, "ACTIONS", "actions", true));

    	assertEquals(null,m.getSchemaForDocument(null, "actions.xml",
		null,null,"ACTIONS",true));
    	
		m.addTypeId("actions","actions.rng",false);

    	// test taken into account
    	assertEquals(new Result(null,"actions.rng"),m.getSchemaForDocument(null, "actions.xml",
		null,null,"ACTIONS",true));
		
    	m.addTypeId("actions", "else.rng",false);

		// test precedence ( the first one wins)
    	assertEquals(new Result(null,"actions.rng"),m.getSchemaForDocument(null, "actions.xml",
		null,null,"ACTIONS",true));
		
    }
    
     @Test
    public void testDefaultRule(){
    	Rule r;
    	
    	try{
    		new DefaultRule(null,null,false);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	
    	try{
    		new DefaultRule(null,"",false);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	
    	r = new DefaultRule(null,"target",false);
    	assertTrue(r.matchNamespace("urn:ns"));
    	assertTrue(r.matchURL("test.txt"));
    	assertTrue(r.matchDoctype("book"));
    	assertTrue(r.matchDocumentElement(null,"test"));
    	assertTrue(r.matchDocumentElement("t","test"));
    }

    @Test
    public void testDocumentElementRule(){
    	Rule r;
    	
    	try{
    		new DocumentElementRule(null,null,null,"target",false);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	
    	try{
    		new DocumentElementRule(null,null,"","target",false);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	
    	r = new DocumentElementRule(null,null,"test","target",false);
    	assertFalse(r.matchNamespace("urn:ns"));
    	assertFalse(r.matchURL("test.txt"));
    	assertFalse(r.matchDoctype("book"));
    	
    	// no prefix
    	assertTrue(r.matchDocumentElement(null,"test"));
    	
    	// with prefix
    	assertTrue(r.matchDocumentElement("t","test"));
    	
    	// no match
    	assertFalse(r.matchDocumentElement(null,"test2"));
    	
    	r = new DocumentElementRule(null, "t","test","target",false);
    	
    	// no prefix
    	assertFalse(r.matchDocumentElement(null,"test"));
    	
    	// with prefix
    	assertTrue(r.matchDocumentElement("t","test"));
    	
    	// no match
    	assertFalse(r.matchDocumentElement("tt","test"));
    	
    	
    }
    
    @Test
    public void testDoctypeRule(){
    	Rule r;
    	
    	try{
    		new DoctypeRule(null, null,"target",false);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	
    	try{
    		new DoctypeRule(null,"","target",false);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	
    	r = new DoctypeRule(null,"test","target",false);
    	assertFalse(r.matchNamespace("urn:ns"));
    	assertFalse(r.matchURL("test.txt"));
    	assertFalse(r.matchDocumentElement(null,"test"));
    	
    	assertTrue(r.matchDoctype("test"));
    	assertFalse(r.matchDoctype("test2"));
    }
    
    @Test
    public void testNamespaceRule(){
    	Rule r;
    	
    	try{
    		new NamespaceRule(null,null,"target",false);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	
    	r = new NamespaceRule(null,"urn:ns","target",false);
    	
    	assertFalse(r.matchDoctype("test"));
    	assertFalse(r.matchURL("test.txt"));
    	assertFalse(r.matchDocumentElement(null,"test"));
    	
    	assertTrue(r.matchNamespace("urn:ns"));
    	assertFalse(r.matchNamespace("urn:ns2"));
    	
    	// default namespace
    	r = new NamespaceRule(null,"","target",false);
    	assertTrue(r.matchNamespace(""));
    	assertFalse(r.matchNamespace("urn:ns2"));
    	
    }
    
    @Test
    public void testURIPatternRule(){
    	Rule r;
    	
    	try{
    		new URIPatternRule(null,null,"target",false);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	
    	r = new URIPatternRule(null,"test.txt","target",false);
    	
    	assertFalse(r.matchDoctype("test"));
    	assertFalse(r.matchDocumentElement(null,"test"));
    	assertFalse(r.matchNamespace("urn:ns"));
    	
    	assertTrue(r.matchURL("test.txt"));
    	assertFalse(r.matchURL("test2.txt"));
    	
    	r = new URIPatternRule(null,"*.txt","target",false);
    	assertTrue(r.matchURL("test.txt"));
    	assertFalse(r.matchURL("test.txt.2"));
    	
    	//relative/absolute uri :
    	r = new URIPatternRule(null,"*.txt","target",false);
    	assertTrue(r.matchURL("test/test.txt"));
    	assertTrue(r.matchURL("file:///c:/test.txt"));
    	
    	r = new URIPatternRule(null,"*/A.txt","target",false);
    	assertTrue(r.matchURL("test/A.txt"));
    	assertTrue(r.matchURL("file:///c:/A.txt"));
    	
    	assertFalse(r.matchURL("A.txt"));
    	
    	//opaque URI
    	r = new URIPatternRule(null,"*jedit*","target",false);
    	assertTrue(r.matchURL("mailto:jedit-devel@sourceforge.net"));
    	assertFalse(r.matchURL("mailto:test:test"));
    }
    
    
    @Test
    public void testURIResourceRule() throws MalformedURLException, URISyntaxException{
    	Rule r;
    	
    	try{
    		new URIResourceRule(null,null,"target",false);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}

    	try{
    		new URIResourceRule(null,"test","target",false);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	
    	// relative url
		try{
			new URIResourceRule(null, "relative","target",false);
			fail("should throw an exception");
		}catch(IllegalArgumentException iae){
			//fine
		}
		
		//illegal URI
		try{
			new URIResourceRule(null, "\\//","target",false);
			fail("should throw an exception");
		}catch(IllegalArgumentException iae){
			//fine
		}

    	r = new URIResourceRule(null,"file:///test.txt","target",false);
    	
    	assertFalse(r.matchDoctype("test"));
    	assertFalse(r.matchDocumentElement(null,"test"));
    	assertFalse(r.matchNamespace("urn:ns"));
    	
    	// absolute uri as pattern

    	assertTrue(r.matchURL("file:///test.txt"));
    	assertTrue(r.matchURL("file:///A/../test.txt")); // same path
    	
    	// relative uri as pattern
    	r = new URIResourceRule(new URI("file:///A/schemas.xml"),"test.txt","target",false);
    	assertTrue(r.matchURL("file:///A/test.txt"));
    	assertFalse(r.matchURL("file:///test.txt"));
    	
    	//relative URI to match
    	assertTrue(r.matchURL("test.txt"));
    }
 
	@Test
    public void testBase() throws MalformedURLException, URISyntaxException{
     	URI builtin = getClass().getClassLoader().getResource("xml/dtds/schemas.xml").toURI();
    	SchemaMapping m = SchemaMapping.fromDocument(builtin.toString());
    	assertNotNull(m);

    	
    	// schemas.xml base
    	assertEquals(new Result(builtin,"relaxng.rng"),m.getSchemaForDocument(null, "actions.rng",
		null,null,"ACTIONS",true));
		
    	m.addRule(new URIResourceRule(new URI("file:///schemas.xml"),"actions.xml", "else.rng", false));

    	// rule base
    	assertEquals(new Result(new URI("file:///schemas.xml"),"else.rng"),
    		m.getSchemaForDocument(null, "actions.xml",null,null,"TOTO",true));

	}
	
	
	@Test
	public void testIncludeMapping() throws MalformedURLException, URISyntaxException{
     	
		try{
			new SchemaMapping.IncludeMapping(null, (String)null);
			fail("should throw an exception");
		}catch(IllegalArgumentException iae){
			//fine
		}
     	
		try{
			new SchemaMapping.IncludeMapping(null, (SchemaMapping)null);
			fail("should throw an exception");
		}catch(IllegalArgumentException iae){
			//fine
		}
		
		try{
			new SchemaMapping.IncludeMapping(null, "relative");
			fail("should throw an exception");
		}catch(IllegalArgumentException iae){
			//fine
		}
		
		try{
			new SchemaMapping.IncludeMapping(null, "\\//");
			fail("should throw an exception");
		}catch(IllegalArgumentException iae){
			//fine
		}

		URI builtin = getClass().getClassLoader().getResource("xml/dtds/schemas.xml").toURI();
     	
    	SchemaMapping m = new SchemaMapping();
    	
    	m.addRule(new SchemaMapping.IncludeMapping(null, builtin.toString()));
    	    	
    	// used the include rule AND used the base uri of the included schema
    	assertEquals(new Result(builtin,"relaxng.rng"),m.getSchemaForDocument(null, "actions.rng",
		null,null,"ACTIONS",true));
		
		// this rule will override the default included rule
    	m.insertRuleAt(0,new URIPatternRule(null,"actions.*", "else.rng", false));

    	// rule base
    	assertEquals(new Result(null,"else.rng"),
    		m.getSchemaForDocument(null, "actions.xml",null,null,"TOTO",true));
	}
	
	@Test
	public void testTransformURI() throws MalformedURLException, URISyntaxException, IOException{
		Mapping r;
		
    	try{
    		new TransformURI(null,null,"target");
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	try{
    		new TransformURI(null,"from",null);
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}

    	try{
    		new TransformURI(null,"*.xml","*/*.xsd");
    		fail("should throw an exception");
    	}catch(RuntimeException e){
    		//fine
    	}
    	
    	System.err.println("hello");
    	r = new TransformURI(null,"*.xml","*.xsd");
    	
    	File existsXML = new File(testData,"simple/actions.xml");
    	File existsXSD = new File(testData,"simple/actions.xsd");

    	// pattern matches and result exists
    	assertEquals(new Result(null,existsXSD.toURL().toString()),
    		r.getSchemaForDocument(null, existsXML.toURL().toString(),null,null,null,true));
    	
    	// test doesn't match
    	assertNull(r.getSchemaForDocument(null, "test.txt",null,null,null,true));

    	// test no result resource
    	File noXSD = new File(testData,"dtd/actions.xml");
    	assertNull(r.getSchemaForDocument(null, noXSD.toURL().toString(),null,null,null,true));

    	// test result relative to base
    	r = new TransformURI(existsXML.toURL().toURI(),"*.xml","../simple/*.xsd");
    	
    	assertEquals(new Result(existsXML.toURL().toURI(),"../simple/actions.xsd"),
    		r.getSchemaForDocument(null, "actions.xml",null,null,null,true));

    	r = new TransformURI(null,"*.xml","../simple/*.xsd");

    	assertEquals(new Result(null,new File(noXSD.getParentFile(),"../simple/actions.xsd").toURL().toString()),
    		r.getSchemaForDocument(null, noXSD.toURL().toString(),null,null,null,true));
	}
	
	@Test
	public void testToDocument() throws MalformedURLException, URISyntaxException, IOException{
     	URI builtin = getClass().getClassLoader().getResource("xml/dtds/schemas.xml").toURI();
    	SchemaMapping m = new SchemaMapping();

		Mapping r;
    	r = new SchemaMapping.IncludeMapping(null, builtin.toString());
    	m.addRule(r);
    	r = new SchemaMapping.IncludeMapping(null, builtin.toString());
    	r.setExplicitBase("file:///a.xml");
    	m.addRule(r);
    	
    	r = new DocumentElementRule(null,null,"test","target",true);
    	m.addRule(r);
    	r = new DocumentElementRule(null,null,"test","target",false);
    	r.setExplicitBase("");
    	m.addRule(r);

     	r = new DoctypeRule(null,"test","target",false);
     	m.addRule(r);
     	r = new DoctypeRule(null,"test","target",true);
    	r.setExplicitBase("file:///b.xml");
    	m.addRule(r);
     	
    	r = new NamespaceRule(null,"urn:ns","target",false);
     	m.addRule(r);
    	r = new NamespaceRule(null,"urn:ns","target",true);
    	r.setExplicitBase("file:///c.xml");
    	m.addRule(r);

    	r = new URIPatternRule(null,"*.txt","target",false);
     	m.addRule(r);
    	r = new URIPatternRule(null,"*.txt","target",true);
    	r.setExplicitBase("file:///d.xml");
    	m.addRule(r);

    	r = new URIResourceRule(null,"file:///test.txt","target",false);
     	m.addRule(r);
    	r = new URIResourceRule(null,"file:///test.txt","target",true);
    	r.setExplicitBase("file:///e.xml");
    	m.addRule(r);

     	r = new DefaultRule(null,"targetDefault",false);
     	m.addRule(r);
     	r = new DefaultRule(null,"targetDefault",true);
    	r.setExplicitBase("file:///f.xml");
    	m.addRule(r);
    	
    	r = new TransformURI(null,"*.xml","*.xsd");
     	m.addRule(r);
    	r = new TransformURI(null,"*.xml","*.xsd");
    	r.setExplicitBase("g.xml");
    	m.addRule(r);

    	m.addTypeId("toto","titi",true);
    	m.addTypeId("titi","relax.rng",false);
    	
    	File f = File.createTempFile("schemamappingtest",".xml");
    	try{
    		m.toDocument(f.getPath());
    	}catch(IOException ioe){
    		fail("should not throw an exception : "+ioe);
    	}
    	
    	SchemaMapping m2 = null;
    	try{
    		m2 = SchemaMapping.fromDocument(f.toURL().toString());
    	}catch(IOException ioe){
    		fail("should not throw an exception : "+ioe);
    	}
    	
    	File f2 = File.createTempFile("schemamappingtest",".xml");
    	try{
    		m2.toDocument(f2.getPath());
    	}catch(IOException ioe){
    		fail("should not throw an exception : "+ioe);
    	}
    	
    	InputStream in1 = new FileInputStream(f);
    	InputStream in2 = new FileInputStream(f2);
    	
    	try{
    	assertInputStreamEquals(in1,in2);
    	}finally{
    		in1.close();
    		in2.close();
    	}
    	//they are deleted only on success => easier debugging
    	f.delete();
    	f2.delete();
	}
}
