/*
 * SchemaLoaderTest.java
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.validation.ValidatorHandler;

import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.thaiopensource.xml.sax.DraconianErrorHandler;
// }}}


/**
 * $Id$
 */
public class SchemaLoaderTest{
	private static File testData;
    private static SchemaLoader l;

    @BeforeClass
    public static void setUpjEdit() throws IOException{
        TestUtils.beforeClass();
        testData = new File(System.getProperty("test_data")).getCanonicalFile();
        assertTrue(testData.exists());
        l = SchemaLoader.instance();
        assertNotNull(l);
    }

    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }

    @Test
    public void testRNG() throws SAXException, IOException{
    	File rngSchema = new File(testData,"relax_ng/actions.rng");

    	ValidatorHandler verifierFilter = l.loadJaxpGrammar(
    			null,
    			rngSchema.getPath(),
    			new DraconianErrorHandler(),
    			null);
    	
    	assertNotNull(verifierFilter);
    	
    	File badActions = new File(testData,"relax_ng/actions.xml");
		XMLReader reader = XMLReaderFactory.createXMLReader();
			
		javax.xml.parsers.SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
			
		reader.setContentHandler(verifierFilter);
		verifierFilter.setContentHandler(new DefaultHandler());
		verifierFilter.setErrorHandler(new DraconianErrorHandler());
			
		// test that it accepts valid content
    	File goodActions = new File(testData,"relax_ng/valid_actions.xml");
		reader.parse(new InputSource(goodActions.toURI().toURL().toString()));

		
		// test that it throws errors 
		verifierFilter = l.loadJaxpGrammar(
    			null,
    			rngSchema.getPath(),
    			new DraconianErrorHandler(),
    			null);
		reader.setContentHandler(verifierFilter);
		verifierFilter.setContentHandler(new DefaultHandler());
		verifierFilter.setErrorHandler(new DraconianErrorHandler());
    	
		try{
			reader.parse(new InputSource(badActions.toURI().toURL().toString()));
			fail("should throw an exception");
		}catch(SAXParseException spe){
			assertNotNull(spe.getMessage());
			assertTrue(spe.getMessage(),spe.getMessage().contains("CODDE"));
		}

    }
    
    @Test
    public void testXSD() throws SAXException, IOException{
    	File xsdSchema = new File(testData,"simple/actions.xsd");

    	ValidatorHandler verifierFilter = l.loadJaxpGrammar(
    			null,
    			xsdSchema.getPath(),
    			new DraconianErrorHandler(),
    			null);
    	
    	assertNotNull(verifierFilter);
    	
    	File badActions = new File(testData,"relax_ng/actions.xml");
		XMLReader reader = XMLReaderFactory.createXMLReader();
			
		javax.xml.parsers.SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
			
		reader.setContentHandler(verifierFilter);
		verifierFilter.setContentHandler(new DefaultHandler());
		verifierFilter.setErrorHandler(new DraconianErrorHandler());
			
		// test that it accepts valid content
    	File goodActions = new File(testData,"relax_ng/valid_actions.xml");
		reader.parse(new InputSource(goodActions.toURI().toURL().toString()));

		
		// test that it throws errors 
		verifierFilter = l.loadJaxpGrammar(
    			null,
    			xsdSchema.getPath(),
    			new DraconianErrorHandler(),
    			null);
		reader.setContentHandler(verifierFilter);
		verifierFilter.setContentHandler(new DefaultHandler());
		verifierFilter.setErrorHandler(new DraconianErrorHandler());
    	
		try{
			reader.parse(new InputSource(badActions.toURI().toURL().toString()));
			fail("should throw an exception");
		}catch(SAXParseException spe){
			assertNotNull(spe.getMessage());
			assertTrue(spe.getMessage(),spe.getMessage().contains("CODDE"));
		}
    }
    
    @Test
    public void testNoSchemaThere(){
    	try{
			l.loadJaxpGrammar(
					null,
					"file:/not_there",
					new DraconianErrorHandler(),
					null);
			fail("should throw an exception");
		}catch(IOException ioe){
			//fine
		}catch(SAXException se){
			fail("wrong exception  :"+se);
		}
   }
   
   /** this test fails... */
   @Test
   public void testBrokenSchema(){
    	File brokenSchema = new File(testData,"dir with space/actions.xsd");
    	try{
			l.loadJaxpGrammar(
					null,
					brokenSchema.toURI().toString(),
					new DraconianErrorHandler(),
					null);
			fail("should throw an exception");
		}catch(IOException ioe){
			fail("wrong exception  :"+ioe);
		}catch(SAXException se){
			//fine
		}
   }
}
