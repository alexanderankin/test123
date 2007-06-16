/*
 * AbbrevsHandler.java
 *
 * Created on 16. juni 2007, 00:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import trie.BackwardsTrie;
import trie.Trie;

/**
 *
 * @author Sune Simonsen
 */
public class AbbrevsHandler {
    
    private static Cache<String,Trie<Abbrev>> cache = 
            new Cache<String,Trie<Abbrev>>(10);

    public LinkedList<Abbrev> getAbbrevs(String mode, String text) {
        Trie<Abbrev> trie = cache.get(mode);
        if (trie == null) {
            // Load the abbreviation from disc
            ArrayList<Abbrev> abbrevs = Persistence.loadAbbrevs(mode);
            trie = new BackwardsTrie<Abbrev>();
            for(Abbrev abbrev : abbrevs) {
                trie.put(abbrev.abbrev, abbrev);
            }
            cache.put(mode, trie);
        }
        
        LinkedList<Abbrev> expansions = trie.scan(text); 
        Collections.sort(expansions);
        
        return expansions;
    }
}
