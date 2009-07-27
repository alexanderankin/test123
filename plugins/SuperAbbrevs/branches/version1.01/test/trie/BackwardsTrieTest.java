package trie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;


public class BackwardsTrieTest {
	private Trie<Integer> trie;

	@Before
	public void setUp() throws Exception {
		trie = new BackwardsTrie<Integer>();
		trie.put("abaab", 1);
		trie.put("abba", 2);
		trie.put("abab", 3);
		trie.put("aba", 4);
		trie.put("abaa", 5);
		trie.put("aba", 6);
		trie.put("baa", 6);
	}

	@Test
	public void testScan() throws Exception {
		Collection<Integer> expResult = new LinkedList<Integer>();
		expResult.add(4);
		expResult.add(6);
		Collection<Integer> result = trie.scan("aba").getElements();
		assertEquals(expResult, result);
	}

	@Test
	public void testScan1() throws Exception {
		Collection<Integer> expResult = new LinkedList<Integer>();
		expResult.add(5);
		Collection<Integer> result = trie.scan("aabaa").getElements();
		assertEquals(expResult, result);
	}

	@Test
	public void testScan2() throws Exception {
		Collection<Integer> expResult = new LinkedList<Integer>();
		Collection<Integer> result = trie.scan("ab").getElements();
		assertEquals(expResult, result);
	}
}
