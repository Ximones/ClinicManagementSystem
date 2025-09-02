/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control.PatientController;


import adt.DoublyLinkedList;
import adt.Pair;
import enitity.Patient;
import enitity.QueueEntry;
import java.time.LocalDate;
import utility.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author szeping
 */

public class QueueControl {
    private DoublyLinkedList<QueueEntry> queueList;
    private static String[] PREFIXES = {"E", "D", "Q", "A", "B"}; 
    // can rotate daily
    private static int lastQueueNumber = 0;
    private static LocalDate lastGeneratedDate = LocalDate.now();


    public QueueControl() {
        loadQueueData();
    }

       
    private static String generateQueueNumber() {
    LocalDate today = LocalDate.now();

    // Reset counter if a new day has started
    if (!today.equals(lastGeneratedDate)) {
        lastQueueNumber = 0;
        lastGeneratedDate = today;
    }

    // Prefix changes each day based on day index
    int dayIndex = today.getDayOfYear() % PREFIXES.length;
    String prefix = PREFIXES[dayIndex];

    lastQueueNumber++;
    return String.format("%s%03d", prefix, lastQueueNumber); 
}
   
    private void loadQueueData() {
    try {
        DoublyLinkedList<Pair<String, QueueEntry>> savedQueuePair =
            (DoublyLinkedList<Pair<String, QueueEntry>>) FileUtils.readDataFromFile("queue");
        
        if (savedQueuePair != null && !savedQueuePair.isEmpty()) {
            queueList = new DoublyLinkedList<>();
            
            // Get the date of the first saved entry
            Pair<String, QueueEntry> firstPair = savedQueuePair.getFirst().getEntry();
            QueueEntry firstEntry = firstPair.getValue();
            LocalDate savedDate = new java.sql.Date(firstEntry.getEnqueueTime()).toLocalDate();
            LocalDate today = LocalDate.now();

            if (!savedDate.equals(today)) {
                // 🔹 New day → reset queue
                lastQueueNumber = 0;
                lastGeneratedDate = today;
                queueList = new DoublyLinkedList<>(); // start empty
                return;
            }   

            // 🔹 Same day → restore data
            for (Pair<String, QueueEntry> pair : savedQueuePair) {
                QueueEntry entry = pair.getValue();
                queueList.insertLast(entry);
                try {
                    int num = Integer.parseInt(entry.getQueueNumber().substring(1));
                    if (num > lastQueueNumber) lastQueueNumber = num;
                } catch (NumberFormatException ignored) {}
            }
        } else {
            queueList = new DoublyLinkedList<>();
        }
    } catch (Exception ex) {
        System.err.println("Error loading queue data: " + ex.getMessage());
        queueList = new DoublyLinkedList<>();
    }
}


    // 🔹 Save queue data to file
    public void saveQueueData() {
        DoublyLinkedList<Pair<String, QueueEntry>> pairQueueList = new DoublyLinkedList<>();
        for (QueueEntry entry : queueList) {
            pairQueueList.insertLast(new Pair<>(entry.getQueueNumber(), entry));
        }
        FileUtils.writeDataToFile("queue", pairQueueList);
    }

    // 🔹 Add a new patient to queue
    public QueueEntry addToQueue(Patient patient) {
        String queueNo = generateQueueNumber();
        QueueEntry entry = new QueueEntry(patient, queueNo, "Waiting");
        queueList.insertLast(entry);
        saveQueueData();
        return entry;
    }

    // 🔹 Return all queue entries
    public DoublyLinkedList<QueueEntry> getQueueList() {
        return queueList;
    }

    // 🔹 Sort queue list by queue number
    public void sortQueue(String order) {
        List<QueueEntry> list = new ArrayList<>();
        for (QueueEntry entry : queueList) {
            list.add(entry);
        }

        Collections.sort(list); // relies on QueueEntry.compareTo()

        if ("DESC".equalsIgnoreCase(order)) {
            Collections.reverse(list);
        }

        queueList = new DoublyLinkedList<>();
        for (QueueEntry entry : list) {
            queueList.insertLast(entry);
        }
    }

    // 🔹 Find patient by IC in the queue
    public QueueEntry findByIC(String ic) {
        for (QueueEntry entry : queueList) {
            if (entry.getPatient().getPatientIC().equalsIgnoreCase(ic)) {
                return entry;
            }
        }
        return null;
    }
    

}