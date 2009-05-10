package superabbrevs.repository;

import superabbrevs.model.AbbreviationTrie;

public interface AbbreviationTrieRepository {
	AbbreviationTrie load(String modeName);
}
