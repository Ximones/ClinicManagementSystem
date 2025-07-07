/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package adt;

/**
 *
 * @author deadb
 * @param <T>
 */
public interface ListInterface<T> {

    public void insertFirst(T newEntry);

    public void insertLast(T newEntry);

    public void insertAtPosition(T newEntry, int position);

    public void deleteFirst();

    public void deleteLast();

    public void deleteAtPosition(int position);

    public void clear();

    public Node getFirst();

    public Node getLast();

    public Node getElement(int position);

    public int getSize();

    public boolean isEmpty();

    public boolean contains(T anEntry);

    public int indexOf(T anEntry);

    public Object[] toArray();

    public boolean replace(int position, T newEntry);

    public void displayFromFirst(Node head);

    public void displayFromLast(Node tail);
}
