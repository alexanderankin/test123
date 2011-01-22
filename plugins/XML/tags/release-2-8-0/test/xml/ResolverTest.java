/*
 * ResolverTest.java
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
package xml;

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
import static org.gjt.sp.jedit.testframework.TestUtils.*;
// }}}

import java.io.*;
import org.xml.sax.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.Buffer;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.PluginUpdate;

/**
 * $Id$
 */
public class ResolverTest{
	private static File testData;
	private Resolver resolver;

    @BeforeClass
    public static void setUpjEdit() {
        TestUtils.beforeClass();
        testData = new File(System.getProperty("test_data"));
        assertTrue(testData.exists());
    }

    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }

    @Before
    public void init()
    {
    	resolver = Resolver.instance();
    }
    
    /** test the return value of resolveEntity()
     *  with systemId="" and publicId="" 
     */
	@Test
	public void testBothEmptyOrNull() throws IOException, SAXException{
		
		assertEquals(null, resolver.resolveEntity(null, null));
		assertEquals(null, resolver.resolveEntity(null, ""  ));
		assertEquals(null, resolver.resolveEntity(""  , null));
		assertEquals(null, resolver.resolveEntity(""  , ""  ));
	}
    
    /** test the return value of resolveEntity()
     *  with systemId="local-file" and inexistant sytemId 
     */
	@Test
	public void testAbsoluteLocalFile() throws IOException, SAXException{
		
		/* {{{ file exists */
		File actual = new File(new File(testData,"simple"),"actions.xsd");
		
		InputSource res = resolver.resolveEntity(null, null, null,
			actual.getPath());
		
		assertEquals(actual.toURI().toString(), res.getSystemId());
		
		assertNotNull(res.getByteStream());
		assertInputStreamEquals(new FileInputStream(actual),res.getByteStream());

		/* }}} */
		/* {{{ file doesn't exist */
		actual = new File(testData,"DOES_NOT_EXIST");
		
		try{
			res = resolver.resolveEntity(null, actual.getPath());
			fail("should throw FileNotFoundException");
		}catch(FileNotFoundException fnfe){
			//that's fine
		}
		/* }}} */
	}
	
    /** test the return value of resolveEntity()
     *  with systemId="relative-local-file" and current="local-file" 
     */
	@Test
	public void testRelativeLocalFile() throws IOException, SAXException{
		File current = new File(new File(testData,"simple"),"actions.xml");
		File actual = new File(new File(testData,"simple"),"actions.xsd");
		
		InputSource res = resolver.resolveEntity(null, null, current.getPath(),
			actual.getName());
		
		assertEquals(actual.toURI().toString(), res.getSystemId());
		
		assertNotNull(res.getByteStream());
		assertInputStreamEquals(new FileInputStream(actual),res.getByteStream());

	}
	
	
	/** test the network dialog 
     */
	@Test
	public void testNetworkMode() throws IOException, SAXException{
		
		
		resolver.setNetworkMode(Resolver.LOCAL);
		assertEquals(Resolver.LOCAL,resolver.getNetworkMode());
		try{
			resolver.resolveEntity(null,"http://www.jedit.org/index.php");		
			fail("should throw IOException");
		}catch(IOException ioe){
			//that's fine
		}
		resolver.setNetworkMode(Resolver.ALWAYS);
		assertEquals(Resolver.ALWAYS,resolver.getNetworkMode());

		InputSource res = resolver.resolveEntity(null,"http://www.jedit.org/index.php");
		assertEquals("http://www.jedit.org/index.php",res.getSystemId());
	
		resolver.clearCache();
		resolver.setNetworkMode(Resolver.ASK);
		assertEquals(Resolver.ASK,resolver.getNetworkMode());
		
		// accept downloading
		ClickT clickT = new ClickT(Option.YES);
		clickT.start();
		
		res = resolver.resolveEntity(null,"http://www.jedit.org/index.php");
		assertEquals("http://www.jedit.org/index.php",res.getSystemId());

		clickT.waitForClick();
		
		//won't be asked now !
		res = resolver.resolveEntity(null,"http://www.jedit.org/index.php");
		assertEquals("http://www.jedit.org/index.php",res.getSystemId());
		
		// refuse downloading
		resolver.clearCache();
		clickT = new ClickT(Option.NO);
		clickT.start();
		
		try{
			res = resolver.resolveEntity( null,"http://www.jedit.org/index.php");
			fail("should throw an exception");
		}catch(IOException ioe){
			//expected
		}

		clickT.waitForClick();

		
		// try again : will fail without asking (URI == IGNORE)
		try{
			res = resolver.resolveEntity(null,"http://www.jedit.org/index.php");
			fail("should throw an exception");
		}catch(IOException ioe){
			//expected
		}
		
		// try again, after clearing the cache
		resolver.clearCache();

		clickT = new ClickT(Option.YES);
		clickT.start();
		
		res = resolver.resolveEntity(null,"http://www.jedit.org/index.php");
		assertEquals("http://www.jedit.org/index.php",res.getSystemId());
		
		clickT.waitForClick();

		// the file is in the cache : even if we set it to NEVER
		// we can use the cached one
		resolver.setNetworkMode(Resolver.LOCAL);
		res = resolver.resolveEntity(null,"http://www.jedit.org/index.php");
		assertEquals("http://www.jedit.org/index.php",res.getSystemId());

		
		/* }}} */
	}
	
	
    /** test the return value of resolveEntity()
     *  with :
     *		- a built-in public ID
     *		- a made-up public ID
     *		- a made-up public ID and a system ID as backup
     */
	@Test
	public void testPublic() throws IOException, SAXException{
		File actual = new File(new File(testData,"simple"),"actions.xsd");
		
		InputSource res = resolver.resolveEntity(
			  "-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN"
			, null);
		assertNotNull(res);
		
		// strange : the systemID is not set !
		//assertEquals("jeditresource:", res.getSystemId());
		
		// the resolver returns null, since the publicId can't be resolved
		res = resolver.resolveEntity("-//FUNNY LOOKING PUBLIC ID//EN",null);
		assertNull(res);
		res = resolver.resolveEntity("-//FUNNY LOOKING PUBLIC ID//EN", actual.toURL().toString());
		assertNotNull(res);
		assertEquals(actual.toURL().toString(),res.getSystemId());
		
	}
	

	/** verify that caching decisions are saved on exit
	 */
	@Test
	public void testSaveOnExit() throws IOException, SAXException{

		resolver.setNetworkMode(Resolver.ASK);
		resolver.clearCache();

		// cache some URLs
		
		ClickT clickT = new ClickT(Option.YES);
		clickT.start();
		
		InputSource res = resolver.resolveEntity("-//testSaveOnExit.ACCEPT//","http://www.jedit.org/index.php");
		
		clickT.waitForClick();
		
		clickT = new ClickT(Option.NO);
		clickT.start();
		
		try{
			res = resolver.resolveEntity("-//testSaveOnExit.REFUSE//", "http://www.w3.org/");
			fail("should throw an exception");
		}catch(IOException ioe){
			//fine : we refused
		}
		
		clickT.waitForClick();
		

		// the choice of the user to ignore a publicId is not saved
		// so asking for the same public id with a different system id succeeds
		res = resolver.resolveEntity("-//testSaveOnExit.REFUSE//","http://www.jedit.org/index.php");

		//reactivate the plugin
		reactivatePlugin(XmlPlugin.class);
		resolver = Resolver.instance();
		// verify that it has been saved
		
		// saved the systemId
		res = resolver.resolveEntity(null,"http://www.jedit.org/index.php");
		
		// saved the publicId
		res = resolver.resolveEntity("-//testSaveOnExit.ACCEPT//",null);
		
		
		// the choice of the user to ignore a resource is not saved
		// so we will be asked again about www.w3.org
		clickT = new ClickT(Option.NO);
		clickT.start();
		
		try{
			res = resolver.resolveEntity(null,"http://www.w3.org/");
			fail("should throw an exception");
		}catch(IOException ioe){
			//fine : we refused
		}
		
		resolver.clearCache();
		
		// verify that it has been cleared
		
		clickT = new ClickT(Option.YES);
		clickT.start();
		
		res = resolver.resolveEntity(null,"http://www.jedit.org/index.php");
		
		clickT.waitForClick();
		
	}
	
	
	/**
	 * verify that we get the fresh version of a resolved buffer
	 */
	@Test
	public void testOpenBuffer() throws IOException, SAXException{
		
		final String txt = "<actions/>\n";
		
		File actual = new File(new File(testData,"simple"),"actions.xml");
		
		final Buffer b = TestUtils.openFile( actual.getPath() );
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					b.remove(0,b.getLength());
					b.insert(0,txt);
				}
		});
		assertEquals(txt.length(),b.getLength());
		
		try{
			InputSource res = resolver.resolveEntity(null,actual.getPath());
			
			assertReaderEquals(new StringReader(txt), res.getCharacterStream());
		}finally{
			TestUtils.close(TestUtils.view(),b);
		}
	}

}
