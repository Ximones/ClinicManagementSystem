CLINIC MANAGEMENT SYSTEM
========================

TAR UMT On-Campus Clinic Management System
Developed by: Team Members from Practical Class

PROJECT OVERVIEW
================
This is a comprehensive Clinic Management System designed for the Tunku Abdul Rahman 
University of Management and Technology (TAR UMT) on-campus clinic. The system 
implements the Entity-Control-Boundary (ECB) architectural pattern and uses custom 
Abstract Data Types (ADTs) instead of Java Collections Framework.

SYSTEM MODULES
==============
1. Patient Management - Handles patient registration, records, and queuing
2. Doctor Management - Manages doctor information, schedules, and availability
3. Consultation Management - Manages patient consultations and follow-up appointments
4. Medical Treatment Management - Manages patient diagnoses and treatment histories
5. Pharmacy Management - Manages medicine dispensing and stock control

CONSULTATION MANAGEMENT MODULE
==============================
Author: Zhen Bang
ADT Used: DoublyLinkedList with Pair<K,V> for Map-like functionality

Features:
- Create and manage patient consultations with real data integration
- Schedule follow-up appointments
- Track consultation status (Scheduled, In Progress, Completed, Cancelled)
- Generate reports (Daily Appointments, Consultation Types)
- Link consultations to real patients and doctors from other modules
- Maintain consultation history with symptoms, diagnosis, and notes
- **Queue Management** - Add patients to consultation queue, track waiting times with real-time updates
- **Prescription Management** - Create prescriptions with real medicine data from Pharmacy module
- **Complete Patient Workflow** - Queue → Consultation → Prescription → Follow-up
- **Real Data Integration** - Uses actual patient, doctor, and medicine data from other modules
- **Persistent Storage** - All consultation data is saved to files and persists between sessions
- **Queue Status Management** - Patients start with "Waiting" status, change to "In Progress" when called, "Done" when completed

Entities:
- Consultation: Main consultation entity with patient, doctor, date/time, type, status
- Appointment: Follow-up appointment entity linked to original consultation
- **Prescription**: Prescription entity linked to consultations with medicine items
- **PrescriptionItem**: Individual medicine items in prescriptions
- **QueueEntry**: Queue management for patient consultation flow

Reports:
1. Daily Appointments Report - Shows all scheduled appointments for today
2. Consultation Types Report - Summary of consultation types with statistics
3. **Queue Statistics Report** - Average waiting times and queue positions
4. **Prescription Report** - Medicine dispensing and cost analysis

HOW TO RUN THE APPLICATION
==========================

Prerequisites:
- Java Development Kit (JDK) 8 or higher
- NetBeans IDE (recommended) or any Java IDE
- iText 7 library for PDF generation (included in external_lib folder)
- JFreeChart library for chart generation (included in external_lib folder)

Steps to Run:
1. Open the project in NetBeans IDE
2. Ensure all required libraries are in the classpath:
   - All JAR files in src/external_lib/ folder
3. Build the project (Clean and Build)
4. Run the MainFrame class (src/boundary/MainFrame.java)
5. The application will start with the main clinic menu

Navigation:
- Use the main menu to access different modules
- Click "Consultation Management" to access the consultation module
- Use the navigation buttons to move between different screens
- Use "Back" buttons to return to previous screens

USING THE CONSULTATION MANAGEMENT MODULE
========================================

1. Main Consultation Management Menu:
   - Manage Consultations: Create and manage patient consultations
   - Manage Appointments: Schedule and manage follow-up appointments
   - Generate Reports: Create consultation and appointment reports

2. Consultation Management:
   - Add Consultation: Create new consultations with patient, doctor, date/time, type, and symptoms
   - Update Consultation: Change consultation status
   - View all consultations in a table format

3. Appointment Management:
   - Add Appointment: Schedule follow-up appointments linked to original consultations
   - Update Appointment: Change appointment status
   - View all appointments in a table format

4. Reports:
   - Daily Appointments Report: View today's scheduled appointments
   - Consultation Types Report: View statistics of consultation types

DATA VALIDATION
===============
- All date/time inputs must be in format: dd/MM/yyyy HH:mm
- Patient and doctor must be selected before creating consultations
- Symptoms and reason fields cannot be empty
- Status updates are validated before applying

TECHNICAL DETAILS
=================
- Architecture: Entity-Control-Boundary (ECB) pattern
- Data Structures: Custom DoublyLinkedList ADT with Pair<K,V> for Map functionality
- UI Framework: Java Swing with NetBeans GUI Builder
- Data Persistence: File-based storage using FileUtils for cross-module data sharing
- Reporting: Text-based reports with statistics
- Data Integration: Real-time loading of patient and doctor data from other modules

AUTHOR INFORMATION
==================
Consultation Management Module: Zhen Bang
Other modules: Team members from practical class

This project demonstrates the implementation of custom ADTs and ECB architectural 
pattern for a real-world clinic management system. 