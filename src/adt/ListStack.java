/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

import java.util.Iterator;

/**
 *
 * @author User
 */
public class ListStack<T extends Comparable<T>> implements StackInterface<T> {

    private DoublyLinkedList<T> list;

    public ListStack() {
        list = new DoublyLinkedList<>();
    }

    @Override
    public void push(T newEntry) {
        // Pushing to the stack is equivalent to inserting at the front of the list.
        list.insertFirst(newEntry);
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        // Popping is removing the first element.
        T top = list.getFirst().getEntry();
        list.deleteFirst();
        return top;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        // Peeking is getting the first element without removing it.
        return list.getFirst().getEntry();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    /**
     * Provides an iterator to view the stack's contents without modifying it.
     * This is useful for displaying the "Recently Added" list.
     * @return An iterator for the stack.
     */
    public Iterator<T> getIterator() {
        return list.iterator();
    }
}
