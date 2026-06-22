package com.ajlucina.cacheutil;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class RedisCache<K, V> implements Cache<K, V> {

    private final RedissonClient client;
    private final CacheName cacheName;

    private final long ttlMinutes;
    private final Function<K, String> keySerializer;

    public RedisCache(RedissonClient client,
                      CacheName cacheName,
                      long ttlMinutes,
                      Function<K, String> keySerializer) {

        this.client = client;
        this.cacheName = cacheName;
        this.ttlMinutes = ttlMinutes;
        this.keySerializer = keySerializer;
    }

    @Override
    public CacheType cacheType() {
        return CacheType.Redis;
    }

    @Override
    public V get(K key) {
        return map().get(key(key));
    }

    @Override
    public void put(K key, V value) {
        map().put(key(key), value, ttlMinutes, TimeUnit.MINUTES );
    }

    @Override
    public void remove(K key) {
        map().fastRemove(key(key));
    }

    private RMapCache<String, V> map() {
        return client.getMapCache(bucket());
    }

    private String bucket() {
        return cacheName.getClass().getSimpleName() + "." + cacheName.name();
    }

    private String key(K key) {
        return bucket() + ":" + keySerializer.apply(key);
    }
}