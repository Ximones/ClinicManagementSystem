/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package adt;

import java.util.Arrays;

/**
 *
 * @author deadb
 */
public class ExampleMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        DoublyLinkedList<String> list = new DoublyLinkedList();
        list.insertFirst("Happy");
        list.insertLast("Day");
        list.insertAtPosition("Nigga", 2);
//        list.deleteAtPosition(3);
        list.displayFromFirst(list.getFirst());
        list.displayFromLast(list.getLast());
        System.out.println(list.getElement(3).entry);
        System.out.println(list.getSize());
//        list.clear();
//        DoublyLinkedList.displayFromFirst(list.head);
//        System.out.println(list.getSize());
        System.out.println(list.contains("Nigga"));
        System.out.println(list.replace(1, "Sad"));
        list.displayFromFirst(list.getFirst());
        for (String string : list) {
            System.out.println(string);
        }
        System.out.println(Arrays.toString(list.toArray()));

        // 1. Create the list
        DoublyLinkedList<Pair<String, Patient>> patientList = new DoublyLinkedList<>();

        // 2. Create your patient objects
        Patient patient1 = new Patient("Simon",40);
        Patient patient2 = new Patient("ZB",50);

        // 3. Create a unique Pair for each patient with a String key
        Pair<String, Patient> patientPair1 = new Pair<>("P001", patient1);
        Pair<String, Patient> patientPair2 = new Pair<>("P002", patient2);

        // 4. Insert EACH pair into the list
        patientList.insertLast(patientPair1); // Use insertLast to maintain order
        patientList.insertLast(patientPair2);

        // 5. Display the final list (assuming you've fixed the display method)
        patientList.displayFromFirst(patientList.getFirst());
        System.out.println(patientList.findByKey("P001"));
    }

}
