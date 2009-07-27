package superabbrevs.model;

import trie.BackwardsTrie;
import trie.Match;
import trie.Trie;

public class AbbreviationTrie implements Trie<Abbreviation> {
	private final static int MINIMUM_PREFIX_LENGTH = 2;
	
	private BackwardsTrie<Abbreviation> fullTrie = new BackwardsTrie<Abbreviation>();
	private BackwardsTrie<Abbreviation> prefixTrie = new BackwardsTrie<Abbreviation>();
	
	public void put(String key, Abbreviation element) {
		fullTrie.put(key, element);
		
		for (int prefixLength = MINIMUM_PREFIX_LENGTH; prefixLength < key.length(); prefixLength++) {
			prefixTrie.put(key.substring(0, prefixLength), element);
		}
	}
	
	public Match<Abbreviation> scan(String text) {
		Match<Abbreviation> match = fullTrie.scan(text);
		if (!match.isEmpty()) {
			return match;
		}
		
		return prefixTrie.scan(text);
	}
}
