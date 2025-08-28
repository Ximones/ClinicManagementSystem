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
 * A Map implementation that uses a DoublyLinkedList of Pair objects as its
 * underlying storage. This demonstrates building a complex ADT from a simpler one.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public class ListMap<K extends Comparable<K>, V> implements MapInterface<K, V> {

    private DoublyLinkedList<Pair<K, V>> list;

    public ListMap() {
        list = new DoublyLinkedList<>();
    }

    @Override
    public V add(K key, V value) {
        // First, check if the key already exists.
        for (Pair<K, V> pair : list) {
            if (pair.getKey().equals(key)) {
                V oldValue = pair.getValue();
                pair.setValue(value); // Update existing value
                return oldValue;
            }
        }
        // If the key is new, add a new Pair to the list.
        list.insertLast(new Pair<>(key, value));
        return null;
    }

    @Override
    public V getValue(K key) {
        for (Pair<K, V> pair : list) {
            if (pair.getKey().equals(key)) {
                return pair.getValue();
            }
        }
        return null; // Key not found
    }

    @Override
    public boolean containsKey(K key) {
        for (Pair<K, V> pair : list) {
            if (pair.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public int getSize() {
        return list.getSize();
    }
    
    /**
     * A helper method to get the underlying list of pairs.
     * Useful for converting map data for reporting.
     * @return The DoublyLinkedList of Pairs.
     */
    public DoublyLinkedList<Pair<K, V>> getPairList() {
        return list;
    }
}
