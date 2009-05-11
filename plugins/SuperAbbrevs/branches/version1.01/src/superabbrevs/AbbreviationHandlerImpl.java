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
import superabbrevs.model.Mode;
import superabbrevs.repository.ModeRepository;
import trie.BackwardsTrie;
import trie.Trie;

import com.google.inject.Inject;

/**
 *
 * @author Sune Simonsen
 */
public class AbbreviationHandlerImpl implements AbbreviationHandler {
	private final ModeRepository modeRepository;
	
	@Inject
	public AbbreviationHandlerImpl(ModeRepository modeRepository) {
		this.modeRepository = modeRepository;
	}
    
    private static Cache<String,Trie<Abbreviation>> cache = 
            new Cache<String,Trie<Abbreviation>>(10);

    public LinkedList<Abbreviation> getAbbrevs(String modeName, String text) {
        Trie<Abbreviation> trie = cache.get(modeName);
        if (trie == null) {
            // Load the abbreviation from disc
            Mode mode = modeRepository.load(modeName);
            trie = new BackwardsTrie<Abbreviation>();
            for(Abbreviation abbrev : mode.getAbbreviations()) {
                trie.put(abbrev.getAbbreviationText(), abbrev);
            }
            cache.put(modeName, trie);
        }
        
        LinkedList<Abbreviation> expansions = trie.scan(text); 
        
        return expansions;
    }
    
    public void invalidateMode(String mode) {
        cache.invalidate(mode);
    }

    public Set<Abbreviation> getAbbrevs(String modeName) {
        Mode mode = modeRepository.load(modeName);
        return mode.getAbbreviations();
    }
}
