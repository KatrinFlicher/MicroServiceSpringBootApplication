package by.training.zaretskaya.cache;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Class implements cache with LRU(least recently used) algorithm.
 */
public class LFUCacheImpl implements ICache {
    private static final int INITIAL_FREQUENCY = 0;
    private Map<Object, Object> cacheMap;
    private Map<Object, Integer> mapFrequencies;
    private int sizeMax;

    public LFUCacheImpl(int size) {
        cacheMap = new HashMap<>(size);
        mapFrequencies = new HashMap<>(size);
        sizeMax = size;
    }

    //The method provides constant-time performance O(1)
    public synchronized int size() {
        return cacheMap.size();
    }

    //The method provides linear performance O(n)
    // since method invalidate() has O(n) and method map.put(key, value) has O(1).
    public synchronized Object put(Object key, Object value) {
        if (!contains(key)) {
            if (size() == sizeMax) {
                invalidate();
            }
            mapFrequencies.put(key, INITIAL_FREQUENCY);
        }
        return cacheMap.put(key, value);
    }

    //The method provides constant-time performance O(1)
    public synchronized boolean contains(Object key) {
        return cacheMap.containsKey(key);
    }

    //The method provides constant-time performance O(1)
    // since methods included in it have performance О(1)(methods contains(key),map.put(key,value), map.get(key)).
    public synchronized Object get(Object key) {
        if (contains(key)) {
            Integer frequency = mapFrequencies.get(key);
            frequency++;
            mapFrequencies.put(key, frequency);
            return cacheMap.get(key);
        } else {
            throw new IllegalArgumentException(); //Here we must go to Data base
        }
    }

    //The method provides linear performance O(n)
    // since method Collections.min depends on size of collection and has linear performance О(n)
    // and method map.remove(key) has O(1)).
    public synchronized Object invalidate() {
        Map.Entry<Object, Integer> keyWithMinFrequency = Collections.min(mapFrequencies.entrySet(),
                Comparator.comparing(Map.Entry::getValue));
        mapFrequencies.remove(keyWithMinFrequency.getKey());
        return cacheMap.remove(keyWithMinFrequency.getKey());
    }

    //The method provides performance from O(1) to O(n) depending on the running method.
    public synchronized Object putIfAbsent(Object key, Object value) {
        if (!contains(key)) {
            return put(key, value);
        } else {
            return get(key);
        }
    }

    @Override
    public synchronized Object remove(Object key) {
        mapFrequencies.remove(key);
        return cacheMap.remove(key);
    }
}
