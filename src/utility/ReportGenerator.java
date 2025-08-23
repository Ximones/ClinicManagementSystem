package utility;

import adt.DoublyLinkedList;
import adt.Pair;
import enitity.Doctor;
import java.io.File;

// Core PDF creation classes for iText 7
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

// Classes for adding elements like text and images for iText 7
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import enitity.DutySlot;
import enitity.Medicine;
import enitity.Patient;
import enitity.QueueEntry;
import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class ReportGenerator {

    /**
     * Report : Generates a PDF with a Pie Chart of Doctor Specializations.
     *
     * @param doctorList The master list of all doctors.
     */
    public static void generateSpecializationReport(DoublyLinkedList<Pair<String, Doctor>> doctorList) {
        try {
            // --- Step 1: Count the data using your helper method ---
            DoublyLinkedList<Pair<String, Integer>> specializationCounts = countOccurrences(doctorList, "position");

            // --- Step 2: Create the Pie Chart using JFreeChart ---
            DefaultPieDataset pieDataset = new DefaultPieDataset();

            // Loop through DoublyLinkedList of pairs
            for (Pair<String, Integer> entry : specializationCounts) {
                pieDataset.setValue(entry.getKey(), entry.getValue());
            }
            JFreeChart pieChart = ChartFactory.createPieChart("Doctor Specialization Distribution", pieDataset, true, true, false);
            File chartFile = new File("specialization_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, pieChart, 450, 400);

            // --- Step 3: Create the PDF using iText 7 ---
            PdfWriter writer = new PdfWriter("Doctor_Specialization_Report.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Doctor Specialization Report"));
            document.add(new Paragraph(" "));

            Image chartImage = new Image(ImageDataFactory.create("specialization_chart.png"));
            document.add(chartImage);

            document.close();
            chartFile.delete();

            System.out.println("Specialization Report generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Report : Generates a PDF with a Bar Chart of Doctor Availability Status.
     *
     * @param doctorList The master list of all doctors.
     */
    public static void generateAvailabilityReport(DoublyLinkedList<Pair<String, Doctor>> doctorList) {
        try {
            // --- Step 1: Count the data using your helper method ---
            DoublyLinkedList<Pair<String, Integer>> statusCounts = countOccurrences(doctorList, "status");

            // --- Step 2: Create the Bar Chart using JFreeChart ---
            DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

            // Loop through  DoublyLinkedList of pairs
            for (Pair<String, Integer> entry : statusCounts) {
                barDataset.addValue(entry.getValue(), "Doctors", entry.getKey());
            }
            JFreeChart barChart = ChartFactory.createBarChart("Doctor Availability Status", "Status", "Number of Doctors", barDataset);
            File chartFile = new File("status_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, barChart, 500, 350);

            // --- Step 3: Create the PDF using iText 7 ---
            PdfWriter writer = new PdfWriter("Doctor_Availability_Report.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Doctor Availability Report"));
            document.add(new Paragraph(" "));

            Image chartImage = new Image(ImageDataFactory.create("status_chart.png"));
            document.add(chartImage);

            document.close();
            chartFile.delete();
            System.out.println("Availability Report generated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A helper method to count occurrences of a specific attribute from the
     * doctor list.
     *
     * @param doctorList The master list of doctors.
     * @param attributeType A string indicating what to count ("position" or
     * "status").
     * @return A list of pairs where the key is the attribute (e.g., "Doctor")
     * and the value is its count.
     */
    private static DoublyLinkedList<Pair<String, Integer>> countOccurrences(DoublyLinkedList<Pair<String, Doctor>> doctorList, String attributeType) {

        DoublyLinkedList<Pair<String, Integer>> counts = new DoublyLinkedList<>();

        for (Pair<String, Doctor> docPair : doctorList) {
            String key;
            if ("position".equals(attributeType)) {
                key = docPair.getValue().getPosition();
            } else { // "status"
                key = docPair.getValue().getStatus();
            }

            // Check if this position/status is already in our counts list
            Pair<String, Integer> existingEntry = null;
            for (Pair<String, Integer> countPair : counts) {
                if (countPair.getKey().equals(key)) {
                    existingEntry = countPair;
                    break;
                }
            }

            if (existingEntry != null) {
                // If it exists, increment its count
                existingEntry.setValue(existingEntry.getValue() + 1);
            } else {
                // If it doesn't exist, add it as a new entry with a count of 1
                counts.insertLast(new Pair<>(key, 1));
            }
        }
        return counts;
    }

    /**
     * Generates a PDF report of a weekly duty schedule.
     *
     * @param scheduleList The list of schedule slots for the week.
     * @param reportTitle The title for the report, e.g., "Next Week's Duty
     * Schedule".
     */
    public static void generateWeeklyScheduleReport(
            DoublyLinkedList<DutySlot> scheduleList,
            String reportTitle,
            DoublyLinkedList<String> DAYS,
            DoublyLinkedList<String> SHIFTS) {

        try {
            String fileName = reportTitle.replace(" ", "_") + ".pdf";

            // --- Step 1: Create PDF document ---
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph(reportTitle).setFontSize(18));
            document.add(new Paragraph(" "));

            // --- Step 2: Create the Table Headers Dynamically ---
            // Create a table with 1 column for the day + a column for each shift
            Table table = new Table(UnitValue.createPercentArray(SHIFTS.getSize() + 1));
            table.setWidth(UnitValue.createPercentValue(100));

            // Add headers
            table.addHeaderCell(new Cell().add(new Paragraph("Day")));
            for (String shift : SHIFTS) {
                table.addHeaderCell(new Cell().add(new Paragraph(shift)));
            }

            // --- Step 3: Populate the Table using your ADTs ---
            for (String day : DAYS) {
                // Add the day of the week in the first column
                table.addCell(new Cell().add(new Paragraph(day)));

                // Find and add the doctor for each shift in the other columns
                for (String shift : SHIFTS) {
                    Doctor assignedDoctor = findDoctorForSlot(scheduleList, day, shift);
                    String doctorName = (assignedDoctor != null) ? assignedDoctor.getName() : "Unassigned";
                    table.addCell(new Cell().add(new Paragraph(doctorName)));
                }
            }

            // --- Step 4: Add the completed table to the document ---
            document.add(table);
            document.close();

            System.out.println("Schedule Report '" + fileName + "' generated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to find the assigned doctor for a specific slot
    private static Doctor findDoctorForSlot(DoublyLinkedList<DutySlot> schedule, String day, String shift) {
        for (DutySlot slot : schedule) {
            if (slot.getDayOfWeek().equals(day) && slot.getShift().equals(shift)) {
                return slot.getAssignedDoctor();
            }
        }
        return null;
    }

    public static void generateLowStockReport(
            DoublyLinkedList<Pair<String, Medicine>> medicineList,
            int threshold) {

        try {
            String fileName = "Low_Stock_Alert_Report.pdf";
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Low-Stock Alert Report").setFontSize(18));
            document.add(new Paragraph("Threshold: " + threshold));
            document.add(new Paragraph(" "));

            // Create dataset for stacked bar chart
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            DoublyLinkedList<Medicine> lowStockMedicines = new DoublyLinkedList<>();

            for (Pair<String, Medicine> pair : medicineList) {
                Medicine med = pair.getValue();
                if (med.getQuantity() <= threshold) {
                    dataset.addValue(med.getQuantity(), med.getName(), med.getCategory());
                    lowStockMedicines.insertLast(med);
                }
            }

            // Create stacked vertical bar chart
            JFreeChart stackedBarChart = ChartFactory.createStackedBarChart(
                    "Low Stock Medicines by Category",
                    "Category",
                    "Quantity",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            // Customize colors
            CategoryPlot plot = stackedBarChart.getCategoryPlot();
            BarRenderer renderer = (BarRenderer) plot.getRenderer();

            // Generate unique colors for each medicine
            Map<String, Color> medicineColors = new HashMap<>();
            int colorIndex = 0;
            Color[] colors = new Color[]{
                Color.decode("#1f77b4"), Color.decode("#ff7f0e"), Color.decode("#2ca02c"),
                Color.decode("#d62728"), Color.decode("#9467bd"), Color.decode("#8c564b"),
                Color.decode("#e377c2"), Color.decode("#7f7f7f"), Color.decode("#bcbd22"),
                Color.decode("#17becf")
            };

            for (int i = 0; i < dataset.getRowCount(); i++) {
                String medicineName = (String) dataset.getRowKey(i);
                Color color = colors[colorIndex % colors.length];
                medicineColors.put(medicineName, color);
                renderer.setSeriesPaint(i, color);
                colorIndex++;
            }

            File chartFile = new File("low_stock_stacked_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, stackedBarChart, 700, 450);

            Image chartImage = new Image(ImageDataFactory.create(chartFile.getAbsolutePath()));
            document.add(chartImage);
            document.add(new Paragraph(" "));

            // Table for detailed information
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2}))
                    .setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Medicine Name")));
            table.addHeaderCell(new Cell().add(new Paragraph("Category")));
            table.addHeaderCell(new Cell().add(new Paragraph("Remaining Stock")));

            for (Medicine med : lowStockMedicines) {
                table.addCell(new Cell().add(new Paragraph(med.getName())));
                table.addCell(new Cell().add(new Paragraph(med.getCategory())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(med.getQuantity()))));
            }

            document.add(table);
            document.close();
            chartFile.delete();

            System.out.println("Low Stock Alert Report generated: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateQueueStatisticsReport(DoublyLinkedList<QueueEntry> queueList) {
        try {
            String fileName = "Queue_Statistics_Report.pdf";
            long totalWaitingTime = 0;
            long longestWaitingTime = 0;
            int count = 0;

            for (QueueEntry entry : queueList) {
                if (entry.getStartConsultTime() > 0) { // consultation started
                    long waitingTime = (entry.getStartConsultTime() - entry.getEnqueueTime()) / 1000; // seconds
                    totalWaitingTime += waitingTime;
                    if (waitingTime > longestWaitingTime) {
                        longestWaitingTime = waitingTime;
                    }
                    count++;
                }
            }

            double averageWaitingTime = (count > 0) ? (double) totalWaitingTime / count : 0;

            // --- chart and pdf code (same as yours) ---
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(averageWaitingTime, "Waiting Times", "Average");
            dataset.addValue(longestWaitingTime, "Waiting Times", "Longest");

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Queue Waiting Times",
                    "Type",
                    "Seconds",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false
            );

            File chartFile = new File("queue_statistics_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, barChart, 500, 350);

            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Queue Statistics Report").setFontSize(18));
            document.add(new Paragraph("Average and longest waiting times in the queue:"));
            document.add(new Paragraph(" "));

            Image chartImage = new Image(ImageDataFactory.create(chartFile.getAbsolutePath()));
            document.add(chartImage);

            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2}))
                    .setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell(new Cell().add(new Paragraph("Metric")));
            table.addHeaderCell(new Cell().add(new Paragraph("Value (seconds)")));
            table.addCell("Average Waiting Time");
            table.addCell(String.format("%.2f", averageWaitingTime));
            table.addCell("Longest Waiting Time");
            table.addCell(String.valueOf(longestWaitingTime));

            document.add(new Paragraph(" "));
            document.add(table);
            document.close();
            chartFile.delete();

            System.out.println("Queue Statistics Report generated: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
