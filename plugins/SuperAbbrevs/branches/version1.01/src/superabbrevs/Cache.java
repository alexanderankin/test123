package superabbrevs;


public interface Cache<K, V> {
	/**
	 * Adds a new value to the cache under the given key.
	 * @param key The key for the value that will be added.
	 * @param value The value to be added.
	 */
	void put(K key, V value);
	
    /**
     * Invalidates the given key.
     * @param key the key to be invalidated.
     */
    void invalidate(K key);
    
    /**
     * Retrieved the value from the cache with the given key.
     * If no value with the given key exists null will be returned.
     * @param key the key for the value to be retrieved.
     * @return the value for the given key.
     */
    V get(K key);
}
