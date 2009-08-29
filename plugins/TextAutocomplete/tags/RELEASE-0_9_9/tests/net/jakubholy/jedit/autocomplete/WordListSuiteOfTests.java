/*
 * Created on 9.2.2005
 * $id
 */
package net.jakubholy.jedit.autocomplete;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 */
public class WordListSuiteOfTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for net.jakubholy.jedit.autocomplete,WordList");
        /*
        //$JUnit-BEGIN$
        suite.addTestSuite(WordListTest.class);
        //$JUnit-END$
         */
        suite.addTest( new WordListTest("testAdd") );
        suite.addTest( new WordListTest("testAddAll") );
        suite.addTest( new WordListTest("testRemove") );
        suite.addTest( new WordListTest("testGetAllWords") );
        suite.addTest( new WordListTest("testGetCompletions") );
        suite.addTest( new WordListTest("testClear") );
        
        return suite;
    }
}
