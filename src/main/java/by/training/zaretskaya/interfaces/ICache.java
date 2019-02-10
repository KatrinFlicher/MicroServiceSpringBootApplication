package by.training.zaretskaya.interfaces;
/**
 * Interface with basic operations with the cache
 *
 * @version 1.0
 *
 * @author Zaretskaya Katsiaryna
 * */
public interface ICache <K,V> {

    /**
     * Returns the number of key-value mappings in this map.
     * */
    int size();

    /**
     * Associates the specified value with the specified key in this cache. If the cache previously contained a
     * mapping for the key, the old value is replaced.
     * @param key - key with which the specified value is to be associated
     * @param value - value to be associated with the specified key
     * */
    V put(K key, V value);

    /**
     * Returns true if this cache contains a mapping for the specified key.
     * @param key - The key whose presence in this cache is to be tested
     * */
    boolean contains(K key);

    /**
     * Returns the value to which the specified key is mapped, or null if this cache contains no mapping for the key.
     * @param key - the key whose associated value is to be returned
     * */
    V get(K key);

    /**
     * * Clear space in this cache for one record due to its overflow.
     * */
    V invalidate();

    /**
     * If the specified key is not already associated with a value, associate it with the given value.
     * @param key key with which the specified value is to be associated
     * @param value - value to be associated with the specified key
     * */
    V putIfAbsent(K key, V value);

    /**
     * Removes the mapping for a key from this cache if it is present.
     * @param key - key whose mapping is to be removed from the cache
     * */
    V remove(K key);
}
