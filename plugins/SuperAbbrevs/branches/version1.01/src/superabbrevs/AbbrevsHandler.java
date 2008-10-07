/*
 * AbbrevsHandler.java
 *
 * Created on 16. juni 2007, 00:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs;

import superabbrevs.model.Abbrev;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import superabbrevs.model.Mode;
import trie.BackwardsTrie;
import trie.Trie;

/**
 *
 * @author Sune Simonsen
 */
public class AbbrevsHandler {
    
    private static Cache<String,Trie<Abbrev>> cache = 
            new Cache<String,Trie<Abbrev>>(10);

    public LinkedList<Abbrev> getAbbrevs(String modeName, String text) {
        Trie<Abbrev> trie = cache.get(modeName);
        if (trie == null) {
            // Load the abbreviation from disc
            Mode mode = Persistence.loadMode(modeName);
            trie = new BackwardsTrie<Abbrev>();
            for(Abbrev abbrev : mode.getAbbreviations()) {
                trie.put(abbrev.getAbbreviation(), abbrev);
            }
            cache.put(modeName, trie);
        }
        
        LinkedList<Abbrev> expansions = trie.scan(text); 
        
        return expansions;
    }
    
    public static void invalidateMode(String mode) {
        cache.invalidate(mode);
    }

    ArrayList<Abbrev> getAbbrevs(String modeName) {
        Mode mode = Persistence.loadMode(modeName);
        return mode.getAbbreviations();
    }
}
