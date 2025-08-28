/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

/**
 *
 * @author User
 */
/**
 * An interface for a Map Abstract Data Type (ADT).
 * A map stores key-value pairs and does not allow duplicate keys.
 *
 * @param <K> The type of the key (must be comparable).
 * @param <V> The type of the value.
 */
public interface MapInterface<K extends Comparable<K>, V> {

    /**
     * Adds a new key-value pair to the map. If the key already exists,
     * the existing value is replaced with the new value.
     *
     * @param key The key for the new entry.
     * @param value The value to be associated with the key.
     * @return The original value associated with the key if it was replaced, or null otherwise.
     */
    public V add(K key, V value);

    /**
     * Retrieves the value associated with a given key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The value associated with the key, or null if the key is not in the map.
     */
    public V getValue(K key);

    /**
     * Checks if the map contains a specific key.
     *
     * @param key The key to search for.
     * @return true if the map contains the key, false otherwise.
     */
    public boolean containsKey(K key);

    /**
     * Checks if the map is empty.
     *
     * @return true if the map contains no key-value pairs, false otherwise.
     */
    public boolean isEmpty();

    /**
     * Gets the total number of key-value pairs in the map.
     *
     * @return The number of entries in the map.
     */
    public int getSize();
}
