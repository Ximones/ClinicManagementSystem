package adt;

/**
 * An interface that defines the contract for a generic List Abstract Data Type
 * (ADT). It outlines all the standard operations that a list implementation
 * should support.
 *
 * @author Chok Chun Fai
 * @param <T> The type of data the list will hold.
 */
public interface ListInterface<T> {

    /**
     * Adds a new entry to the beginning of the list.
     *
     * @param newEntry The new element to add.
     */
    public void insertFirst(T newEntry);

    /**
     * Adds a new entry to the end of the list.
     *
     * @param newEntry The new element to add.
     */
    public void insertLast(T newEntry);

    /**
     * Adds a new entry at a specified position in the list (1-based).
     *
     * @param newEntry The new element to add.
     * @param position The 1-based position to insert the new entry at.
     */
    public void insertAtPosition(T newEntry, int position);

    /**
     * Removes the first entry from the list.
     */
    public void deleteFirst();

    /**
     * Removes the last entry from the list.
     */
    public void deleteLast();

    /**
     * Removes the entry at a specified position from the list (1-based).
     *
     * @param position The 1-based position of the entry to remove.
     */
    public void deleteAtPosition(int position);

    /**
     * Removes all entries from the list, making it empty.
     */
    public void clear();

    /**
     * Retrieves the first node of the list.
     *
     * @return The first node in the list.
     */
    public Node<T> getFirst();

    /**
     * Retrieves the last node of the list.
     *
     * @return The last node in the list.
     */
    public Node<T> getLast();

    /**
     * Retrieves the node at a specified position (1-based).
     *
     * @param position The 1-based position of the node to retrieve.
     * @return The node at the given position, or null if the position is
     * invalid.
     */
    public Node<T> getElement(int position);

    /**
     * Gets the total number of entries in the list.
     *
     * @return The integer number of entries currently in the list.
     */
    public int getSize();

    /**
     * Checks if the list is empty.
     *
     * @return true if the list is empty, false otherwise.
     */
    public boolean isEmpty();

    /**
     * Checks whether the list contains a given entry.
     *
     * @param anEntry The entry to look for.
     * @return true if the entry is in the list, false otherwise.
     */
    public boolean contains(T anEntry);

    /**
     * Gets the position (0-based index) of the first occurrence of a given
     * entry.
     *
     * @param anEntry The entry to find.
     * @return The 0-based index of the entry, or -1 if not found.
     */
    public int indexOf(T anEntry);

    /**
     * Replaces the entry at a given position with a new entry.
     *
     * @param position The 1-based position of the entry to replace.
     * @param newEntry The new entry to place in the list.
     * @return true if the replacement was successful, false otherwise.
     */
    public boolean replace(int position, T newEntry);

    /**
     * Displays all entries in the list from first to last.
     *
     * @param node
     */
    public void displayFromFirst(Node node);

    /**
     * Displays all entries in the list from last to first.
     *
     * @param node
     */
    public void displayFromLast(Node node);

    /**
     * Finds the value associated with a specific key from a list of Pair
     * objects.
     *
     * @param key The key to search for.
     * @return The value (as an Object) associated with the key, or null if not
     * found.
     */
    public Object findByKey(Object key);

    public void sort();

    public void reverse();

    public T binarySearch(T keyToFind);
}
