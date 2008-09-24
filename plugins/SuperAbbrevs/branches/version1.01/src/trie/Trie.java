/*
 * Trie.java
 *
 * Created on 13. juni 2007, 18:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package trie;

import java.util.LinkedList;

/**
 *
 * @author Sune Simonsen
 */
public interface Trie<T> {
    public LinkedList<T> scan(String text);
    public void put(String key, T element);
    public boolean remove(String key, T element);
}
