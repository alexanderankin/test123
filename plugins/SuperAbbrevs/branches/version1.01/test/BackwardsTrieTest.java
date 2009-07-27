/*
 * SimpleTrieTest.java
 * JUnit based test
 *
 * Created on 14. juni 2007, 21:19
 */

import junit.framework.*;

import java.util.Collection;
import java.util.LinkedList;
import trie.*;

/**
 *
 * @author Sune Simonsen
 */
public class BackwardsTrieTest extends TestCase {
    
    private Trie<Integer> trie;
    
    public BackwardsTrieTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        trie = new BackwardsTrie<Integer>();
        trie.put("abaab",1);
        trie.put("abba",2);
        trie.put("abab",3);
        trie.put("aba",4);
        trie.put("abaa",5);
        trie.put("aba",6);
        trie.put("baa",6);
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of scan method, of class trie.SimpleTrie.
     */
    public void testScan() {
        String key = "aba";
        
        Collection<Integer> expResult = new LinkedList<Integer>();
        expResult.add(4);
        expResult.add(6);
        Match<Integer> result = trie.scan(key);
        assertEquals(expResult, result.getElements());
        assertEquals(key, result.getMatchingText());
    }
    
    public void testScan1() {
        String key = "aabaa";
        
        Collection<Integer> expResult = new LinkedList<Integer>();
        expResult.add(5);
        Collection<Integer> result = trie.scan(key).getElements();
        assertEquals(expResult, result);
    }
    
    public void testScan2() {
        String key = "ab";
        
        Collection<Integer> expResult = new LinkedList<Integer>();
        Collection<Integer> result = trie.scan(key).getElements();
        assertEquals(expResult, result);
    }
    
    public void testPartialMatch() {
       String key = "abb";
        
        Collection<Integer> expResult = new LinkedList<Integer>();
        Collection<Integer> result = trie.scan(key).getElements();
        assertEquals(expResult, result);
    }
}
