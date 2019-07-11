package by.training.zaretskaya.cache;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LRUCacheImplTest extends AbstractCacheTest {

    @Before
    public void createCache() {
        cache = new LRUCacheImpl(2);
    }

    @Test
    public void testRemoveValueFromCache() {
        putOneValueInCache(1);
        putOneValueInCache(2);
        assertEquals("value1", cache.invalidate());
    }

    @Test
    public void testAutoRemoveLeastRecentlyUsedKey() {
        putOneValueInCache(1);
        putOneValueInCache(2);
        cache.get("key1");
        putOneValueInCache(3);
        assertFalse(cache.contains("key2"));
    }
}
