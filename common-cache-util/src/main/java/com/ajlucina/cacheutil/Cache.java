package com.ajlucina.cacheutil;

public interface Cache <K,V>{
    default CacheType cacheType(){
        return CacheType.InMemory;
    }
    default CacheName cacheName() {
        return DefaultCacheName.DEFAULT;
    }

    V get (K key);
    void put (K key, V value);
    void remove (K key);
}
