/*
 * Cache.java
 *
 * Created on 16. juni 2007, 01:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs;

import java.util.LinkedHashMap;
import java.util.Set;


public class HashMapCache<K,V> implements Cache<K,V> {
    
    private int maxSize;
    private LinkedHashMap<K,V> data;
    
    public HashMapCache(int size) throws IllegalArgumentException {
        if(size == 0) {
            throw new IllegalArgumentException("The size of the cache much be greater than zero.");
        }
        
        this.maxSize = size;
        data = new LinkedHashMap<K,V>(size);
    }
    
    public void put(K key, V value) {
        if(data.size() == maxSize) {
            // Too many objects in the cache to insert the new element, 
            // we must delete an element.
            Set<K> keys = data.keySet();
            K oldKey = keys.iterator().next();
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
