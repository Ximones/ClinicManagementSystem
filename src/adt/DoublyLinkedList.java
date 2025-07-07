/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

import java.util.Iterator;

/**
 *
 * @author deadb
 * @param <T>
 */
public class DoublyLinkedList<T> implements Iterable<T>, ListInterface<T> {

    Node head;
    Node tail;

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
    }

    @Override
    public void insertFirst(T newEntry) {
        Node temp = new Node(newEntry);
        if (head == null) {
            head = temp;
            tail = temp;

        } else {
            temp.next = head;
            head.prev = temp;
            head = temp;
        }
    }

    @Override
    public void insertLast(T newEntry) {
        Node temp = new Node(newEntry);
        if (tail == null) {
            head = temp;
            tail = temp;

        } else {
            tail.next = temp;
            temp.prev = tail;
            tail = temp;
        }
    }

    @Override
    public void insertAtPosition(T newEntry, int position) {
        Node temp = new Node(newEntry);
        if (position == 1) {
            insertFirst(newEntry);
        } else {
            Node current = head;
            int currentPosition = 1;
            while (current != null && currentPosition < position) {
                current = current.next;
                currentPosition++;
            }
            if (current == null) {
                insertLast(newEntry);
            } else {
                temp.next = current;
                temp.prev = current.prev;
                current.prev.next = temp;
                current.prev = temp;
            }
        }
    }

    @Override
    public void deleteFirst() {
        if (head == null) {
            return;
        }
        if (head == tail) {
            head = null;
            tail = null;
            return;
        }
        Node temp = head;
        head = head.next;
        head.prev = null;
        temp.next = null;
    }

    @Override
    public void deleteLast() {
        if (tail == null) {
            return;
        }
        if (head == tail) {
            head = null;
            tail = null;
            return;
        }
        Node temp = tail;
        tail = tail.prev;
        tail.next = null;
        temp.prev = null;
    }

    @Override
    public void deleteAtPosition(int position) {
        if (head == null) {
            return;
        }
        if (position == 1) {
            deleteFirst();
            return;
        }
        Node current = head;
        int count = 1;
        while (current != null && count != position) {
            current = current.next;
            count++;
        }
        if (current == null) {
            System.out.println("Position is null");
            return;
        }
        if (current == tail) {
            deleteLast();
            return;
        }
        current.prev.next = current.next;
        current.next.prev = current.prev;
        current.prev = null;
        current.next = null;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
    }

    @Override
    public Node getFirst() {
        return head;
    }

    @Override
    public Node getLast() {
        return tail;
    }

    @Override
    public Node getElement(int position) {
        if (head == null) {
            return null;
        }
        if (position == 1) {
            return getFirst();
        }
        Node current = head;
        int count = 1;
        while (current != null && count != position) {
            current = current.next;
            count++;
        }
        if (current == null) {
            System.out.println("Position is null");
            return null;
        }
        if (current == tail) {
            return getLast();

        }
        return current;
    }

    @Override
    public int getSize() {

        if (head == null) {
            return 0;
        }
        Node current = head;
        int count = 0;
        while (current != null) {
            current = current.next;
            count++;
        }
        return count;
    }

    @Override
    public boolean isEmpty() {

        return getSize() == 0;
    }

    @Override
    public boolean contains(T anEntry) {

        boolean found = false;

        if (head == null) {
            return false;
        } else {
            Node temp = head;
            while (temp != null) {
                if (anEntry.equals(temp.entry)) {
                    found = true;
                    break;
                }
                temp = temp.next;
            }
        }
        return found;
    }

    @Override
    public int indexOf(T anEntry) {
        Node current = head;
        int index = 0;
        while (current != null) {
            if (current.equals(anEntry)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[getSize()];
        Node current = head;
        int i = 0;
        while (current != null) {
            array[i] = current.entry;
            current = current.next;
            i++;
        }

        return array;
    }

    @Override
    public boolean replace(int position, T newEntry) {

        Node nodeToReplace = getElement(position);

        if (nodeToReplace != null) {
            nodeToReplace.entry = newEntry;
            return true;
        }
        return false;
    }

    @Override
    public void displayFromFirst(Node head) {
        Node temp = this.head;
        System.out.print("[");
        while (temp != null) {
            System.out.print(temp.entry);
            temp = temp.next;
            if (temp != null) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }

    @Override
    public void displayFromLast(Node tail) {
        Node temp = this.tail;
        System.out.print("[");
        while (temp != null) {
            System.out.print(temp.entry);
            temp = temp.prev;
            if (temp != null) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }

    @Override
    public Iterator<T> iterator() {
        return new DoublyLinkedListIterator<>(this);
    }

}
