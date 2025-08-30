/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control.PatientController;


import adt.DoublyLinkedList;
import adt.Pair;
import enitity.Patient;
import enitity.QueueEntry;
import utility.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author szepi
 */

public class QueueControl {
    private DoublyLinkedList<QueueEntry> queueList;
    private int lastNumber = 1000; // starting queue number

    public QueueControl() {
        loadQueueData();
    }

    // 🔹 Load queue data from file
    private void loadQueueData() {
        try {
            DoublyLinkedList<Pair<String, QueueEntry>> savedQueuePair =
                (DoublyLinkedList<Pair<String, QueueEntry>>) FileUtils.readDataFromFile("queue");
            if (savedQueuePair != null && !savedQueuePair.isEmpty()) {
                queueList = new DoublyLinkedList<>();
                for (Pair<String, QueueEntry> pair : savedQueuePair) {
                    QueueEntry entry = pair.getValue();
                    queueList.insertLast(entry);

                    // update lastNumber based on existing queue numbers
                    try {
                        int num = Integer.parseInt(entry.getQueueNumber().substring(1));
                        if (num > lastNumber) lastNumber = num;
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                queueList = new DoublyLinkedList<>();
            }
        } catch (ClassCastException e) {
            try {
                DoublyLinkedList<QueueEntry> savedQueue =
                    (DoublyLinkedList<QueueEntry>) FileUtils.readDataFromFile("queue");
                if (savedQueue != null) {
                    queueList = savedQueue;
                    for (QueueEntry entry : savedQueue) {
                        try {
                            int num = Integer.parseInt(entry.getQueueNumber().substring(1));
                            if (num > lastNumber) lastNumber = num;
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
    }

    // 🔹 Save queue data to file
    public void saveQueueData() {
        DoublyLinkedList<Pair<String, QueueEntry>> pairQueueList = new DoublyLinkedList<>();
        for (QueueEntry entry : queueList) {
            pairQueueList.insertLast(new Pair<>(entry.getQueueNumber(), entry));
        }
        FileUtils.writeDataToFile("queue", pairQueueList);
    }

    // 🔹 Generate next queue number
    public String generateQueueNumber() {
        lastNumber++;
        return "E" + lastNumber;
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