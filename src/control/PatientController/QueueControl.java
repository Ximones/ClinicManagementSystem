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
    private static int lastQueueNumber = 1000; // reset daily to E1000
    private static LocalDate lastGeneratedDate = LocalDate.now();

    public QueueControl() {
        loadQueueData();
    }

    // 🔹 Generate queue number (E1001, E1002, ...)
    private static String generateQueueNumber() {
        LocalDate today = LocalDate.now();
        // Reset counter if a new day has started
        if (!today.equals(lastGeneratedDate)) {
            lastQueueNumber = 1000; // reset to baseline
            lastGeneratedDate = today;
        }
        lastQueueNumber++;
        return "E" + lastQueueNumber;
    }

    // 🔹 Load queue data
    private void loadQueueData() {
        try {
            DoublyLinkedList<Pair<String, QueueEntry>> savedQueuePair
                    = (DoublyLinkedList<Pair<String, QueueEntry>>) FileUtils.readDataFromFile("queue");

            if (savedQueuePair != null && !savedQueuePair.isEmpty()) {
                queueList = new DoublyLinkedList<>();

                Pair<String, QueueEntry> firstPair = savedQueuePair.getFirst().getEntry();
                QueueEntry firstEntry = firstPair.getValue();
                LocalDate savedDate = new java.sql.Date(firstEntry.getEnqueueTime()).toLocalDate();
                LocalDate today = LocalDate.now();

                if (!savedDate.equals(today)) {
                    // 🔹 New day → reset
                    lastQueueNumber = 1000;
                    lastGeneratedDate = today;
                    queueList = new DoublyLinkedList<>();
                    return;
                }

                // 🔹 Same day → restore data
                for (Pair<String, QueueEntry> pair : savedQueuePair) {
                    QueueEntry entry = pair.getValue();
                    queueList.insertLast(entry);
                    try {
                        int num = Integer.parseInt(entry.getQueueNumber().substring(1));
                        if (num > lastQueueNumber) {
                            lastQueueNumber = num;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            } else {
                queueList = new DoublyLinkedList<>();
            }
        } catch (Exception ex) {
            System.err.println("Error loading queue data: " + ex.getMessage());
            queueList = new DoublyLinkedList<>();
        }
    }

    // 🔹 Save queue data
    public void saveQueueData() {
        DoublyLinkedList<Pair<String, QueueEntry>> pairQueueList = new DoublyLinkedList<>();
        for (QueueEntry entry : queueList) {
            pairQueueList.insertLast(new Pair<>(entry.getQueueNumber(), entry));
        }
        FileUtils.writeDataToFile("queue", pairQueueList);
    }

    // 🔹 Add patient
    public QueueEntry addToQueue(Patient patient) {
        String queueNo = generateQueueNumber();
        QueueEntry entry = new QueueEntry(patient, queueNo, "Waiting");
        queueList.insertLast(entry);
        saveQueueData();
        return entry;
    }

    // 🔹 Get list
    public DoublyLinkedList<QueueEntry> getQueueList() {
        return queueList;
    }

    // 🔹 Sort by queue number
    public void sortQueue(String order) {
        List<QueueEntry> list = new ArrayList<>();
        for (QueueEntry entry : queueList) {
            list.add(entry);
        }
        Collections.sort(list);
        if ("DESC".equalsIgnoreCase(order)) {
            Collections.reverse(list);
        }
        queueList = new DoublyLinkedList<>();
        for (QueueEntry entry : list) {
            queueList.insertLast(entry);
        }
    }

    // 🔹 Find by IC
    public QueueEntry findByIC(String ic) {
        for (QueueEntry entry : queueList) {
            if (entry.getPatient().getPatientIC().equalsIgnoreCase(ic)) {
                return entry;
            }
        }
        return null;
    }
}
