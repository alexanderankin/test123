/*
 * Created on 9.2.2005
 * $id
 */
package net.jakubholy.jedit.autocomplete;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * 
 */
public class WordListTest extends TestCase {

    public WordListTest(String arg0) {
        super(arg0);
    }
    WordList wordList;
    Completion[] completions;
    final String prefix = "prefix";
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        wordList = new WordListTreeSet();
        completions = new Completion[]{
          new Completion("pref"),
          new Completion("prefaBefore"),
          new Completion("prefix"),
          new Completion("prefix01"),
          new Completion("prefix02"),
          new Completion("prefiyAfter")
        };
    }
    
    /*
    public static junit.framework.Test suite() {

        junit.framework.TestSuite suite = new junit.framework.TestSuite(WordListTest.class);
        
        return suite;
    }
    */

    public void testGetCompletions() {
        wordList.addAll(completions);
        
        Completion[] words = wordList.getCompletions(prefix);
        assertTrue("The nr of completions should be 3 (prefix," +
                "prefix01, prefix02)",words.length == 3);
        for (int i = 0; i < words.length; i++) {
            assertEquals(words[i], completions[i+2]);
        }
    }

    public void testGetAllWords() {
        wordList.addAll(completions);
        
        Completion[] words = wordList.getAllWords();
        for (int i = 0; i < words.length; i++) {
            assertTrue("Get all w. shall insert all the elements we " +
                    "inserted with addAll & in the same order as the added " +
                    "array was ordered", words[i].equals(completions[i]));
        }
    }

    public void testAdd() {
        //wordList.add(null);
        wordList.add(completions[0]);
        assertEquals(completions[0], 
                wordList.getCompletions(completions[0].getWord())[0] );
    }

    // testAdd first!
    public void testRemove() {
        wordList.add(completions[0]);
        
        assertTrue("Removal of just inserted element shall return true.", 
                wordList.remove(completions[0]) );
        
        Completion[] ca = wordList.getCompletions(completions[0].getWord()); 
        
        assertTrue("Removed element shouldn't be there anymore ", 
                ca.length == 0 || ! ca[0].equals(completions[0]));
    }

    public void testClear() {
        wordList.addAll(completions);
        wordList.clear();
        assertTrue( wordList.size() == 0 );
    }
    
    public void testAddAll() {
        wordList.addAll(completions);
        for (int i = 0; i < completions.length; i++) {
            assertTrue("The list should contain all completions inserted ", 
                    wordList.containes( completions[i] ) );
        }
    }

}
