/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

import java.util.Iterator;

/**
 *
 * @author Chok Chun Fai
 * @param <T>
 */
public class DoublyLinkedListIterator<T extends Comparable<T>> implements Iterator<T> {

    Node<T> current;

    // initialize pointer to head of the list for iteration
    public DoublyLinkedListIterator(DoublyLinkedList<T> list) {
        current = list.getFirst();
    }

    @Override
    // returns false if next element does not exist
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public T next() {
        T data = current.getEntry();
        current = current.getNext();
        return data;
    }

    @Override
    public void remove() {
        Iterator.super.remove(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

}
