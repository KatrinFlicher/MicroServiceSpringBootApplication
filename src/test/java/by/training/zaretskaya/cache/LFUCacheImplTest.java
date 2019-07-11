package by.training.zaretskaya.cache;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LFUCacheImplTest extends AbstractCacheTest {

    @Before
    public void createCache() {
        cache = new LFUCacheImpl(2);
    }

    @Test
    public void testRemoveValueFromCache() {
        putOneValueInCache(1);
        putOneValueInCache(2);
        cache.get("key1");
        assertEquals("value2", cache.invalidate());
    }

    @Test
    public void testAutoRemoveLeastFrequentlyUsedKey() {
        putOneValueInCache(1);
        putOneValueInCache(2);
        cache.get("key2");
        putOneValueInCache(3);
        assertFalse(cache.contains("key1"));
    }
}
