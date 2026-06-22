package com.ajlucina.cacheutil;

import java.util.concurrent.ConcurrentHashMap;

public abstract class BasicCache <K,V> implements Cache <K,V>{

    private final ConcurrentHashMap <K,V> map = new ConcurrentHashMap<>();

    @Override
    public V get(K key){
        return map.getOrDefault(key, null);
    }

    @Override
    public void put(K key, V value) {
        map.put(key, value);
    }

    @Override
    public void remove (K key){
        map.remove(key);
    }
}
