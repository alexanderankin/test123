/*
 * Cache.java
 *
 * Created on 16. juni 2007, 01:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs;

import java.lang.IllegalArgumentException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

/**
 *
 * @author Sune Simonsen
 */
public class Cache<K,V> {
    
    private int maxSize;
    private int size;
    private Hashtable<K,V> data;
    
    public Cache(int size) throws IllegalArgumentException {
        if(size == 0) {
            throw new IllegalArgumentException("The size of the cache much be greater than zero.");
        }
        
        this.maxSize = size;
        data = new Hashtable<K,V>(size);
    }
    
    public void put(K key, V value) {
        if(size == maxSize) {
            // Too many objects in the cache to insert the new element, 
            // we must delete an element.
            Enumeration<K> keys = data.keys();
            K oldKey = keys.nextElement();
            data.remove(oldKey);
        }
        
        data.put(key,value);
    }
    
    public void invalidate(K key) {
        data.remove(key);
    }
    
    public V get(K key) {
        return data.get(key);
    }
}
