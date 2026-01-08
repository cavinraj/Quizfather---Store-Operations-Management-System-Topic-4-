package src;

import java.util.*;

public class DataAnalytics {

    public static String runAnalytics() {

        ArrayList<Sale> sales = SalesDataHandler.loadSales();

        if (sales == null || sales.isEmpty()) {
            return "No sales records found.";
        }

        double totalSales = 0;
        int totalQty = 0;
        HashMap<String, Integer> modelCount = new HashMap<>();

        for (Sale sale : sales) {
            totalSales += sale.getTotalAmount();

            for (SaleItem item : sale.getItems()) {
                totalQty += item.getQuantity();
                String model = item.getModelName();
                modelCount.put(model, modelCount.getOrDefault(model, 0) + item.getQuantity());
            }
        }

        String bestModel = "";
        int maxQty = 0;

        for (String model : modelCount.keySet()) {
            if (modelCount.get(model) > maxQty) {
                maxQty = modelCount.get(model);
                bestModel = model;
            }
        }

        return
            "DATA ANALYTICS SUMMARY\n\n" +
            "Total Sales: RM " + String.format("%.2f", totalSales) + "\n" +
            "Total Quantity Sold: " + totalQty + "\n" +
            "Most Sold Model: " + bestModel + " (" + maxQty + " units)";
    }
}
