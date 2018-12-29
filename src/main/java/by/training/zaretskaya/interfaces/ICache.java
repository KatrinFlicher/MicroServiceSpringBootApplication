package by.training.zaretskaya.interfaces;
/**
 * Interface with basic operations with the cache
 *
 * @version 1.0
 *
 * @author Zaretskaya Katsiaryna
 * */
public interface ICache {

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
    Object put(Object key, Object value);

    /**
     * Returns true if this cache contains a mapping for the specified key.
     * @param key - The key whose presence in this cache is to be tested
     * */
    boolean contains(Object key);

    /**
     * Returns the value to which the specified key is mapped, or null if this cache contains no mapping for the key.
     * @param key - the key whose associated value is to be returned
     * */
    Object get(Object key);

    /**
     * * Clear space in this cache for one record due to its overflow.
     * */
    Object invalidate();

    /**
     * If the specified key is not already associated with a value, associate it with the given value.
     * @param key key with which the specified value is to be associated
     * @param value - value to be associated with the specified key
     * */
    Object putIfAbsent(Object key, Object value);
}
