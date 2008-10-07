/*
 * Leaf.java
 *
 * Created on 13. juni 2007, 18:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package trie;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 *
 * @author Sune Simonsen
 */
public class Node<T> {
    
    private LinkedList<T> elements = new LinkedList<T>();
    
    public LinkedList<T> getElements() {
        return elements;
    }
    
    public void addElement(T element) {
        elements.add(element);
    }
    
    public void removeElement(T element) {
        elements.remove(element);
    }
    
    private Hashtable<Character, Node<T>> children = 
            new Hashtable<Character,Node<T>>();
    
    public void addChild(char c, Node<T> child) {
        children.put(c, child);
    }
    
    public Node<T> getChild(char c) {
        return children.get(c);
    }
    
    public Node<T> removeChild(char c) {
        return children.remove(c);
    }
    
    public boolean hasChild(char c) {
        return children.containsKey(c);
    }
    
    public boolean hasChildren() {
        return !children.isEmpty();
    }
}
