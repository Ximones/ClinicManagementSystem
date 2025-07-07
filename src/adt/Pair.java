/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

/**
 *
 * @author deadb
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {

    K key;
    V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public V findByKey(K key, DoublyLinkedList list) {
        Node<Pair<K, V>> current = list.getFirst();

        while (current != null) {
            if (current.entry.key.equals(key)) {
                return current.entry.value;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public String toString() {
        // This will now call the custom toString() of the Patient object
        return "{Key: " + key + ", Value: " + value + "}";
    }
}
