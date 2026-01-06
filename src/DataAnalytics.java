package src;
import java.io.*;
import java.util.*;

public class DataAnalytics {

    static final String FILE = "sales.txt";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Enter Sales");
        System.out.println("2. Run Analytics");
        System.out.print("Choose option: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            enterSales(sc);
        } else if (choice == 2) {
            runAnalytics();
        } else {
            System.out.println("Invalid option");
        }
    }

    // -------- SALES ENTRY --------
    static void enterSales(Scanner sc) {
        try (FileWriter fw = new FileWriter(FILE, true)) {

            System.out.print("Date (YYYY-MM-DD): ");
            String date = sc.nextLine();

            System.out.print("Model: ");
            String model = sc.nextLine();

            System.out.print("Quantity: ");
            int qty = sc.nextInt();

            System.out.print("Total Price (RM): ");
            double price = sc.nextDouble();

            fw.write(date + "," + model + "," + qty + "," + price + "\n");
            System.out.println("Sales saved.");

        } catch (IOException e) {
            System.out.println("Error saving sales.");
        }
    }

    // -------- DATA ANALYTICS --------
    static void runAnalytics() {

        double totalSales = 0;
        int totalQty = 0;

        String topModel = "";
        int maxQty = 0;

        HashMap<String, Integer> modelCount = new HashMap<>();

        try (Scanner sc = new Scanner(new File(FILE))) {

            while (sc.hasNextLine()) {
                String[] d = sc.nextLine().split(",");

                String model = d[1];
                int qty = Integer.parseInt(d[2]);
                double price = Double.parseDouble(d[3]);

                totalSales += price;
                totalQty += qty;

                modelCount.put(model,
                        modelCount.getOrDefault(model, 0) + qty);
            }

        } catch (Exception e) {
            System.out.println("No sales records found.");
            return;
        }

        for (String m : modelCount.keySet()) {
            if (modelCount.get(m) > maxQty) {
                maxQty = modelCount.get(m);
                topModel = m;
            }
        }

        System.out.println("\n--- DATA ANALYTICS ---");
        System.out.println("Total Sales: RM " + totalSales);
        System.out.println("Total Quantity Sold: " + totalQty);
        System.out.println("Most Sold Model: " + topModel);
    }
}