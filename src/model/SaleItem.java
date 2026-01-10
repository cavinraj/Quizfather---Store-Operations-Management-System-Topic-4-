package src.model;

public class SaleItem {
    private String modelName;
    private int quantity;
    private double unitPrice;

    public SaleItem(String modelName, int quantity, double unitPrice) {
        this.modelName = modelName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getModelName() { return modelName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return unitPrice * quantity; }

    public void setModelName(String modelName) {this.modelName = modelName;}
    public void setQuantity(int quantity) {this.quantity = quantity;}
    
    @Override
    public String toString() {
        return modelName + ":" + quantity + ":" + unitPrice;
    }
    
    // for stored in CSV
    public static SaleItem fromString(String str) {
        String[] parts = str.split(":");
        if (parts.length == 3) {
            return new SaleItem(parts[0], Integer.parseInt(parts[1]), Double.parseDouble(parts[2]));
        }
        return null;
    }
}
