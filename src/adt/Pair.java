package adt;

import java.io.Serializable;

/**
 *
 * @author Chok Chun Fai
 */

/**
 * A simple, generic class to hold a key-value pair. This is a helper class used
 * to simulate Map-like behavior in a List.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public class Pair<K extends Comparable<K>, V> implements Comparable<Pair<K, V>>, Serializable {

    K key;
    V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "{Key: " + key + ", Value: " + value + "}";
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public int compareTo(Pair<K, V> other) {
        // Compare pairs based on their keys
        return this.key.compareTo(other.key);
    }
}
