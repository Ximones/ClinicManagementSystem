package utility;

import adt.DoublyLinkedList;
import adt.Pair;
import enitity.Doctor;
import enitity.Treatment;
import enitity.Consultation;
import enitity.Appointment;
import java.io.File;

// Core PDF creation classes for iText 7
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
// Classes for adding elements like text and images for iText 7
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.TextAlignment;
import enitity.DispenseRecord;
import enitity.DutySlot;
import enitity.Medicine;
import enitity.Patient;
import enitity.QueueEntry;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            // --- Step 1: Count the data (No change here) ---
            DoublyLinkedList<Pair<String, Integer>> specializationCounts = countOccurrences(doctorList, "position");

            // --- Step 2: Create the Pie Chart (No change here) ---
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            for (Pair<String, Integer> entry : specializationCounts) {
                pieDataset.setValue(entry.getKey(), entry.getValue());
            }
            JFreeChart pieChart = ChartFactory.createPieChart("Doctor Specialization Distribution", pieDataset, true, true, false);

            // --- ADD THESE LINES TO CUSTOMIZE THE LABELS ---
            PiePlot plot = (PiePlot) pieChart.getPlot();
            StandardPieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                    "{0}: {1} ({2})"); // {0}=Key, {1}=Value, {2}=Percentage
            plot.setLabelGenerator(labelGenerator);
            // --- END OF NEW CODE ---

            File chartFile = new File("specialization_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, pieChart, 450, 400);

            // --- Step 3: Create the PDF (No change here) ---
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
     * Report : Generates a PDF with a Pie Chart of Doctor Availability Status.
     *
     * @param doctorList The master list of all doctors.
     */
    public static void generateAvailabilityReport(DoublyLinkedList<Pair<String, Doctor>> doctorList) {
        try {
            // --- Step 1: Count the data (This part is the same) ---
            DoublyLinkedList<Pair<String, Integer>> statusCounts = countOccurrences(doctorList, "status");

            // --- Step 2: Create the Pie Chart using JFreeChart (This part is changed) ---
            // Use DefaultPieDataset instead of DefaultCategoryDataset
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            for (Pair<String, Integer> entry : statusCounts) {
                // Use .setValue(key, value) for pie charts
                pieDataset.setValue(entry.getKey(), entry.getValue());
            }

            // Use ChartFactory.createPieChart
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Doctor Availability Status", // Chart Title
                    pieDataset, // Dataset
                    true, true, false);

            // Optional but recommended: Customize labels to show count and percentage
            PiePlot plot = (PiePlot) pieChart.getPlot();
            StandardPieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                    "{0}: {1} ({2})"); // Format: Status: Count (Percentage)
            plot.setLabelGenerator(labelGenerator);

            File chartFile = new File("status_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, pieChart, 500, 350);

            // --- Step 3: Create the PDF (This part is the same) ---
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

    public static void generateDailyDispenseReport(DoublyLinkedList<DispenseRecord> dispenseRecords) {
        try {
            // 1) Filter today's records
            LocalDate today = LocalDate.now();
            DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            DoublyLinkedList<DispenseRecord> todayRecords = new DoublyLinkedList<>();
            for (DispenseRecord dr : dispenseRecords) {
                LocalDate recordDate = LocalDate.parse(dr.getDateTime().substring(0, 10));
                if (recordDate.equals(today)) {
                    todayRecords.insertLast(dr);
                }
            }

            if (todayRecords.isEmpty()) {
                System.out.println("No dispensing records for today.");
                return;
            }

            // 2) Aggregate quantities per medicine
            Map<String, Integer> medicineQuantities = new HashMap<>();
            int totalQuantity = 0;
            double totalAmount = 0.0;

            for (DispenseRecord dr : todayRecords) {
                medicineQuantities.put(dr.getMedicineName(),
                        medicineQuantities.getOrDefault(dr.getMedicineName(), 0) + dr.getQuantity());
                totalQuantity += dr.getQuantity();
                totalAmount += dr.getLineTotal();
            }

            // 3) Create Bar Chart
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (String med : medicineQuantities.keySet()) {
                dataset.addValue(medicineQuantities.get(med), "Quantity", med);
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Daily Dispensing Chart",
                    "Medicine",
                    "Quantity",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false
            );

            CategoryPlot plot = chart.getCategoryPlot();
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setDrawBarOutline(false);

            File chartFile = new File("daily_dispense_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, chart, 700, 400);

            // 4) Create PDF
            PdfWriter writer = new PdfWriter("Daily_Dispense_Report.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Daily Dispensing Report - " + today).setFontSize(18));
            document.add(new Paragraph(" "));

            // 5) Add chart image FIRST
            Image chartImage = new Image(ImageDataFactory.create(chartFile.getAbsolutePath()));
            document.add(chartImage);
            document.add(new Paragraph(" "));

            // 6) Table of dispensed items AFTER chart
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 2, 2, 2}))
                    .setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell(new Cell().add(new Paragraph("Prescription ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("Patient")));
            table.addHeaderCell(new Cell().add(new Paragraph("Doctor")));
            table.addHeaderCell(new Cell().add(new Paragraph("Medicine")));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity")));
            table.addHeaderCell(new Cell().add(new Paragraph("Total (RM)")));

            for (DispenseRecord dr : todayRecords) {
                table.addCell(new Cell().add(new Paragraph(dr.getPrescriptionId())));
                table.addCell(new Cell().add(new Paragraph(dr.getPatientName())));
                table.addCell(new Cell().add(new Paragraph(dr.getDoctorName())));
                table.addCell(new Cell().add(new Paragraph(dr.getMedicineName())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(dr.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", dr.getLineTotal()))));
            }

            // Add total row at the end
            Cell totalCell = new Cell(1, 4).add(new Paragraph("TOTAL").setBold());
            totalCell.setTextAlignment(TextAlignment.RIGHT);
            table.addCell(totalCell);

            table.addCell(new Cell().add(new Paragraph(String.valueOf(totalQuantity)).setBold()));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", totalAmount)).setBold()));

            document.add(table);

            // 7) Finish up
            document.close();
            chartFile.delete();

            System.out.println("Daily Dispense Report generated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generatePatientAgeRangeReport(DoublyLinkedList<Patient> patientList) {
        try {
            // Step 1: Filter patients by today's date
            LocalDate today = LocalDate.now();
            Map<String, Integer> ageRangeCounts = new HashMap<>();
            ageRangeCounts.put("0-12", 0);
            ageRangeCounts.put("13-19", 0);
            ageRangeCounts.put("20-35", 0);
            ageRangeCounts.put("36-50", 0);
            ageRangeCounts.put("51+", 0);

            for (Patient p : patientList) {
                // Assuming dateOfRegistration is stored as String (yyyy-MM-dd)
                LocalDate regDate = LocalDate.parse(p.getDateOfRegistration());
                if (regDate.equals(today)) {
                    int age = p.getPatientAge();
                    if (age <= 12) {
                        ageRangeCounts.put("0-12", ageRangeCounts.get("0-12") + 1);
                    } else if (age <= 19) {
                        ageRangeCounts.put("13-19", ageRangeCounts.get("13-19") + 1);
                    } else if (age <= 35) {
                        ageRangeCounts.put("20-35", ageRangeCounts.get("20-35") + 1);
                    } else if (age <= 50) {
                        ageRangeCounts.put("36-50", ageRangeCounts.get("36-50") + 1);
                    } else {
                        ageRangeCounts.put("51+", ageRangeCounts.get("51+") + 1);
                    }
                }
            }

            // Step 2: Create Pie Dataset
            DefaultPieDataset dataset = new DefaultPieDataset();
            for (Map.Entry<String, Integer> entry : ageRangeCounts.entrySet()) {
                if (entry.getValue() > 0) { // only include non-zero
                    dataset.setValue(entry.getKey(), entry.getValue());
                }
            }

            // Step 3: Generate Pie Chart
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Patient Age Range Report (" + today + ")",
                    dataset,
                    true, true, false
            );

            PiePlot plot = (PiePlot) pieChart.getPlot();
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));

            File chartFile = new File("patient_age_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, pieChart, 500, 350);

            // Step 4: Export PDF
            PdfWriter writer = new PdfWriter("Patient_AgeRange_Report.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Daily Patient Age Range Report").setFontSize(18));
            document.add(new Paragraph("Generated on: " + today).setFontSize(12));
            document.add(new Paragraph(" "));

            Image chartImage = new Image(ImageDataFactory.create("patient_age_chart.png"));
            document.add(chartImage);

            document.close();
            chartFile.delete();

            System.out.println("Patient Age Range Report generated successfully.");

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

        long averageWaitingTimeSeconds = (count > 0) ? totalWaitingTime / count : 0;

        // --- chart ---
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(averageWaitingTimeSeconds, "Waiting Times", "Average");
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

        // --- table ---
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2}))
                .setWidth(UnitValue.createPercentValue(100));
        table.addHeaderCell(new Cell().add(new Paragraph("Metric")));
        table.addHeaderCell(new Cell().add(new Paragraph("Value (minutes and seconds)")));

        table.addCell("Average Waiting Time");
        table.addCell(formatMinutesSeconds(averageWaitingTimeSeconds));

        table.addCell("Longest Waiting Time");
        table.addCell(formatMinutesSeconds(longestWaitingTime));

        document.add(new Paragraph(" "));
        document.add(table);

        document.close();
        chartFile.delete();

        System.out.println("Queue Statistics Report generated: " + fileName);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // Helper method to convert seconds to Xm Ys format
    private static String formatMinutesSeconds(long totalSeconds) {
    long minutes = totalSeconds / 60;
    long seconds = totalSeconds % 60;
    return minutes + "m " + seconds + "s";
    }


    /**
     * REPORT 1: Generates a PDF report showing the frequency of common diagnoses.
     * @param frequencyData A list of pairs, where each pair is (Diagnosis, Count).
     */
    public static void generateDiagnosisFrequencyReport(DoublyLinkedList<Pair<String, Integer>> frequencyData) {
        try {
            // --- 1. Create the Pie Chart ---
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            for (Pair<String, Integer> entry : frequencyData) {
                pieDataset.setValue(entry.getKey(), entry.getValue());
            }
            JFreeChart pieChart = ChartFactory.createPieChart("Common Diagnoses Distribution", pieDataset, true, true, false);

            PiePlot plot = (PiePlot) pieChart.getPlot();
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})")); // Key: Value (Percentage)

            File chartFile = new File("diagnosis_frequency_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, pieChart, 500, 400);

            // --- 2. Create the PDF ---
            PdfWriter writer = new PdfWriter("Common_Diagnoses_Report.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Common Diagnoses Report").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("This report shows the distribution of diagnoses recorded.").setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));
            
            Image chartImage = new Image(ImageDataFactory.create(chartFile.getAbsolutePath()));
            document.add(chartImage);

            document.close();
            chartFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * REPORT 2: Generates a PDF report detailing a single patient's medical history.
     * @param patientHistory A list of treatments for a single patient.
     */
    public static void generatePatientHistoryReport(DoublyLinkedList<Treatment> patientHistory) {
        if (patientHistory.isEmpty()) {
            return;
        }
        try {
            // Get patient details from the first treatment record
            Treatment firstTreatment = patientHistory.getFirst().getEntry();
            String patientName = firstTreatment.getConsultation().getPatient().getPatientName();
            String patientId = firstTreatment.getConsultation().getPatient().getPatientID();
            String fileName = "Patient_History_" + patientId + ".pdf";

            // --- 1. Create the PDF ---
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Patient Medical History Report").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Patient: " + patientName + " (ID: " + patientId + ")").setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            // --- 2. Create the Table ---
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 3, 2, 3}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Date")));
            table.addHeaderCell(new Cell().add(new Paragraph("Doctor")));
            table.addHeaderCell(new Cell().add(new Paragraph("Diagnosis")));
            table.addHeaderCell(new Cell().add(new Paragraph("Treatment Details")));
            table.addHeaderCell(new Cell().add(new Paragraph("Notes")));

            // --- 3. Populate the Table ---
            for (Treatment t : patientHistory) {
                table.addCell(t.getFormattedDateTime());
                table.addCell(t.getConsultation().getDoctor().getName());
                table.addCell(t.getDiagnosis());
                table.addCell(t.getTreatmentDetails());
                table.addCell(t.getNotes() != null ? t.getNotes() : "");
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * NEW REPORT: Generates a PDF report showing the popularity of different treatment types.
     * @param masterTreatmentList The complete list of all treatments.
     */
    public static void generateTreatmentTypePopularityReport(DoublyLinkedList<Pair<String, Treatment>> masterTreatmentList) {
        try {
            // --- 1. Aggregate Data by Treatment Type ---
            DoublyLinkedList<Pair<String, Integer>> treatmentTypeCounts = new DoublyLinkedList<>();

            for (Pair<String, Treatment> pair : masterTreatmentList) {
                Treatment t = pair.getValue();
                String treatmentType = t.getTreatmentDetails();
                boolean found = false;

                // Search for the treatment type in our counts list
                for (Pair<String, Integer> countPair : treatmentTypeCounts) {
                    if (countPair.getKey().equals(treatmentType)) {
                        countPair.setValue(countPair.getValue() + 1);
                        found = true;
                        break;
                    }
                }

                // If treatment type not found, add a new entry
                if (!found) {
                    treatmentTypeCounts.insertLast(new Pair<>(treatmentType, 1));
                }
            }

            // --- 2. Create Bar Chart ---
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Pair<String, Integer> entry : treatmentTypeCounts) {
                dataset.addValue(entry.getValue(), "Count", entry.getKey());
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                "Treatment Type Popularity",
                "Treatment Type",
                "Number of Patients",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
            );

            File chartFile = new File("treatment_type_popularity_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, barChart, 700, 400);

            // --- 3. Create PDF ---
            PdfWriter writer = new PdfWriter("Treatment_Type_Popularity_Report.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Treatment Type Popularity Report").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("This report shows the total number of patients for each treatment type.").setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));
            
            Image chartImage = new Image(ImageDataFactory.create(chartFile.getAbsolutePath()));
            document.add(chartImage);

            document.close();
            chartFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== CONSULTATION MANAGEMENT PDF REPORTS =====

    /**
     * Generates a PDF report for consultation statistics with charts.
     * @param consultationList The list of all consultations.
     * @param fromDate Start date for the report period.
     * @param toDate End date for the report period.
     */
    public static void generateConsultationStatisticsPDF(DoublyLinkedList<Pair<String, Consultation>> consultationList, 
                                                       LocalDateTime fromDate, LocalDateTime toDate) {
        try {
            // --- 1. Aggregate Data ---
            int totalConsultations = 0;
            int completedConsultations = 0;
            int scheduledConsultations = 0;
            int cancelledConsultations = 0;
            int inProgressConsultations = 0;

            for (Pair<String, Consultation> pair : consultationList) {
                Consultation consultation = pair.getValue();
                if (consultation.getConsultationDateTime() != null &&
                    !consultation.getConsultationDateTime().isBefore(fromDate) &&
                    !consultation.getConsultationDateTime().isAfter(toDate)) {
                    
                    totalConsultations++;
                    
                    switch (consultation.getStatus()) {
                        case "Completed":
                            completedConsultations++;
                            break;
                        case "Scheduled":
                            scheduledConsultations++;
                            break;
                        case "Cancelled":
                            cancelledConsultations++;
                            break;
                        case "In Progress":
                            inProgressConsultations++;
                            break;
                    }
                }
            }

            // --- 2. Create Pie Chart ---
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            if (completedConsultations > 0) pieDataset.setValue("Completed", completedConsultations);
            if (scheduledConsultations > 0) pieDataset.setValue("Scheduled", scheduledConsultations);
            if (cancelledConsultations > 0) pieDataset.setValue("Cancelled", cancelledConsultations);
            if (inProgressConsultations > 0) pieDataset.setValue("In Progress", inProgressConsultations);

            JFreeChart pieChart = ChartFactory.createPieChart("Consultation Status Distribution", pieDataset, true, true, false);
            PiePlot plot = (PiePlot) pieChart.getPlot();
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));

            File chartFile = new File("consultation_statistics_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, pieChart, 500, 400);

            // --- 3. Create PDF ---
            String fileName = "Consultation_Statistics_Report.pdf";
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Consultation Statistics Report").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Period: " + fromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                                     " to " + toDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            // Add chart
            Image chartImage = new Image(ImageDataFactory.create(chartFile.getAbsolutePath()));
            document.add(chartImage);
            document.add(new Paragraph(" "));

            // Add summary table
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Metric")));
            table.addHeaderCell(new Cell().add(new Paragraph("Count")));

            table.addCell(new Cell().add(new Paragraph("Total Consultations")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(totalConsultations))));

            table.addCell(new Cell().add(new Paragraph("Completed")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(completedConsultations))));

            table.addCell(new Cell().add(new Paragraph("Scheduled")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(scheduledConsultations))));

            table.addCell(new Cell().add(new Paragraph("In Progress")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(inProgressConsultations))));

            table.addCell(new Cell().add(new Paragraph("Cancelled")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(cancelledConsultations))));

            if (totalConsultations > 0) {
                double completionRate = (double) completedConsultations / totalConsultations * 100;
                table.addCell(new Cell().add(new Paragraph("Completion Rate")));
                table.addCell(new Cell().add(new Paragraph(String.format("%.1f%%", completionRate))));
            }

            document.add(table);
            document.close();
            chartFile.delete();

            System.out.println("Consultation Statistics Report generated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a PDF report for appointment summary with charts.
     * @param appointmentList The list of all appointments.
     * @param fromDate Start date for the report period.
     * @param toDate End date for the report period.
     */
    public static void generateAppointmentSummaryPDF(DoublyLinkedList<Pair<String, Appointment>> appointmentList,
                                                   LocalDateTime fromDate, LocalDateTime toDate) {
        try {
            // --- 1. Aggregate Data ---
            int totalAppointments = 0;
            int confirmedAppointments = 0;
            int completedAppointments = 0;
            int cancelledAppointments = 0;
            int noShowAppointments = 0;

            for (Pair<String, Appointment> pair : appointmentList) {
                Appointment appointment = pair.getValue();
                if (appointment.getAppointmentDateTime() != null &&
                    !appointment.getAppointmentDateTime().isBefore(fromDate) &&
                    !appointment.getAppointmentDateTime().isAfter(toDate)) {
                    
                    totalAppointments++;
                    
                    switch (appointment.getStatus()) {
                        case "Confirmed":
                            confirmedAppointments++;
                            break;
                        case "Completed":
                            completedAppointments++;
                            break;
                        case "Cancelled":
                            cancelledAppointments++;
                            break;
                        case "No-show":
                            noShowAppointments++;
                            break;
                    }
                }
            }

            // --- 2. Create Bar Chart ---
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            if (confirmedAppointments > 0) dataset.addValue(confirmedAppointments, "Count", "Confirmed");
            if (completedAppointments > 0) dataset.addValue(completedAppointments, "Count", "Completed");
            if (cancelledAppointments > 0) dataset.addValue(cancelledAppointments, "Count", "Cancelled");
            if (noShowAppointments > 0) dataset.addValue(noShowAppointments, "Count", "No-show");

            JFreeChart barChart = ChartFactory.createBarChart(
                "Appointment Status Distribution",
                "Status",
                "Number of Appointments",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
            );

            File chartFile = new File("appointment_summary_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, barChart, 600, 400);

            // --- 3. Create PDF ---
            String fileName = "Appointment_Summary_Report.pdf";
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Appointment Summary Report").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Period: " + fromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                                     " to " + toDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            // Add chart
            Image chartImage = new Image(ImageDataFactory.create(chartFile.getAbsolutePath()));
            document.add(chartImage);
            document.add(new Paragraph(" "));

            // Add summary table
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Metric")));
            table.addHeaderCell(new Cell().add(new Paragraph("Count")));

            table.addCell(new Cell().add(new Paragraph("Total Appointments")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(totalAppointments))));

            table.addCell(new Cell().add(new Paragraph("Confirmed")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(confirmedAppointments))));

            table.addCell(new Cell().add(new Paragraph("Completed")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(completedAppointments))));

            table.addCell(new Cell().add(new Paragraph("Cancelled")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(cancelledAppointments))));

            table.addCell(new Cell().add(new Paragraph("No-show")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(noShowAppointments))));

            if (totalAppointments > 0) {
                double attendanceRate = (double) completedAppointments / totalAppointments * 100;
                table.addCell(new Cell().add(new Paragraph("Attendance Rate")));
                table.addCell(new Cell().add(new Paragraph(String.format("%.1f%%", attendanceRate))));
            }

            document.add(table);
            document.close();
            chartFile.delete();

            System.out.println("Appointment Summary Report generated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a PDF report for doctor performance analysis.
     * @param consultationList The list of all consultations.
     * @param fromDate Start date for the report period.
     * @param toDate End date for the report period.
     */
    public static void generateDoctorPerformancePDF(DoublyLinkedList<Pair<String, Consultation>> consultationList,
                                                  LocalDateTime fromDate, LocalDateTime toDate) {
        try {
            // --- 1. Aggregate Data by Doctor ---
            java.util.Map<String, Integer> doctorConsultations = new java.util.HashMap<>();
            java.util.Map<String, Integer> doctorCompleted = new java.util.HashMap<>();

            for (Pair<String, Consultation> pair : consultationList) {
                Consultation consultation = pair.getValue();
                if (consultation.getConsultationDateTime() != null &&
                    !consultation.getConsultationDateTime().isBefore(fromDate) &&
                    !consultation.getConsultationDateTime().isAfter(toDate) &&
                    consultation.getDoctor() != null) {
                    
                    String doctorName = consultation.getDoctor().getName();
                    doctorConsultations.put(doctorName, doctorConsultations.getOrDefault(doctorName, 0) + 1);
                    
                    if ("Completed".equals(consultation.getStatus())) {
                        doctorCompleted.put(doctorName, doctorCompleted.getOrDefault(doctorName, 0) + 1);
                    }
                }
            }

            // --- 2. Create Bar Chart ---
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (String doctorName : doctorConsultations.keySet()) {
                int total = doctorConsultations.get(doctorName);
                int completed = doctorCompleted.getOrDefault(doctorName, 0);
                double completionRate = total > 0 ? (double) completed / total * 100 : 0;
                dataset.addValue(completionRate, "Completion Rate (%)", doctorName);
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                "Doctor Performance - Completion Rates",
                "Doctor",
                "Completion Rate (%)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
            );

            File chartFile = new File("doctor_performance_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, barChart, 700, 400);

            // --- 3. Create PDF ---
            String fileName = "Doctor_Performance_Report.pdf";
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Doctor Performance Report").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Period: " + fromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                                     " to " + toDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            // Add chart
            Image chartImage = new Image(ImageDataFactory.create(chartFile.getAbsolutePath()));
            document.add(chartImage);
            document.add(new Paragraph(" "));

            // Add detailed table
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Doctor")));
            table.addHeaderCell(new Cell().add(new Paragraph("Total")));
            table.addHeaderCell(new Cell().add(new Paragraph("Completed")));
            table.addHeaderCell(new Cell().add(new Paragraph("Rate (%)")));

            for (String doctorName : doctorConsultations.keySet()) {
                int total = doctorConsultations.get(doctorName);
                int completed = doctorCompleted.getOrDefault(doctorName, 0);
                double completionRate = total > 0 ? (double) completed / total * 100 : 0;

                table.addCell(new Cell().add(new Paragraph(doctorName)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(total))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(completed))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.1f", completionRate))));
            }

            document.add(table);
            document.close();
            chartFile.delete();

            System.out.println("Doctor Performance Report generated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a PDF report for monthly consultation summary.
     * @param consultationList The list of all consultations.
     */
    public static void generateMonthlyConsultationPDF(DoublyLinkedList<Pair<String, Consultation>> consultationList) {
        try {
            // --- 1. Aggregate Data by Month ---
            java.util.Map<String, Integer> monthlyConsultations = new java.util.HashMap<>();

            for (Pair<String, Consultation> pair : consultationList) {
                Consultation consultation = pair.getValue();
                if (consultation.getConsultationDateTime() != null) {
                    String monthYear = consultation.getConsultationDateTime().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                    monthlyConsultations.put(monthYear, monthlyConsultations.getOrDefault(monthYear, 0) + 1);
                }
            }

            // --- 2. Create Bar Chart ---
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (String monthYear : monthlyConsultations.keySet()) {
                dataset.addValue(monthlyConsultations.get(monthYear), "Consultations", monthYear);
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                "Monthly Consultation Summary",
                "Month",
                "Number of Consultations",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
            );

            File chartFile = new File("monthly_consultation_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, barChart, 700, 400);

            // --- 3. Create PDF ---
            String fileName = "Monthly_Consultation_Summary.pdf";
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Monthly Consultation Summary Report").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            // Add chart
            Image chartImage = new Image(ImageDataFactory.create(chartFile.getAbsolutePath()));
            document.add(chartImage);
            document.add(new Paragraph(" "));

            // Add summary table
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Month")));
            table.addHeaderCell(new Cell().add(new Paragraph("Consultations")));

            for (String monthYear : monthlyConsultations.keySet()) {
                table.addCell(new Cell().add(new Paragraph(monthYear)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(monthlyConsultations.get(monthYear)))));
            }

            document.add(table);
            document.close();
            chartFile.delete();

            System.out.println("Monthly Consultation Summary Report generated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
