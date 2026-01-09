package src.utils;
import java.io.*;
import java.util.*;

import src.model.Sale;
import src.model.SaleItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SalesDataHandler {
    private static final String SALES_FILE = "data/sales.csv";
    private static final DateTimeFormatter DATES_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Save a sale record to CSV
    public static void saveSale(Sale sale) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SALES_FILE, true))) {
            //format = Date,Customer,Employee,PaymentMethod,Total,Item1|Item2|Item3
            StringBuilder sb = new StringBuilder();
            sb.append(sale.getDateTime().format(DATES_FORMAT)).append(",");
            sb.append(sale.getCustomerName()).append(",");
            sb.append(sale.getEmployeeId()).append(",");
            sb.append(sale.getPaymentMethod()).append(",");
            sb.append(sale.getTotalAmount()).append(",");
            
            // serialize items with a separator
            for (int i = 0; i < sale.getItems().size(); i++) {
                sb.append(sale.getItems().get(i).toString());
                if (i < sale.getItems().size() - 1) sb.append("|");
            }
            pw.println(sb.toString());
        } catch (IOException e) {
            System.out.println("Error saving sale: " + e.getMessage());
        }
    }

    // load all sales for History/Filtering
    public static ArrayList<Sale> loadSales() {
        ArrayList<Sale> sales = new ArrayList<>();
        File file = new File(SALES_FILE);
        if (!file.exists()) return sales;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    LocalDateTime dt = LocalDateTime.parse(parts[0], DATES_FORMAT);
                    Sale sale = new Sale(dt, parts[1], parts[2], parts[3]);
                    sale.setTotalAmount(Double.parseDouble(parts[4]));

                    // Parse Items
                    String[] itemStrings = parts[5].split("\\|");
                    for (String is : itemStrings) {
                        SaleItem item = SaleItem.fromString(is);
                        if (item != null) sale.getItems().add(item);
                    }
                    sales.add(sale);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading sales: " + e.getMessage());
        }
        return sales;
    }

    // Generate Text Receipt
    public static void generateReceipt(Sale sale) {
        String dateStr = sale.getDateTime().toLocalDate().toString();
        String filename = "data/sales_" + dateStr + ".txt";
        
        try (FileWriter fw = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("=== SALES RECEIPT ===");
            pw.println("Date: " + sale.getDateTime().toLocalDate());
            pw.println("Time: " + sale.getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            pw.println("Customer: " + sale.getCustomerName());
            pw.println("Employee: " + sale.getEmployeeId());
            pw.println("---------------------------------");
            pw.println(String.format("%-15s %-5s %-10s", "Model", "Qty", "Price"));
            for (SaleItem item : sale.getItems()) {
                pw.println(String.format("%-15s %-5d RM%.2f", 
                    item.getModelName(), item.getQuantity(), item.getSubtotal()));
            }
            pw.println("---------------------------------");
            pw.println("Total: RM" + String.format("%.2f", sale.getTotalAmount()));
            pw.println("Payment: " + sale.getPaymentMethod());
            pw.println("=================================\n");
            
        } catch (IOException e) {
            System.out.println("Error writing receipt: " + e.getMessage());
        }
    }
}