package trie;

import java.util.LinkedList;

public class BackwardsTrie<T> implements Trie<T> {
    
	private final Node<T> root = new Node<T>();
	
    public Match<T> scan(String text) {
        return scanRecursive(root, text, text.length()-1);
    }
    
    private Match<T> scanRecursive(Node<T> node, String key, int offset) {
    	if (offset < 0) {
    		// There is no more text to scan
    		return new Match<T>(key, node.getElements());
		} 
    	
    	char c = key.charAt(offset);
    	if (!node.hasChild(c)) {
			// We reached a leaf
    		return new Match<T>(key.substring(offset+1), node.getElements());
		}
    	
    	// We have not scanned the hole text
    	return scanRecursive(node.getChild(c), key, offset-1);
    }
    
    public LinkedList<T> get(String key) {
    	return getRecursive(root, key, key.length()-1);
    }
    
    private LinkedList<T> getRecursive(Node<T> node, String key, int offset) {
        char c;
        if (0 <= offset && node.hasChild(c = key.charAt(offset))) {
            // We have not scanned the hole key
            return getRecursive(node.getChild(c), key, offset-1);
        } else if (offset == -1) {
            // we found the key 
            return node.getElements();
        } else {
            // the key was not found
            return new LinkedList<T>();
        }
    }
    
    public void put(String key, T element) {
    	int offset = key.length() - 1;
		putRecursive(root, key, offset, element);
    }
    
    private void putRecursive(Node<T> node, String key, int offset, T element) {
        if (0 <= offset) {
            char c = key.charAt(offset);
            if (node.hasChild(c)) {
                // search down the tree for an insertion point
                putRecursive(node.getChild(c), key, offset-1, element);
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
