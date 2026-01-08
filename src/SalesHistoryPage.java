package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class SalesHistoryPage extends JFrame {
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JTextField startDateField, endDateField;
    private JComboBox<String> sortBox;
    private JLabel totalSalesLabel;
    private ArrayList<Sale> allSales;

    public SalesHistoryPage() {
        setTitle("Sales History & Analytics");
        setSize(800, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header
        JLabel title = new JLabel("Sales History Filter");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(20, 10, 200, 30);
        add(title);

        // Filters
        JLabel startLbl = new JLabel("Start Date (YYYY-MM-DD):");
        startLbl.setBounds(20, 50, 160, 25);
        add(startLbl);
        startDateField = new JTextField();
        startDateField.setBounds(180, 50, 100, 25);
        add(startDateField);

        JLabel endLbl = new JLabel("End Date (YYYY-MM-DD):");
        endLbl.setBounds(300, 50, 160, 25);
        add(endLbl);
        endDateField = new JTextField();
        endDateField.setBounds(460, 50, 100, 25);
        add(endDateField);

        // Sort Options
        JLabel sortLbl = new JLabel("Sort By:");
        sortLbl.setBounds(580, 50, 60, 25);
        add(sortLbl);
        String[] sorts = {"Date (Newest)", "Date (Oldest)", "Amount (High-Low)", "Amount (Low-High)", "Customer (A-Z)"};
        sortBox = new JComboBox<>(sorts);
        sortBox.setBounds(640, 50, 130, 25);
        add(sortBox);

        JButton filterButton = new JButton("Apply Filter");
        filterButton.setBounds(640, 90, 130, 30);
        add(filterButton);

        // Table
        String[] columns = {"Date", "Time", "Customer", "Items", "Total (RM)", "Method"};
        tableModel = new DefaultTableModel(columns, 0);
        salesTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(salesTable);
        scroll.setBounds(20, 140, 750, 250);
        add(scroll);

        // Summary
        totalSalesLabel = new JLabel("Total Cumulative Sales: RM 0.00");
        totalSalesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalSalesLabel.setBounds(20, 410, 400, 30);
        add(totalSalesLabel);

        // Load Data
        allSales = SalesDataHandler.loadSales();
        updateTable(allSales); // Show all initially

        // Filter Action
        filterButton.addActionListener(e -> applyFilterAndSort());
        setVisible(true);
    }

    private void applyFilterAndSort() {
        String startStr = startDateField.getText().trim();
        String endStr = endDateField.getText().trim();
        ArrayList<Sale> filteredList = new ArrayList<>(allSales);

        // Filter by Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            if (!startStr.isEmpty()) {
                LocalDate start = LocalDate.parse(startStr, formatter);
                filteredList = (ArrayList<Sale>) filteredList.stream()
                        .filter(s -> !s.getDateTime().toLocalDate().isBefore(start))
                        .collect(Collectors.toList());
            }
            if (!endStr.isEmpty()) {
                LocalDate end = LocalDate.parse(endStr, formatter);
                filteredList = (ArrayList<Sale>) filteredList.stream()
                        .filter(s -> !s.getDateTime().toLocalDate().isAfter(end))
                        .collect(Collectors.toList());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format. Use YYYY-MM-DD");
            return;
        }

        // Sort
        String sortType = (String) sortBox.getSelectedItem();
        switch (sortType) {
            case "Date (Newest)":
                filteredList.sort((s1, s2) -> s2.getDateTime().compareTo(s1.getDateTime()));
                break;
            case "Date (Oldest)":
                filteredList.sort(Comparator.comparing(Sale::getDateTime));
                break;
            case "Amount (High-Low)":
                filteredList.sort((s1, s2) -> Double.compare(s2.getTotalAmount(), s1.getTotalAmount()));
                break;
            case "Amount (Low-High)":
                filteredList.sort(Comparator.comparingDouble(Sale::getTotalAmount));
                break;
            case "Customer (A-Z)":
                filteredList.sort(Comparator.comparing(Sale::getCustomerName));
                break;
        }

        updateTable(filteredList);
    }

    private void updateTable(ArrayList<Sale> sales) {
        tableModel.setRowCount(0);
        double total = 0;
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Sale s : sales) {
            // Summarize items for table display
            String itemSummary = s.getItems().size() + " items";
            if(s.getItems().size() == 1) itemSummary = s.getItems().get(0).getModelName();

            tableModel.addRow(new Object[]{
                s.getDateTime().toLocalDate(),
                s.getDateTime().format(timeFmt),
                s.getCustomerName(),
                itemSummary,
                String.format("%.2f", s.getTotalAmount()),
                s.getPaymentMethod()
            });
            total += s.getTotalAmount();
        }
        
        totalSalesLabel.setText("Total Cumulative Sales: RM " + String.format("%.2f", total));
    }
}