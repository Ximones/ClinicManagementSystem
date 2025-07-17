package utility;

import adt.DoublyLinkedList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A utility class for handling file input and output operations,
 * specifically for serializing and deserializing DoublyLinkedList objects.
 *
 * @author Chok Chun Fai
 */
public class FileUtils {

    // The directory where data files will be stored, relative to the project root.
    private static final String DIR_PATH = "dao";
    // The file extension for the binary data files.
    private static final String FILE_TYPE = ".bin";
    
    /**
     * Writes a DoublyLinkedList object to a binary file.
     * The file will be stored in the 'dao' directory.
     *
     * @param fileName The name of the file to create (without extension).
     * @param list The DoublyLinkedList object to be serialized and saved.
     */
    public static void writeDataToFile(String fileName, DoublyLinkedList<?> list) {
        // Create a File object representing the directory.
        File dir = new File(DIR_PATH);

        // If the directory does not exist, create it.
        if (!dir.exists()) {
            System.out.println("Creating directory: " + DIR_PATH);
            dir.mkdirs(); // Use mkdirs() to create parent directories if needed.
        }

        // Create a File object for the target binary file inside the directory.
        File file = new File(dir, fileName + FILE_TYPE);

        // Use a try-with-resources block to automatically close the stream.
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            // Write the entire list object to the file.
            oos.writeObject(list);
            System.out.println("Data successfully saved to " + file.getPath());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads a DoublyLinkedList object from a binary file.
     * The file is expected to be in the 'dao' directory.
     *
     * @param fileName The name of the file to read (without extension).
     * @return The deserialized DoublyLinkedList object, or an empty list if the file doesn't exist or an error occurs.
     */
    public static DoublyLinkedList<?> readDataFromFile(String fileName) {
        // Create a File object for the target binary file.
        File file = new File(DIR_PATH + "/" + fileName + FILE_TYPE);

        // Check if the file exists before trying to read it.
        if (!file.exists()) {
            System.out.println("File not found: " + file.getPath() + ". Returning a new list.");
            return new DoublyLinkedList<>(); // Return a new, empty list if no save file exists.
        }

        // Use a try-with-resources block to automatically close the stream.
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // Read the object from the file and cast it to DoublyLinkedList.
            Object obj = ois.readObject();
            System.out.println("Data successfully loaded from " + file.getPath());
            return (DoublyLinkedList<?>) obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading from file: " + e.getMessage());
            e.printStackTrace();
            // Return a new list in case of an error to prevent the program from crashing.
            return new DoublyLinkedList<>();
        }
    }
}
