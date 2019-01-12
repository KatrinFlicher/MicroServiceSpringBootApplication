package by.training.zaretskaya.impl;

import by.training.zaretskaya.interfaces.ICache;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractCacheTest {
    ICache cache;

    @Test
    public void testCountElementsInCache() {
        assertEquals(0, cache.size());
    }

    @Test
    public void testPutOneValueIfCacheHaveEnoughSpace() {
        putOneValueInCache(1);
        assertEquals(1, cache.size());
    }

    @Test
    public void testCacheContainsKey() {
        putOneValueInCache(1);
        assertTrue(cache.contains("key1"));
    }

    @Test
    public void testGetExistentValueFromCache() {
        putOneValueInCache(1);
        assertEquals("value1", cache.get("key1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNonexistentValueFromCache() {
        cache.get("key");
    }

    @Test
    public void testPutOneValueIfCacheHaveNotEnoughSpace() {
        putOneValueInCache(1);
        putOneValueInCache(2);
        putOneValueInCache(3);
        assertEquals(2, cache.size());
        assertEquals("value3", cache.get("key3"));
    }

    @Test
    public void testPutIfKeyContainsInCache() {
        putOneValueInCache(1);
        Object oldValue = cache.put("key1", "value2");
        assertEquals("value1", oldValue);
    }

    @Test
    public void testPutIfAbsent() {
        assertFalse(cache.contains("key"));
        cache.putIfAbsent("key", "value");
        assertTrue(cache.contains("key"));
        cache.putIfAbsent("key", "value12");
        assertNotEquals("value12", cache.get("key"));
    }

    protected void putOneValueInCache(int index) {
        cache.put("key" + index, "value" + index);
    }
}
