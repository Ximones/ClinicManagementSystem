/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

/**
 *
 * @author User
 */
public interface StackInterface<T> {
    /**
     * Adds a new entry to the top of the stack.
     * @param newEntry The object to be added to the stack.
     */
    public void push(T newEntry);

    /**
     * Removes and returns the stack's top entry.
     * @return The object at the top of the stack.
     */
    public T pop();

    /**
     * Retrieves the stack's top entry without removing it.
     * @return The object at the top of the stack.
     */
    public T peek();

    /**
     * Detects whether the stack is empty.
     * @return true if the stack is empty.
     */
    public boolean isEmpty();
}