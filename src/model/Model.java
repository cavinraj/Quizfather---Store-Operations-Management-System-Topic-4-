package src.model;
import java.util.HashMap;
import java.util.Map;

public class Model {
    private String modelName;
    private double price;
    private Map<String, Integer> stockByOutlet;

    public Model(String modelName, double price) {
        this.modelName = modelName;
        this.price = price;
        this.stockByOutlet = new HashMap<>();
    }

    public String getModelName() { return modelName; }
    public double getPrice() { return price; }
    
    public int getStockForOutlet(String outletId) {
        return stockByOutlet.getOrDefault(outletId, 0);
    }

    public void setStockForOutlet(String outletId, int quantity) {
        stockByOutlet.put(outletId, quantity);
    }
    
    public Map<String, Integer> getStockMap() {
        return stockByOutlet;
    }
}