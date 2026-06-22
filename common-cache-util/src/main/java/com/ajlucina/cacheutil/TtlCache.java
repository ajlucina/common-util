package com.ajlucina.cacheutil;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TtlCache<K, V> implements Cache<K, V> {

    private final ConcurrentHashMap<K, Entry<V>> cache = new ConcurrentHashMap<>();

    private final long accessTtlTime;
    private final long maxTtlTime;
    private final boolean refreshOnAccess;

    private final long cleanupIntervalTime = 5L * 60L * 1_000_000_000L;

    private final AtomicLong cleanUpTimeElapsed = new AtomicLong();
    private final AtomicBoolean isCleaning = new AtomicBoolean(false);

    private final ExecutorService executor =  Executors.newVirtualThreadPerTaskExecutor();

    public TtlCache(long refreshOnAccessMinutes, long maxTtlMinutes) {
        this.refreshOnAccess = refreshOnAccessMinutes >= 0;
        this.accessTtlTime = refreshOnAccessMinutes * 60L * 1_000_000_000L;
        this.maxTtlTime = maxTtlMinutes * 60L * 1_000_000_000L;

        cleanUpTimeElapsed.set(currentTime() + cleanupIntervalTime);
    }

    private static class Entry<V> {
        final V value;
        final long maxExpiryTime;
        volatile long accessExpiryTime;

        Entry(V value, long accessTtlTime, long maxTtlTime) {
            var now = currentTime();

            this.value = value;
            this.accessExpiryTime = now + accessTtlTime;
            this.maxExpiryTime = now + maxTtlTime;
        }

        boolean isExpired() {
            var now = currentTime();
            return now >= maxExpiryTime || now >= accessExpiryTime;
        }

        boolean isMaxExpired() {
            return currentTime() >= maxExpiryTime;
        }

        void refresh(long accessTtlTime) {
            this.accessExpiryTime = currentTime() + accessTtlTime;
        }
    }

    @Override
    public V get(K key) {

        var entry = cache.get(key);

        if (entry == null || entry.isExpired()) 
            return null;

        if (refreshOnAccess)
            entry.refresh(accessTtlTime);

        runAsyncCleanup();

        return entry.value;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, new Entry<>(value, accessTtlTime, maxTtlTime));
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }


    private void runAsyncCleanup() {

        if (currentTime() < cleanUpTimeElapsed.get() )
            return;

        if (isCleaning.compareAndSet(false, true)) {
            cleanUpTimeElapsed.set(currentTime() + cleanupIntervalTime);

            executor.execute(() -> {
                try {
                    cache.entrySet().removeIf(e -> e.getValue().isMaxExpired());
                } finally {
                    isCleaning.set(false); // Reset when done
                }
            });
        }
    }

    private static long currentTime() {
        return System.nanoTime();
    }
}