package src;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Sale {
    private LocalDateTime dateTime;
    private String customerName;
    private String employeeId;
    private String paymentMethod;
    private ArrayList<SaleItem> items;
    private double totalAmount;

    public Sale(LocalDateTime dateTime, String customerName, String employeeId, String paymentMethod) {
        this.dateTime = dateTime;
        this.customerName = customerName;
        this.employeeId = employeeId;
        this.paymentMethod = paymentMethod;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    public void addItem(SaleItem item) {
        items.add(item);
        totalAmount += item.getSubtotal();
    }

    // Getters
    public LocalDateTime getDateTime() { return dateTime; }
    public String getCustomerName() { return customerName; }
    public String getEmployeeId() { return employeeId; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalAmount() { return totalAmount; }
    public ArrayList<SaleItem> getItems() { return items; }
    
    public void setTotalAmount(double amount) { this.totalAmount = amount; } // For loading from CSV
}