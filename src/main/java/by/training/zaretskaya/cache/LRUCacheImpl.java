package by.training.zaretskaya.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Class implements cache with LRU(least recently used) algorithm.
 */
public class LRUCacheImpl implements ICache {

    private Map<Object, Object> cacheMap;
    private LinkedList<Object> listRecentKeys;
    private int sizeMax;

    public LRUCacheImpl(int size) {
        this.cacheMap = new HashMap<>(size);
        listRecentKeys = new LinkedList<>();
        sizeMax = size;
    }

    //The method provides constant-time performance O(1)
    public synchronized int size() {
        return cacheMap.size();
    }

    //The method provides constant-time performance O(1)
    // since methods included in it have performance О(1)(methods invalidate(),
    // list.add(key) and map.put(key, value).
    public synchronized Object put(Object key, Object value) {
        if (!contains(key)) {
            if (size() == sizeMax) {
                invalidate();
            }
            listRecentKeys.add(key);
        }
        return cacheMap.put(key, value);
    }

    //The method provides constant-time performance O(1)
    public synchronized boolean contains(Object key) {
        return cacheMap.containsKey(key);
    }

    //The method provides constant-time performance O(1)
    // since methods included in it have performance О(1)(methods list.removeFirst(), map.remove(key)).
    public synchronized Object invalidate() {
        Object keyRemoved = listRecentKeys.removeFirst();
        return cacheMap.remove(keyRemoved);
    }

    //The method provides linear performance O(n)
    // since methods included in it have performance О(1)(methods list.add(key) and map.get(key) have O(1),
    // but list.remove(object) have O(n)(we search by value not by id)).
    public synchronized Object get(Object key) {
        if (contains(key)) {
            listRecentKeys.remove(key);
            listRecentKeys.add(key);
            return cacheMap.get(key);
        } else {
            throw new IllegalArgumentException(); //Here we must go to Data base
        }
    }

    //The method provides performance from O(1) to O(n) depending on the running redirect.
    public synchronized Object putIfAbsent(Object key, Object value) {
        if (!contains(key)) {
            return put(key, value);
        } else {
            return get(key);
        }
    }

    @Override
    public synchronized Object remove(Object key) {
        listRecentKeys.remove(key);
        return cacheMap.remove(key);
    }
}
