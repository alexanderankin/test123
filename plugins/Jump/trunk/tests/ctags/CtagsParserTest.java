package tests.ctags;

import java.io.IOException;
import java.util.ArrayList;

import tests.AllTests;
import jump.ctags.CtagsBuffer;
import jump.ctags.CtagsMain;
import jump.ctags.CtagsParser;
import junit.framework.TestCase;

public class CtagsParserTest extends TestCase {
	
	private CtagsMain ctags;
	private CtagsParser parser;
	
	protected void setUp() throws Exception {
		super.setUp();
		ctags = new CtagsMain(AllTests.PATH_TO_CTAGS_EXE);
		parser = new CtagsParser();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testIsValidExtension() {
		CtagsMain.setUnsupportedExtensions(new String[] {"foo", "bar", "baz"});
		assertTrue(parser.isValidExtension("test-file.sh"));
		assertTrue(parser.isValidExtension("test-file"));
		assertFalse(parser.isValidExtension("test-file.foo"));
		assertFalse(parser.isValidExtension("test-file.foo.baz"));
		assertTrue(parser.isValidExtension("foo.bar.baz.exe"));
	}
	
	
	public void testParseFile() {
		try {
			CtagsBuffer buf = parser.parse(AllTests.TEST_FILE_FOR_CTAGS);
			assertNotNull(buf);
			assertEquals(10, buf.size());
		}
		catch (IOException e) {
			fail("Unexpected IOException handled");
		}
	}
	
	public void testParseFileList() {
		ArrayList list = new ArrayList();
		list.add(AllTests.TEST_FILE_FOR_CTAGS);
		list.add(AllTests.TEST_FILE_FOR_CTAGS2);
		
		try {
			CtagsBuffer buf = parser.parse(list);
			assertNotNull(buf);
			assertEquals(22, buf.size());
		}
		catch (IOException e) {
			fail("Unexpected IOException handled");
		}
	}

}
