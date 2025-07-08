package adt;

/**
 * A simple, generic class to hold a key-value pair.
 * This is a helper class used to simulate Map-like behavior in a List.
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public class Pair<K, V> {

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
}