package tests.ctags;

import java.io.IOException;

import tests.AllTests;
import jump.ctags.CtagsBuffer;
import jump.ctags.CtagsMain;
import jump.ctags.CtagsParser;
import junit.framework.TestCase;

public class CtagsParserTest extends TestCase {
	
	public void testIsValidExtension() {
		
		CtagsMain.setUnsupportedExtensions(new String[] {"foo", "bar", "baz"});
		CtagsParser pa = new CtagsParser();
		assertTrue(pa.isValidExtension("test-file.sh"));
		assertTrue(pa.isValidExtension("test-file"));
		assertFalse(pa.isValidExtension("test-file.foo"));
		assertFalse(pa.isValidExtension("test-file.foo.baz"));
		assertTrue(pa.isValidExtension("foo.bar.baz.exe"));
	}
	
	
	public void testParseFile() {
		CtagsMain main = new CtagsMain(AllTests.PATH_TO_CTAGS_EXE);
		CtagsParser pa = new CtagsParser();
		try {
			CtagsBuffer buf = pa.parse(AllTests.TEST_FILE_FOR_CTAGS);
			assertNotNull(buf);
			assertEquals(10, buf.size());
		}
		catch (IOException e) {
			fail("Unexpected IOException handled");
		}
	}

}
