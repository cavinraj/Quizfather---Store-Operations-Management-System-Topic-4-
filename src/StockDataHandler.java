package src;
import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class StockDataHandler {
    private static final String MODEL_FILE = "data/model.csv";
    private static final String OUTLET_FILE = "data/outlet.csv";

    // OUTLET OPERATIONS
    public static ArrayList<Outlet> loadOutlets() {
        ArrayList<Outlet> outlets = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileInputStream(OUTLET_FILE))) {
            if(scanner.hasNextLine()) scanner.nextLine(); // Skip header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    outlets.add(new Outlet(parts[0].trim(), parts[1].trim()));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Outlet file not found.");
        }
        return outlets;
    }

    // MODEL/STOCK OPERATIONS
    // Reads model.csv and maps columns dynamically to outlets
    public static ArrayList<Model> loadModels() {
        ArrayList<Model> models = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileInputStream(MODEL_FILE))) {
            if (!scanner.hasNextLine()) return models;

            // Parse Header to find which column belongs to which outlet
            String headerLine = scanner.nextLine();
            String[] headers = headerLine.split(",");
            // headers[0]=Model, headers[1]=Price, headers[2...]=OutletCodes
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String name = parts[0];
                double price = Double.parseDouble(parts[1]);
                Model model = new Model(name, price);

                // Loop through the rest of the columns to get stock for each outlet
                for (int i = 2; i < parts.length && i < headers.length; i++) {
                    String outletCode = headers[i].trim();
                    int qty = Integer.parseInt(parts[i]);
                    model.setStockForOutlet(outletCode, qty);
                }
                models.add(model);
            }
        } catch (Exception e) {
            System.out.println("Error loading models: " + e.getMessage());
        }
        return models;
    }

    // Saves the current state of models back to CSV
    public static void saveModels(ArrayList<Model> models) {
        // We need the header list to write columns in correct order. 
        // We'll fetch outlet codes from the first model or reload outlets.
        String[] outletOrder = {"C60","C61","C62","C63","C64","C65","C66","C67","C68","C69"};

        try (PrintWriter pw = new PrintWriter(new FileWriter(MODEL_FILE))) {
            // Write Header
            StringBuilder header = new StringBuilder("Model,Price");
            for (String out : outletOrder) header.append(",").append(out);
            pw.println(header);

            // Write Rows
            for (Model m : models) {
                StringBuilder row = new StringBuilder();
                row.append(m.getModelName()).append(",").append((int)m.getPrice());
                for (String out : outletOrder) {
                    row.append(",").append(m.getStockForOutlet(out));
                }
                pw.println(row);
            }
        } catch (IOException e) {
            System.out.println("Error saving models: " + e.getMessage());
        }
    }

    // RECEIPT GENERATION
    public static void appendReceipt(String content) {
        String filename = "data/receipts_" + LocalDate.now() + ".txt";
        try (FileWriter fw = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(content);
            pw.println("--------------------------------------------------");
        } catch (IOException e) {
            System.out.println("Error writing receipt: " + e.getMessage());
        }
    }
}