package tests;

import tests.ctags.CtagsBufferTest;
import tests.ctags.CtagsEntryTest;
import tests.ctags.CtagsParserTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static final String TEST_FILE_FOR_CTAGS = "D:/Programmation/apps/" +
			"eclipse3M8/workspace/Jump/tests/FooClassForCtagsTests.java";
	public static final String TEST_FILE_FOR_CTAGS2 = "D:/Programmation/apps/" +
	"eclipse3M8/workspace/Jump/tests/FooClassForCtagsTests2.java";
	
	public static final String PATH_TO_CTAGS_EXE = "D:/Programmation/apps/" +
			"ctags55/ctags.exe";

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Jump! jEdit plugin");
		suite.addTestSuite(CtagsEntryTest.class);
		suite.addTestSuite(CtagsBufferTest.class);
		suite.addTestSuite(CtagsParserTest.class);
		return suite;
	}
}
