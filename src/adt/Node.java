package adt;

import java.io.Serializable;

/**
 *
 * @author Chok Chun Fai
 */

/**
 * Represents a single node in a Doubly Linked List.
 * Each node acts as a container for a data entry and holds references (pointers)
 * to the next and previous nodes in the sequence.
 * @param <T> The type of data stored in the node.
 */
public class Node<T> implements Serializable{

    private static final long serialVersionUID = 1L;

    // The actual data element stored in the node.
    T entry;
    // A reference to the previous node in the list.
    Node<T> prev;
    // A reference to the next node in thelist.
    Node<T> next;

    /**
     * Constructs a new node with the specified data.
     * The previous and next links are initialized to null.
     * @param newEntry The data to be stored in this node.
     */
    public Node(T newEntry) {
        this.entry = newEntry;
        this.prev = null;
        this.next = null;
    }

    /**
     * Returns the data stored in this node.
     * @return The entry of type T.
     */
    public T getEntry() {
        return entry;
    }

    /**
     * Sets or updates the data stored in this node.
     * @param entry The new data to be stored.
     */
    public void setEntry(T entry) {
        this.entry = entry;
    }

    /**
     * Returns the previous node in the list.
     * @return The preceding Node object.
     */
    public Node<T> getPrev() {
        return prev;
    }

    /**
     * Sets the reference to the previous node.
     * @param prev The node to be set as the previous node.
     */
    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    /**
     * Returns the next node in the list.
     * @return The succeeding Node object.
     */
    public Node<T> getNext() {
        return next;
    }

    /**
     * Sets the reference to the next node.
     * @param next The node to be set as the next node.
     */
    public void setNext(Node<T> next) {
        this.next = next;
    }
}