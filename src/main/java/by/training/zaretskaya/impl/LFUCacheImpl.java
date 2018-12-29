package by.training.zaretskaya.impl;

import by.training.zaretskaya.interfaces.ICache;

import java.util.*;

/**
 * Class implements cache with LRU(least recently used) algorithm.
 * */
public class LFUCacheImpl implements ICache {
    private Map<Object,Object> cacheMap;
    private Map<Object,Integer> mapFrequencies;
    private static final int FREQUENCY_IF_THE_FIRST_CALL = 1;
    private int sizeMax;

    public LFUCacheImpl(int size) {
        cacheMap = new HashMap<>(size);
        mapFrequencies = new HashMap<>(size);
        sizeMax = size;
    }

    //The method provides constant-time performance O(1)
    public int size() {
        return cacheMap.size();
    }

    //The method provides linear performance O(n)
    // since method invalidate() has O(n) and method map.put(key, value) has O(1).
    public Object put(Object key, Object value) {
        if (size()==sizeMax)
            invalidate();
        mapFrequencies.put(key,FREQUENCY_IF_THE_FIRST_CALL);
        return cacheMap.put(key, value);
    }

    //The method provides constant-time performance O(1)
    public boolean contains(Object key) {
        return cacheMap.containsKey(key);
    }

    //The method provides constant-time performance O(1)
    // since methods included in it have performance О(1)(methods contains(key),map.put(key,value), map.get(key)).
    public Object get(Object key) {
        if (contains(key)){
            Integer frequency = mapFrequencies.get(key);
            frequency++;
            mapFrequencies.put(key,frequency);
        return cacheMap.get(key);}
        else throw new IllegalArgumentException(); //Here we must go to Data base
    }

    //The method provides constant-time performance O(n)
    // since method Collections.min depends on size of collection and has linear performance О(n)
    // and method map.remove(key) has O(1)).
    public Object invalidate() {
        Map.Entry<Object, Integer> keyWithMinFrequency = Collections.min(mapFrequencies.entrySet(),
                Comparator.comparing(Map.Entry::getValue));
        mapFrequencies.remove(keyWithMinFrequency.getKey());
        return cacheMap.remove(keyWithMinFrequency.getKey());
    }

    //The method provides performance from O(1) to O(n) depending on the running method.
    public Object putIfAbsent(Object key, Object value) {
        if (!cacheMap.containsKey(key))
            return put(key, value);
        else
            return get(key);
    }
}
