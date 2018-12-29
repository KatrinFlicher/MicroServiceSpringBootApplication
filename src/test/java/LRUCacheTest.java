import by.training.zaretskaya.interfaces.ICache;
import by.training.zaretskaya.impl.LRUCacheImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LRUCacheTest {
    ICache cache;

    @Before
    public void createCache(){
        cache = new LRUCacheImpl(2);
    }

    @Test
    public void testHowMuchElementsHaveBeenInCache(){
        Assert.assertEquals(0,cache.size());
    }

    @Test
    public void testPutOneValueIfCacheHaveEnoughSpace(){
        putOneValueInCache(1);
        Assert.assertEquals(1,cache.size());
    }

    @Test
    public void testCacheContainsKey(){
        putOneValueInCache(1);
        Assert.assertTrue(cache.contains("key1"));
    }

    @Test
    public void testGetExistentValueFromCache(){
        putOneValueInCache(1);
        Assert.assertEquals("value1", cache.get("key1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNonexistentValueFromCache(){
         cache.get("key");
    }

    @Test
    public void testRemoveValueFromCache(){
        putOneValueInCache(1);
        putOneValueInCache(2);
        Assert.assertEquals("value1",  cache.invalidate());
    }

    @Test
    public void testPutOneValueIfCacheHaveNotEnoughSpace(){
        putOneValueInCache(1);
        putOneValueInCache(2);
        putOneValueInCache(3);
        Assert.assertEquals(2, cache.size());
        Assert.assertEquals("value3",cache.get("key3"));

    }

    @Test
    public void testAutoRemoveLeastRecentlyUsedKey(){
        putOneValueInCache(1);
        putOneValueInCache(2);
        cache.get("key1");
        putOneValueInCache(3);
        Assert.assertFalse(cache.contains("key2"));
    }

    @Test
    public void testPutIfAbsent(){
        Assert.assertFalse(cache.contains("key"));
        cache.putIfAbsent("key", "value");
        Assert.assertTrue(cache.contains("key"));
        cache.putIfAbsent("key", "value12");
        Assert.assertNotEquals("value12", cache.get("key"));
    }

    private void putOneValueInCache(int index){
        cache.put("key"+index, "value"+index);
    }

}
