/*
 * SimpleTrie.java
 *
 * Created on 13. juni 2007, 18:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package trie;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Sune Simonsen
 */
public class BackwardsTrie<T> implements Trie<T> {
    
    private Node<T> root = new Node<T>();

    public LinkedList<T> scan(String text) {
        return scanHelper(root, text, text.length()-1);
    }
    
    private LinkedList<T> scanHelper(Node<T> node, String key, int offset) {
        char c;
        if (0 <= offset && node.hasChild(c = key.charAt(offset))) {
            // We have not scanned the hole text
            return scanHelper(node.getChild(c), key, offset-1);
        } else {
            // Eigther there is no more text to scan or we reached a leaf
            return node.getElements();
        }
    }

    public boolean remove(String key, T element) {
        LinkedList<T> elements = get(key);
        return elements.remove(element);
    }
    
    public LinkedList<T> get(String key) {
        return getHelper(root, key, key.length()-1);
    }
    
    public LinkedList<T> getHelper(Node<T> node, String key, int offset) {
        char c;
        if (0 <= offset && node.hasChild(c = key.charAt(offset))) {
            // We have not scanned the hole key
            return getHelper(node.getChild(c), key, offset-1);
        } else if (offset == -1) {
            // we found the key 
            return node.getElements();
        } else {
            // the key was not found
            return new LinkedList<T>();
        }
    }
    
    public void put(String key, T element) {
        putHelper(root, key, key.length()-1, element);
    }
    
    private void putHelper(Node<T> node, String key, int offset, T element) {
        if (0 <= offset) {
            char c = key.charAt(offset);
            if (node.hasChild(c)) {
                // search down the tree for an insertion point
                putHelper(node.getChild(c), key, offset-1, element);
            } else {
                // insert nodes for the rest of the key
                insertNodes(node, key, offset, element);
            }
        } else {
            // we found the place to insert the element
            node.addElement(element);
        }
    }

    private void insertNodes(Node<T> node, String key, int offset, T element) {
        if (0 <= offset) {
            // Add nodes for the rest of the key text
            Node<T> newNode = new Node<T>();
            node.addChild(key.charAt(offset), newNode);
            insertNodes(newNode, key, offset-1, element);
        } else {
            node.addElement(element);
        }
    }
}
