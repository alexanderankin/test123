package tests;

import tests.ctags.CtagsBufferTest;
import tests.ctags.CtagsEntryTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {


	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Jump! jEdit plugin");
		suite.addTestSuite(CtagsEntryTest.class);
		suite.addTestSuite(CtagsBufferTest.class);
		return suite;
	}
}
