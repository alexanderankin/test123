
/*
Copyright (C) 2008 Matthew Gilbert

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package voxspellcheck;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;


public class SuggestionTree
{
    protected SortedMap<String, TreeSet<String>> map;
    protected WordTrie word_trie;
    protected OffsetTrie offset_trie;
    
    public SuggestionTree(OffsetTrie offset_trie_)
    {
        map = new TreeMap<String, TreeSet<String>>();
        offset_trie = offset_trie_;
        
        ArrayList<String> words = offset_trie.getWords();
        for (String s : words) {
            String meta = new DoubleMetaphone().encode(s);
            if (map.get(meta) == null) {
                map.put(meta, new TreeSet<String>());
            }
            map.get(meta).add(s);
        }
        
        word_trie = new WordTrie();
    }
    
    public void addWord(String word)
    {
        if (word.length() > 0) {
            String meta = new DoubleMetaphone().encode(word);
            if (map.get(meta) == null) {
                map.put(meta, new TreeSet<String>());
            }
            TreeSet<String> words = map.get(meta);
            words.add(word);
            word_trie.addWord(word);
        }
    }
    
    /* Find valid words that can be formed from a simple character swap. */
    public TreeSet<String> getPermutations(String word)
    {
        TreeSet<String> permutations = new TreeSet<String>();
        int length = word.length();
        
        for (int i = 0; i < length - 1; ++i) {
            char[] chars = new char[length];
            for (int j = 0; j < length; ++j) {
                if (j == i)
                    chars[j] = word.charAt(j + 1);
                else if (j == (i + 1))
                    chars[j] = word.charAt(j - 1);
                else
                    chars[j] = word.charAt(j);
            }
            String new_word = new String(chars);
            if (offset_trie.find(new_word) || word_trie.find(new_word)) {
                permutations.add(new_word);
            }
        }
        return permutations;
    }
    
    public TreeSet<String> getSoundalike(String word)
    {
        DoubleMetaphone dm = new DoubleMetaphone();
        String meta = dm.encode(word);
        TreeSet<String> suggestions = map.get(meta);
        if (suggestions == null)
            suggestions = new TreeSet<String>();
        String higher = meta;
        String lower = meta;
        int num_tries = 0;
        // TODO: What's appropriate here?
        //while (suggestions.size() < 1000) {
        while (num_tries < 50) {
            //higher = (higher != null) ? map.higherKey(higher) : null;
            if (higher != null) {
                SortedMap<String, TreeSet<String>> tail = map.tailMap(higher);
                Object[] keys = tail.keySet().toArray();
                if (keys.length > 0) {
                    // If higher isn't in the tail, then the first
                    // result is what we want (i.e. meta is not in
                    // map, so the next higher or equal
                    // is a new value). Otherwise, take the next
                    // higher key.
                    if (!higher.equals((String)keys[0])) {
                        higher = (String)keys[0];
                    } else if (keys.length > 1) {
                        higher = (String)keys[1];
                    } else {
                        higher = null;
                    }
                } else {
                    higher = null;
                }
            }
            if (higher != null) {
                TreeSet<String> add = map.get(higher);
                if (add != null) 
                    suggestions.addAll(add);
            }
            //lower = (lower != null) ? map.lowerKey(lower) : null;
            if (lower != null) {
                // headMap does not include lower, so don't need to do
                // the filtering like for tailMap.
                SortedMap<String, TreeSet<String>> head = map.headMap(lower);
                lower = null;
                try {
                    lower = head.lastKey();
                } catch (java.util.NoSuchElementException ex) {     // NOPMD
                    // don't care
                }
            }
            if (higher != null) {
                TreeSet<String> add = map.get(higher);
                if (add != null)
                    suggestions.addAll(add);
            }
            if ((lower == null) && (higher == null))
                break;
            ++num_tries;
        }
        return suggestions;
    }
    
    public ArrayList<String> getStartsWith(String word)
    {
        ArrayList<String> vec = new ArrayList<String>();
        vec.addAll(offset_trie.getWords(word));
        vec.addAll(word_trie.getWords(word));
        return vec;
    }
        
    public ArrayList<String> getSuggestions(String word)
    {
        if (word == null || word.isEmpty())
            return null;
        
        word = word.trim();
        
        TreeSet<String> permutations = getPermutations(word);
        TreeSet<String> suggestions = new TreeSet<String>();
        permutations.add(word);
        for (String s : permutations)
            suggestions.addAll(getSoundalike(s));
        suggestions.addAll(getStartsWith(word));
        
        final String input = word;
        
        Comparator<String> c = new Comparator<String>() {
            
            public String s;
            
            public int compare(String s1, String s2) {
                int d1 = LevenshteinDistance.LD(input, s1);
                int d2 = LevenshteinDistance.LD(input, s2);
                return d1 - d2;
            }
            
            public boolean equals(Object obj) {
                return false;
            }
        };
        
        /* Sort the words according the levenshtein distance using the
           above comparator. The suggestion tree can have the same word
           appear twice, so make a unique list. */
        ArrayList<String> vec_suggestions = new ArrayList<String>(suggestions);
        Collections.sort(vec_suggestions, c);
        HashSet<String> unique_db = new HashSet<String>();
        ArrayList<String> unique = new ArrayList<String>();
        for (String s : vec_suggestions) {
            if (unique_db.contains(s))
                continue;
            unique.add(s);
        }
        
        // Cut it down to the first 100
        unique = new ArrayList<String>(unique.subList(0, 100));
        
        return unique;
    }
}
