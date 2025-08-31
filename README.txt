================================
Clinic Management System - README
================================

This document provides instructions on how to run this application and a high-level overview of how its different modules interact.

--------------------------------
1. How to Run the Application
--------------------------------

### Prerequisites
- Java Development Kit (JDK) 24 or later.
- Apache NetBeans IDE (Version 25 or later recommended).

### Setup Instructions
1.  **Open the Project in NetBeans:**
    - Launch the NetBeans IDE.
    - Go to `File > Open Project...`.
    - Navigate to the project folder and select it. NetBeans will recognize it as a "Java with Ant" project.

2.  **Verify Libraries:**
    - In the "Projects" window, expand the project and look for the "Libraries" folder.
    - Ensure all the necessary third-party .jar files (iText, JFreeChart, SLF4J) are present. They should be located in the `src/external_lib` folder.
	- Right Click Libraries and choose .jar/folder then select all files in the `src/external_lib` folder.

### Running the Program
1.  **Clean and Build:** It is highly recommended to first clean and build the project to ensure all files are compiled correctly. Go to the top menu and select `Run > Clean and Build Project`.
2.  **Run Project:** Click the **Run Project** button (the green play icon) in the main toolbar, or press the **F6** key.

### First-Time Data Initialization
- On the first run, the application will check for data files. If any are missing, it will automatically create a `dao` directory in the project's root folder and populate it with default test data (`.bin` files) for doctors, patients, schedules, etc.
- To reset the application to its original state, simply delete the `dao` folder and run the program again.

--------------------------------
2. How the Modules Interact
--------------------------------

The application is designed using a variation of the Model-View-Controller (MVC) architecture to keep the code organized and maintainable.

### Core Components:

* **Boundary (View):** These are the graphical user interface (GUI) components that the user sees and interacts with. They are located in the `boundary` package and consist of `JPanel` and `JDialog` classes (e.g., `DoctorInformationPanel`, `DoctorSchedulePanel`). Their job is to display data and capture user input (like button clicks). They should contain minimal logic.

* **Control (Controller):** These classes contain the application's business logic. They are located in the `control` package (e.g., `DoctorInformationControl`, `DoctorScheduleControl`). A controller is responsible for responding to user actions from the View, processing data, and telling the View when to update.

* **Entity (Model):** These are the simple data classes that represent the core objects of the application (e.g., `Doctor`, `DutySlot`). They hold the data and have getters and setters.

* **ADT (Abstract Data Type):** This is your custom data structure, the `DoublyLinkedList`, located in the `adt` package. It is used throughout the application to store and manage lists of entities.

* **Utility:** These are helper classes for common tasks, such as reading/writing files (`FileUtils`) and generating PDF reports (`ReportGenerator`).

### Example Interaction Flow: Adding a New Doctor

Here is how the modules work together when a user adds a new doctor:

1.  **User Action (View):** The user clicks the "Add Doctor" button on the `DoctorInformationPanel`.
2.  **Delegate to Controller:** The button's `actionPerformed` method in the View does not contain any logic. Instead, it makes a single call to the controller: `control.addNewDoctor()`.
3.  **Process Logic (Controller):**
    * The `addNewDoctor()` method in `DoctorInformationControl` takes over.
    * It creates and displays the `DoctorAddDialog`.
    * The dialog has its own controller, `DoctorAddControl`, which handles validation for the form fields.
    * When the user clicks "Save," the `DoctorAddControl` creates a new `Doctor` object and returns it to the `DoctorInformationControl`.
4.  **Update Data and View (Controller):**
    * The `DoctorInformationControl` adds the new `Doctor` object to its `masterDoctorList`.
    * It calls `FileUtils.writeDataToFile(...)` to save the updated list to the `doctors.bin` file, persisting the change.
    * Finally, it calls the `populateDoctorTable(...)` method in the `DoctorInformationPanel` to refresh the on-screen table with the new data.

This separation of concerns ensures that the UI is only responsible for display, the controller handles all the logic, and the data is neatly stored in the entity and ADT classes.