package by.training.zaretskaya.cache;

import by.training.zaretskaya.exception.CollectionWrongParameters;
import by.training.zaretskaya.models.Document;

import java.lang.reflect.Constructor;

public class FactoryCache {
    public static ICache<String, Document> createCache(String algorithmCache, int cacheLimit) {
        TypeCache typeCache = TypeCache.valueOf(algorithmCache);
        typeCache.setCache(cacheLimit);
        return typeCache.getCache();
    }

    public enum TypeCache {
        LRU(new LRUCacheImpl(0)), LFU(new LFUCacheImpl(0));

        private ICache<String, Document> cache;

        TypeCache(ICache cache) {
            this.cache = cache;
        }

        public ICache<String, Document> getCache() {
            return cache;
        }

        public void setCache(int cacheLimit) {
            Class<? extends ICache> cacheClass = cache.getClass();
            Constructor<? extends ICache> constructor = null;
            try {
                constructor = cacheClass.getConstructor(int.class);
                ICache iCache = constructor.newInstance(cacheLimit);
                this.cache = iCache;
            } catch (Exception e) {
                throw new CollectionWrongParameters(e.getMessage());
            }
        }
    }
}
