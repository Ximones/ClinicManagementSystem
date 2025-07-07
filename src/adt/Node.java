/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

/**
 *
 * @author deadb
 * @param <T>
 */
public class Node<T> {

    T entry;
    Node prev;
    Node next;

    public Node(T newEntry){
        this.entry = newEntry;
        this.prev = null;
        this.next = null;
    }

    public T getEntry() {
        return entry;
    }

    public Node<T> getNext() {
        return next;
    }

}
