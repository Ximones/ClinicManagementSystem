package adt;

import java.util.Iterator;

/**
 * A generic Doubly Linked List implementation. It manages a sequence of nodes,
 * each linked to its previous and next node. Implements Iterator to allow for
 * use in for-each loops. Implements a custom ListInterface.
 *
 * @param <T> The type of data stored in the list.
 */
public class DoublyLinkedList<T> implements Iterable<T>, ListInterface<T> {

    // A reference to the first node in the list.
    private Node<T> head;
    // A reference to the last node in the list.
    private Node<T> tail;

    /**
     * Constructs an empty DoublyLinkedList.
     */
    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
    }

    /**
     * Inserts a new element at the beginning of the list.
     *
     * @param newEntry The new element to add.
     */
    @Override
    public void insertFirst(T newEntry) {
        Node<T> temp = new Node<>(newEntry);
        if (head == null) { // If list is empty, new node is both head and tail
            head = temp;
            tail = temp;
        } else { // If list is not empty, link the new node to the old head
            temp.next = head;
            head.prev = temp;
            head = temp; // The new node becomes the new head
        }
    }

    /**
     * Inserts a new element at the end of the list.
     *
     * @param newEntry The new element to add.
     */
    @Override
    public void insertLast(T newEntry) {
        Node<T> temp = new Node<>(newEntry);
        if (tail == null) { // If list is empty, new node is both head and tail
            head = temp;
            tail = temp;
        } else { // If list is not empty, link the new node to the old tail
            tail.next = temp;
            temp.prev = tail;
            tail = temp; // The new node becomes the new tail
        }
    }

    /**
     * Inserts a new element at a specified position (1-based).
     *
     * @param newEntry The new element to add.
     * @param position The 1-based position to insert the element at.
     */
    @Override
    public void insertAtPosition(T newEntry, int position) {
        if (position == 1) {
            insertFirst(newEntry);
        } else {
            Node<T> current = head;
            int currentPosition = 1;
            // Traverse to the node currently at the desired position
            while (current != null && currentPosition < position) {
                current = current.next;
                currentPosition++;
            }
            if (current == null) { // If position is beyond the end, insert at the last
                insertLast(newEntry);
            } else { // Insert the new node before the current node
                Node<T> temp = new Node<>(newEntry);
                temp.next = current;
                temp.prev = current.prev;
                current.prev.next = temp;
                current.prev = temp;
            }
        }
    }

    /**
     * Deletes the first element from the list.
     */
    @Override
    public void deleteFirst() {
        if (head == null) { // Do nothing if list is empty
            return;
        }
        if (head == tail) { // If only one element, empty the list
            head = null;
            tail = null;
            return;
        }
        // Remove the head and clean up pointers
        Node<T> temp = head;
        head = head.next;
        head.prev = null;
        temp.next = null; // Help GC by removing reference from old node
    }

    /**
     * Deletes the last element from the list.
     */
    @Override
    public void deleteLast() {
        if (tail == null) { // Do nothing if list is empty
            return;
        }
        if (head == tail) { // If only one element, empty the list
            head = null;
            tail = null;
            return;
        }
        // Remove the tail and clean up pointers
        Node<T> temp = tail;
        tail = tail.prev;
        tail.next = null;
        temp.prev = null; // Help GC
    }

    /**
     * Deletes the element at a specified position (1-based).
     *
     * @param position The 1-based position of the element to delete.
     */
    @Override
    public void deleteAtPosition(int position) {
        if (head == null) { // Do nothing if list is empty
            return;
        }
        if (position == 1) {
            deleteFirst();
            return;
        }
        // Traverse to the node to be deleted
        Node<T> current = head;
        int count = 1;
        while (current != null && count != position) {
            current = current.next;
            count++;
        }
        if (current == null) { // Do nothing if position is out of bounds
            return;
        }
        if (current == tail) { // If it's the last element, use deleteLast
            deleteLast();
            return;
        }
        // Bypass the current node and clean up pointers
        current.prev.next = current.next;
        current.next.prev = current.prev;
        current.prev = null;
        current.next = null;
    }

    /**
     * Removes all elements from the list efficiently.
     */
    @Override
    public void clear() {
        head = null;
        tail = null;
    }

    /**
     * Returns the first node in the list.
     *
     * @return The head node.
     */
    @Override
    public Node<T> getFirst() {
        return head;
    }

    /**
     * Returns the last node in the list.
     *
     * @return The tail node.
     */
    @Override
    public Node<T> getLast() {
        return tail;
    }

    /**
     * Returns the node at a specified position (1-based).
     *
     * @param position The 1-based position of the element.
     * @return The node at the given position, or null if out of bounds.
     */
    @Override
    public Node<T> getElement(int position) {
        if (position < 1 || head == null) {
            return null;
        }
        Node<T> current = head;
        int count = 1;
        while (current != null && count < position) {
            current = current.next;
            count++;
        }
        return current;
    }

    /**
     * Returns the total number of elements in the list.
     *
     * @return The size of the list.
     */
    @Override
    public int getSize() {
        Node<T> current = head;
        int count = 0;
        while (current != null) {
            current = current.next;
            count++;
        }
        return count;
    }

    /**
     * Checks if the list is empty.
     *
     * @return true if the list has no elements, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Checks if the list contains a specified element.
     *
     * @param anEntry The element to search for.
     * @return true if the element is found, false otherwise.
     */
    @Override
    public boolean contains(T anEntry) {
        Node<T> temp = head;
        while (temp != null) {
            if (anEntry.equals(temp.entry)) {
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    /**
     * Finds the 0-based index of a specified element.
     *
     * @param anEntry The element to find.
     * @return The 0-based index of the first occurrence, or -1 if not found.
     */
    @Override
    public int indexOf(T anEntry) {
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            // **FIXED BUG:** Compare the entry data, not the node itself.
            if (current.entry.equals(anEntry)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    /**
     * Converts the list into an array.
     *
     * @return An array containing all elements of the list in order.
     */
    @Override
    public Object[] toArray() {
        Object[] array = new Object[getSize()];
        Node<T> current = head;
        int i = 0;
        while (current != null) {
            array[i++] = current.entry;
            current = current.next;
        }
        return array;
    }

    /**
     * Replaces the element at a specified position with a new element.
     *
     * @param position The 1-based position of the element to replace.
     * @param newEntry The new element.
     * @return true if the replacement was successful, false otherwise.
     */
    @Override
    public boolean replace(int position, T newEntry) {
        Node<T> nodeToReplace = getElement(position);
        if (nodeToReplace != null) {
            nodeToReplace.entry = newEntry;
            return true;
        }
        return false;
    }

    /**
     * Returns an iterator over elements of type T. Required for the Iterator
     * interface, allows use of for-each loops.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<T> iterator() {
        return new DoublyLinkedListIterator<>(this);
    }

    /**
     * Displays the contents of the list from head to tail. This is an instance
     * method for better encapsulation.
     *
     * @param head
     */
    @Override
    public void displayFromFirst(Node head) {
        Node<T> temp = this.head;
        System.out.print("[");
        while (temp != null) {
            System.out.print(temp.entry);
            temp = temp.next;
            if (temp != null) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    /**
     * Displays the contents of the list from tail to head. This is an instance
     * method for better encapsulation.
     *
     * @param tail
     */
    @Override
    public void displayFromLast(Node tail) {
        Node<T> temp = this.tail;
        System.out.print("[");
        while (temp != null) {
            System.out.print(temp.entry);
            temp = temp.prev;
            if (temp != null) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    /**
     * Finds the value associated with a specific key by searching through a
     * list of Pair objects.
     *
     * @param key The key to search for, treated as a generic Object.
     * @return The value associated with the key, or null if not found. The
     * return type is Object.
     */
    @Override
    public Object findByKey(Object key) {
        Node<T> currentNode = this.head;

        while (currentNode != null) {
            // Check if the entry is actually a Pair object
            if (currentNode.getEntry() instanceof Pair) {

                // Cast the entry to a Pair to access its key and value
                Pair<?, ?> currentPair = (Pair<?, ?>) currentNode.getEntry();

                // Compare the key from the pair with the key provided
                if (currentPair.key.equals(key)) {
                    return currentPair.value; // Return the value if keys match
                }
            }
            currentNode = currentNode.getNext();
        }
        return null; // Return null if key is not found
    }
}
