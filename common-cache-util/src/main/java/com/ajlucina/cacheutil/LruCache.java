package com.ajlucina.cacheutil;

import java.util.concurrent.ConcurrentHashMap;

public abstract class LruCache<K, V> implements Cache<K, V> {

    private final ConcurrentHashMap<K, Node> cache = new ConcurrentHashMap<>();
    private final int capacity;

    private final Node head;
    private final Node tail;
    private final Node node;

    class Node {
        K key;
        V value;
        Node prev;
        Node next;

        Node() {}
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public LruCache(int capacity) {
        this.capacity = capacity;

        this.node = new Node();
        head = node;
        tail = node;

        head.next = tail;
        tail.prev = head;
    }

    @Override
    public V get(K key) {
        Node node = cache.get(key);

        if (node == null) {
            return null;
        }

        moveToHead(node);
        return node.value;
    }

    @Override
    public void put(K key, V value) {
        Node node = cache.get(key);

        if (node == null) {
            node = new Node(key, value);
            cache.put(key, node);
            addToHead(node);

            if (cache.size() > capacity) {
                Node lru = removeTail();
                cache.remove(lru.key);
            }
        } else {
            node.value = value;
            moveToHead(node);
        }
    }

    @Override
    public void remove(K key) {
        Node node = cache.get(key);
        if (node == null) {
            return;
        }

        removeNode(node);
        cache.remove(key);
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;

        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        prev.next = next;
        next.prev = prev;
    }

    private Node removeTail() {
        Node lru = tail.prev;
        removeNode(lru);
        return lru;
    }
}