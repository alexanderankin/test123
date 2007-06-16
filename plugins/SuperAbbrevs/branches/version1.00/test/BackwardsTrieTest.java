/*
 * SimpleTrieTest.java
 * JUnit based test
 *
 * Created on 14. juni 2007, 21:19
 */

import junit.framework.*;
import java.util.ArrayList;
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
        System.out.println("scan");
        
        String key = "aba";
        
        LinkedList<Integer> expResult = new LinkedList<Integer>();
        expResult.add(4);
        expResult.add(6);
        LinkedList<Integer> result = trie.scan(key);
        assertEquals(expResult, result);
    }
    
    public void testScan1() {
        System.out.println("scan1");
        
        String key = "aabaa";
        
        LinkedList<Integer> expResult = new LinkedList<Integer>();
        expResult.add(5);
        LinkedList<Integer> result = trie.scan(key);
        assertEquals(expResult, result);
    }
    
    public void testScan2() {
        System.out.println("scan2");
        
        String key = "ab";
        
        LinkedList<Integer> expResult = new LinkedList<Integer>();
        LinkedList<Integer> result = trie.scan(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of remove method, of class trie.SimpleTrie.
     */
    public void testRemove() {
        System.out.println("remove");
        
        String key = "aba";
                
        boolean expResult = true;
        boolean result = trie.remove(key, 4);
        assertTrue(result);
    }
    
}
