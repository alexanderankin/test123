package tests;

import tests.jump.JumpPluginTest;
import tests.jump.ProjectBufferTest;
import tests.jump.ctags.CtagsBufferTest;
import tests.jump.ctags.CtagsEntryTest;
import tests.jump.ctags.CtagsParserTest;
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
		suite.addTestSuite(JumpPluginTest.class);
		suite.addTestSuite(ProjectBufferTest.class);
		return suite;
	}
}
//TODO: CtagsMain test