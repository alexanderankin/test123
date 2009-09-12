package xml;

// {{{ jUnit imports 

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

// }}}

import java.io.*;
import org.xml.sax.*;


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
		
		assertEquals(null, resolver.resolveEntity(null, null, null, null));
		assertEquals(null, resolver.resolveEntity(null, null, null, ""  ));
		assertEquals(null, resolver.resolveEntity(null, ""  , null, null));
		assertEquals(null, resolver.resolveEntity(null, ""  , null, ""  ));
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
			res = resolver.resolveEntity(null, null, null,
			actual.getPath());
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
		
		
		Resolver.instance().setNetworkModeVal(1);
		assertEquals("local",Resolver.instance().getNetworkMode());
		try{
			resolver.resolveEntity(null, null, null,"http://www.jedit.org/index.php");		
			fail("should throw IOException");
		}catch(IOException ioe){
			//that's fine
		}
		Resolver.instance().setNetworkModeVal(2);
		assertEquals("always",Resolver.instance().getNetworkMode());

		InputSource res = resolver.resolveEntity(null, null, null,"http://www.jedit.org/index.php");
		assertEquals("http://www.jedit.org/index.php",res.getSystemId());
		
		Resolver.instance().setNetworkModeVal(0);
		assertEquals("ask",Resolver.instance().getNetworkMode());
		
		// accept downloading
		Thread clickT = new Thread(){
			public void run(){
				TestUtils.jEditFrame().optionPane(Timeout.timeout(2000)).yesButton().click();
		}};
		clickT.start();
		
		res = resolver.resolveEntity(null, null, null,"http://www.jedit.org/index.php");
		assertEquals("http://www.jedit.org/index.php",res.getSystemId());

		try{clickT.join();}catch(Exception e){}
		
		//won't be asked now !
		res = resolver.resolveEntity(null, null, null,"http://www.jedit.org/index.php");
		assertEquals("http://www.jedit.org/index.php",res.getSystemId());
		
		// refuse downloading
		Resolver.instance().clearCache();
		clickT = new Thread(){
			public void run(){
				TestUtils.jEditFrame().optionPane(Timeout.timeout(2000)).noButton().click();
		}};
		clickT.start();
		
		try{
			res = resolver.resolveEntity(null, null, null,"http://www.jedit.org/index.php");
			fail("should throw an exception");
		}catch(IOException ioe){
			//expected
		}
		
		// try again : will fail without asking (URI == IGNORE)
		try{
			res = resolver.resolveEntity(null, null, null,"http://www.jedit.org/index.php");
			fail("should throw an exception");
		}catch(IOException ioe){
			//expected
		}
		
		// try again, after clearing the cache
		Resolver.instance().clearCache();

		clickT = new Thread(){
			public void run(){
				TestUtils.jEditFrame().optionPane(Timeout.timeout(2000)).yesButton().click();
		}};
		clickT.start();
		
		res = resolver.resolveEntity(null, null, null,"http://www.jedit.org/index.php");
		assertEquals("http://www.jedit.org/index.php",res.getSystemId());
		
		/* }}} */
	}
	
	
    /** test the return value of resolveEntity()
     *  with a built-in public URI 
     */
	@Test
	public void testBuiltInCatalogPublic() throws IOException, SAXException{
		
		
		InputSource res = resolver.resolveEntity(null,
			"-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN",
			null, null);
		
		// strange : the systemID is not set !
		//assertEquals("jeditresource:", res.getSystemId());
		
	}

	
	public static void assertInputStreamEquals(InputStream expectedIn, InputStream actualIn)
	throws IOException
	{
		try{
			for(int i=0,e=0,a=0;a!=-1 && e!=-1;){
				a=actualIn.read();
				e=expectedIn.read();
				assertEquals("at byte "+i, a,e);
			}
		}finally{
			expectedIn.close();
			actualIn.close();
		}
	}
	
}
