package com.thevoxelbox.voyage.utils;

public class Tuple2<K,V> {
    K k;
    V v;

    public Tuple2(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getKey() {
        return k;
    }

    public V getValue() {
        return v;
    }
}
