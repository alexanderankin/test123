/*
 * AbbrevsHandler.java
 *
 * Created on 16. juni 2007, 00:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs;

import java.util.LinkedList;
import java.util.Set;

import superabbrevs.model.Abbreviation;
import superabbrevs.model.AbbreviationTrie;
import superabbrevs.model.Mode;
import superabbrevs.repository.AbbreviationTrieRepository;
import superabbrevs.repository.ModeRepository;
import trie.Match;

import com.google.inject.Inject;

public class AbbreviationHandlerImpl implements AbbreviationHandler {
	
	private final ModeRepository modeRepository;
	private final AbbreviationTrieRepository abbreviationTrieRepository;

	@Inject 
	public AbbreviationHandlerImpl(ModeRepository modeRepository, AbbreviationTrieRepository abbreviationTrieRepository) {
		this.modeRepository = modeRepository;
		this.abbreviationTrieRepository = abbreviationTrieRepository;
	}

    public Match<Abbreviation> getAbbrevs(String modeName, String text) {
    	AbbreviationTrie trie = abbreviationTrieRepository.load(modeName);        
        Match<Abbreviation> match = trie.scan(text);
        return match;
    }

    public Set<Abbreviation> getAbbrevs(String modeName) {
        Mode mode = modeRepository.load(modeName);
        return mode.getAbbreviations();
    }
}
