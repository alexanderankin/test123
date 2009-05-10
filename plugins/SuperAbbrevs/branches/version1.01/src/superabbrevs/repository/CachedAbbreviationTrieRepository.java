package superabbrevs.repository;

import superabbrevs.Cache;
import superabbrevs.model.Abbreviation;
import superabbrevs.model.AbbreviationTrie;
import superabbrevs.model.Mode;

import com.google.inject.Inject;

public class CachedAbbreviationTrieRepository implements AbbreviationTrieRepository, ModeSavedListener {
	private final ModeRepository modeRepository;
	
	@Inject
	public CachedAbbreviationTrieRepository(ModeRepository modeRepository) {
		this.modeRepository = modeRepository;
		modeRepository.addModeSavedListener(this);
	}
    
    private Cache<String,AbbreviationTrie> cache = 
            new Cache<String,AbbreviationTrie>(10);

    public AbbreviationTrie load(String modeName) {
    	AbbreviationTrie trie = cache.get(modeName);
        if (trie == null) {
            // Load the abbreviation from disc
            Mode mode = modeRepository.load(modeName);
            trie = new AbbreviationTrie();
            for(Abbreviation abbrev : mode.getAbbreviations()) {
                trie.put(abbrev.getAbbreviationText(), abbrev);
            }
            cache.put(modeName, trie);
        }
        return trie;
    }

	public void modeWasSaved(Mode mode) {
		cache.invalidate(mode.getName());
	}
}
