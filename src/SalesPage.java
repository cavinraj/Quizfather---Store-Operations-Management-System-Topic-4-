package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SalesPage extends JFrame implements ActionListener {
    private JTextField customerField, qtyField;
    private JComboBox<String> modelBox, paymentBox;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JButton addButton, checkoutButton, cancelButton;

    private ArrayList<Model> availableModels;
    private Sale currentSale;
    private String currentOutletId;

    public SalesPage() {
        setTitle("Record New Sale");
        setSize(600, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Determine Outlet ID
        if (Session.current_user != null) {
            String empId = Session.current_user.get_employee_id();
            currentOutletId = (empId.length() >= 3) ? empId.substring(0, 3) : "C60";
            // Initialize Sale Object
            currentSale = new Sale(LocalDateTime.now(), "", empId, "");
        } else {
            currentOutletId = "C60";
            currentSale = new Sale(LocalDateTime.now(), "", "Guest", "");
        }

        // --- UI COMPONENTS ---

        // Customer Details
        JLabel custLbl = new JLabel("Customer Name:");
        custLbl.setBounds(30, 20, 120, 25);
        add(custLbl);
        customerField = new JTextField();
        customerField.setBounds(140, 20, 200, 25);
        add(customerField);

        // Payment Method [cite: 128]
        JLabel payLbl = new JLabel("Payment Method:");
        payLbl.setBounds(30, 50, 120, 25);
        add(payLbl);
        String[] methods = {"Cash", "Credit Card", "Debit Card", "E-Wallet"};
        paymentBox = new JComboBox<>(methods);
        paymentBox.setBounds(140, 50, 200, 25);
        add(paymentBox);

        // Item Selection
        JSeparator sep = new JSeparator();
        sep.setBounds(20, 90, 540, 10);
        add(sep);

        JLabel itemLbl = new JLabel("Select Model:");
        itemLbl.setBounds(30, 110, 100, 25);
        add(itemLbl);

        availableModels = StockDataHandler.loadModels();
        String[] modelNames = new String[availableModels.size()];
        for(int i=0; i<availableModels.size(); i++) modelNames[i] = availableModels.get(i).getModelName();
        
        modelBox = new JComboBox<>(modelNames);
        modelBox.setBounds(120, 110, 150, 25);
        add(modelBox);

        JLabel qtyLbl = new JLabel("Qty:");
        qtyLbl.setBounds(280, 110, 30, 25);
        add(qtyLbl);
        qtyField = new JTextField();
        qtyField.setBounds(310, 110, 50, 25);
        add(qtyField);

        addButton = new JButton("Add to Cart");
        addButton.setBounds(380, 110, 120, 25);
        addButton.addActionListener(this);
        add(addButton);

        // Cart Table
        String[] columns = {"Model", "Unit Price", "Qty", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0);
        cartTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.setBounds(30, 150, 520, 200);
        add(scroll);

        // Total and Actions
        totalLabel = new JLabel("Total: RM 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setBounds(400, 360, 150, 30);
        add(totalLabel);

        checkoutButton = new JButton("Complete Sale");
        checkoutButton.setBounds(350, 400, 150, 40);
        checkoutButton.addActionListener(this);
        add(checkoutButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(200, 400, 100, 40);
        cancelButton.addActionListener(this);
        add(cancelButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            this.dispose();
        } 
        else if (e.getSource() == addButton) {
            addToCart();
        } 
        else if (e.getSource() == checkoutButton) {
            completeSale();
        }
    }

    private void addToCart() {
        String selectedModelName = (String) modelBox.getSelectedItem();
        String qtyText = qtyField.getText();

        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) throw new NumberFormatException();

            // Find Model and Check Stock [cite: 149]
            Model selectedModel = null;
            for (Model m : availableModels) {
                if (m.getModelName().equals(selectedModelName)) {
                    selectedModel = m;
                    break;
                }
            }

            if (selectedModel != null) {
                int currentStock = selectedModel.getStockForOutlet(currentOutletId);
                // Check if we already have this item in cart to prevent over-selling
                int cartQty = 0;
                for(SaleItem item : currentSale.getItems()) {
                    if(item.getModelName().equals(selectedModelName)) cartQty += item.getQuantity();
                }

                if ((cartQty + qty) > currentStock) {
                    JOptionPane.showMessageDialog(this, "Insufficient Stock! Available: " + currentStock);
                    return;
                }

                SaleItem newItem = new SaleItem(selectedModelName, qty, selectedModel.getPrice());
                currentSale.addItem(newItem);
                
                // Update Table
                tableModel.addRow(new Object[]{
                    selectedModelName, 
                    selectedModel.getPrice(), 
                    qty, 
                    newItem.getSubtotal()
                });
                
                // Update Total
                totalLabel.setText("Total: RM " + String.format("%.2f", currentSale.getTotalAmount()));
                qtyField.setText("");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Quantity");
        }
    }

    private void completeSale() {
        String customer = customerField.getText().trim();
        if (customer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Customer Name.");
            return;
        }
        if (currentSale.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        // Finalize Sale Object
        // We create a fresh object to ensure the timestamp is the exact moment of checkout
        Sale finalSale = new Sale(LocalDateTime.now(), customer, Session.current_user.get_employee_id(), (String)paymentBox.getSelectedItem());
        for(SaleItem item : currentSale.getItems()) finalSale.addItem(item); // Copy items

        // 1. Update Stock in Memory & CSV [cite: 129]
        for (SaleItem item : finalSale.getItems()) {
            for (Model m : availableModels) {
                if (m.getModelName().equals(item.getModelName())) {
                    int currentStock = m.getStockForOutlet(currentOutletId);
                    m.setStockForOutlet(currentOutletId, currentStock - item.getQuantity());
                }
            }
        }
        StockDataHandler.saveModels(availableModels);

        // 2. Save Sale Record [cite: 129]
        SalesDataHandler.saveSale(finalSale);

        // 3. Generate Receipt [cite: 130]
        SalesDataHandler.generateReceipt(finalSale);

        JOptionPane.showMessageDialog(this, "Sale Recorded Successfully!\nReceipt generated.");
        this.dispose();
    }
}