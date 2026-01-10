package src.model;
import java.util.HashMap;
import java.util.Map;

// blueprint for model csv
public class Model {
    private String modelName;
    private double price;
    private Map<String, Integer> stockByOutlet; // uses hash for faster access compared to arraylist
    // it says Integer not int because map cannot use primitive data types


    public Model(String modelName, double price) {
        this.modelName = modelName;
        this.price = price;
        this.stockByOutlet = new HashMap<>();
    }

    public String getModelName() { return modelName; }
    public double getPrice() { return price; }
    
    public int getStockForOutlet(String outletId) {
        return stockByOutlet.getOrDefault(outletId, 0); // get stock, default to 0 if not found
    }

    public void setStockForOutlet(String outletId, int quantity) {
        stockByOutlet.put(outletId, quantity); // .put writes an entry into the map
    }
    
    public Map<String, Integer> getStockMap() {
        return stockByOutlet;
    }
}