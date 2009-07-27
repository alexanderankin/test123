/*
 * Trie.java
 *
 * Created on 13. juni 2007, 18:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package trie;

public interface Trie<T> {
    public Match<T> scan(String text);
    public void put(String key, T element);
}
