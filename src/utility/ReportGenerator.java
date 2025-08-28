package utility;

import adt.DoublyLinkedList;
import adt.Pair;
import adt.MapInterface;
import adt.ListMap;
import enitity.Doctor;
import enitity.Treatment;
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

    /**
     * REPORT 1: Generates a PDF report showing the frequency of common
     * diagnoses.
     *
     * @param frequencyData A list of pairs, where each pair is (Diagnosis,
     * Count).
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
     * REPORT 2: Generates a PDF report detailing a single patient's medical
     * history.
     *
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
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 3, 1, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Date")));
            table.addHeaderCell(new Cell().add(new Paragraph("Doctor")));
            table.addHeaderCell(new Cell().add(new Paragraph("Diagnosis")));
            table.addHeaderCell(new Cell().add(new Paragraph("Cost (RM)")));
            table.addHeaderCell(new Cell().add(new Paragraph("Treatment Details")));

            // --- 3. Populate the Table ---
            for (Treatment t : patientHistory) {
                table.addCell(t.getFormattedDateTime());
                table.addCell(t.getConsultation().getDoctor().getName());
                table.addCell(t.getDiagnosis());
                table.addCell(String.format("%.2f", t.getCost()));
                table.addCell(t.getTreatmentDetails());
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * NEW REPORT: Generates a PDF report analyzing treatment costs by doctor.
     *
     * @param masterTreatmentList The complete list of all treatments.
     */
    public static void generateTreatmentCostReport(DoublyLinkedList<Pair<String, Treatment>> masterTreatmentList) {
        try {
            // --- 1. Aggregate Data using a Map ---
            MapInterface<String, Double> doctorRevenue = new ListMap<>();
            for (Pair<String, Treatment> pair : masterTreatmentList) {
                Treatment t = pair.getValue();
                String doctorName = t.getConsultation().getDoctor().getName();
                double currentRevenue = doctorRevenue.getValue(doctorName) == null ? 0.0 : doctorRevenue.getValue(doctorName);
                doctorRevenue.add(doctorName, currentRevenue + t.getCost());
            }

            // --- 2. Create Bar Chart ---
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            DoublyLinkedList<Pair<String, Double>> revenueList = ((ListMap<String, Double>) doctorRevenue).getPairList();
            for (Pair<String, Double> entry : revenueList) {
                dataset.addValue(entry.getValue(), "Revenue", entry.getKey());
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Treatment Revenue by Doctor",
                    "Doctor",
                    "Total Cost (RM)",
                    dataset
            );

            File chartFile = new File("treatment_cost_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, barChart, 600, 400);

            // --- 3. Create PDF ---
            PdfWriter writer = new PdfWriter("Treatment_Cost_Analysis_Report.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Treatment Cost Analysis Report").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            Image chartImage = new Image(ImageDataFactory.create(chartFile.getAbsolutePath()));
            document.add(chartImage);

            document.close();
            chartFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
