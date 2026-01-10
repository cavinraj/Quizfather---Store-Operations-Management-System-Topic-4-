package src.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import src.model.Sale;
import src.model.SaleItem;
import src.model.Model;
import src.utils.SalesDataHandler;
import src.utils.StockDataHandler;
import src.utils.Session;

public class SalesEditPage extends JFrame {
    private JTextField dateSearchField, customerSearchField, editNameField, editTotalField;
    private JComboBox<String> editMethodBox;
    private DefaultListModel<String> itemsListModel;
    private JList<String> itemsList;
    private JButton searchButton, updateButton, editButton;
    private Sale foundSale = null;
    private String currentOutletId;

    public SalesEditPage() {
        setTitle("Edit Sales & Inventory Synchronization");
        setSize(650, 700);
        setLayout(new BorderLayout(10, 10));

        if (Session.current_user != null) { // this method will get the current user's outlet code
            String empId = Session.current_user.get_employee_id();
                if (empId.length() >= 3) {
                    currentOutletId = empId.substring(0, 3);
                } else {
                    currentOutletId = "C60";
                }
        } else {
            currentOutletId = "C60";
        }

        // search sales records by date or customer name
        JPanel searchPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Transaction History"));
        
        searchPanel.add(new JLabel(" Enter Transaction Date (yyyy-mm-dd):"));
        dateSearchField = new JTextField();
        searchPanel.add(dateSearchField);
        
        searchPanel.add(new JLabel("Enter Customer Name:"));
        customerSearchField = new JTextField();
        searchPanel.add(customerSearchField);
        
        searchButton = new JButton("Search Sales Record");
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // section to display and edit items in the sale
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Items in Transaction (Select to Edit)"));
        itemsListModel = new DefaultListModel<>();
        itemsList = new JList<>(itemsListModel);
        centerPanel.add(new JScrollPane(itemsList), BorderLayout.CENTER);

        editButton = new JButton("Edit Selected Item (Model/Qty)");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editSelectedItem());
        centerPanel.add(editButton, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // editable part for customer name, total amount, payment method
        JPanel bottomPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        bottomPanel.add(new JLabel(" 1. Customer Name:"));
        editNameField = new JTextField();
        bottomPanel.add(editNameField);

        bottomPanel.add(new JLabel(" 4. Total Amount (RM):"));
        editTotalField = new JTextField();
        bottomPanel.add(editTotalField);

        bottomPanel.add(new JLabel(" 5. Transaction Method:"));
        String[] methods = {"Cash", "Credit card", "E-wallet", "Debit card"};
        editMethodBox = new JComboBox<>(methods);
        bottomPanel.add(editMethodBox);

        updateButton = new JButton("Confirm All Changes & Sync CSV");
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> performUpdate());
        bottomPanel.add(new JLabel("")); 
        bottomPanel.add(updateButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void performSearch() {
        ArrayList<Sale> salesList = SalesDataHandler.loadSales();
        String date = dateSearchField.getText().trim();
        String customer = customerSearchField.getText().trim();

        for (Sale s : salesList) {
            if (s.getDateTime().toLocalDate().toString().equals(date) && 
                s.getCustomerName().equalsIgnoreCase(customer)) {
                
                foundSale = s;
                refreshFields();
                updateButton.setEnabled(true);
                editButton.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Sales Record Found!");
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Sales Record Not Found.");
    }

    private void refreshFields() {
        editNameField.setText(foundSale.getCustomerName());
        editTotalField.setText(String.format("%.2f", foundSale.getTotalAmount()));
        editMethodBox.setSelectedItem(foundSale.getPaymentMethod());
        
        itemsListModel.clear();
        for (SaleItem item : foundSale.getItems()) {
            itemsListModel.addElement(item.getModelName() + " | Qty: " + item.getQuantity());
        }
    }

    private void editSelectedItem() {
        int index = itemsList.getSelectedIndex();
        if (index == -1) {
            return;
        }

        SaleItem selectedItem = foundSale.getItems().get(index);
        String oldModel = selectedItem.getModelName();
        int oldQuantity = selectedItem.getQuantity();

        // get new model and verify whether it exists or not
        String newModel = JOptionPane.showInputDialog(this, "Update Model Name:", oldModel);
        if (newModel == null || !doesModelExist(newModel)) {
            JOptionPane.showMessageDialog(this, "Model does not exist in stock records.");
            return;
        }

        // get new quantity and verify whether it's in stock or not
        String newQuantity = JOptionPane.showInputDialog(this, "Update Quantity:", oldQuantity);
        if (newQuantity == null) return;

        try {
            int newQty = Integer.parseInt(newQuantity);
            if (newQty < 0) throw new NumberFormatException();

            // make sure stock is sufficient after considering reversal of old qty
            ArrayList<Model> inventory = StockDataHandler.loadModels();
            Model targetModel = null;
            for (Model m : inventory) {
                if (m.getModelName().equalsIgnoreCase(newModel)) {
                    targetModel = m;
                    break;
                }
            }

            if (targetModel != null) {
                int currentInCSV = targetModel.getStockForOutlet(currentOutletId);
                int availableAfterReversal = newModel.equalsIgnoreCase(oldModel) ? currentInCSV + oldQuantity : currentInCSV;

                if (availableAfterReversal - newQty < 0) {
                    JOptionPane.showMessageDialog(this, "Insufficient stock! Resulting stock would be " + (availableAfterReversal - newQty));
                    return;
                }
            }

            for (Model m : inventory) {
                if (m.getModelName().equalsIgnoreCase(oldModel)) {
                    m.setStockForOutlet(currentOutletId, m.getStockForOutlet(currentOutletId) + oldQuantity);
                }
                if (m.getModelName().equalsIgnoreCase(newModel)) {
                    m.setStockForOutlet(currentOutletId, m.getStockForOutlet(currentOutletId) - newQty);
                }
            }
            StockDataHandler.saveModels(inventory); // modify data adjustment 

            // update selected item in sale
            selectedItem.setModelName(newModel);
            selectedItem.setQuantity(newQty);
            refreshFields();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity! Use positive numbers only.");
        }
    }

    private boolean doesModelExist(String modelName) {
        // search if model exists in stock records
        ArrayList<Model> validModels = StockDataHandler.loadModels();
        for (Model m : validModels) {
            if (m.getModelName().equalsIgnoreCase(modelName.trim())) return true;
        }
        return false;
    }

    private void performUpdate() {
        if (foundSale == null) return;

        // confirm before updating
        int confirm = JOptionPane.showConfirmDialog(this, "Confirm Update? (Y/N)", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                foundSale.setCustomerName(editNameField.getText().trim());
                foundSale.setTotalAmount(Double.parseDouble(editTotalField.getText().trim()));
                foundSale.setPaymentMethod((String) editMethodBox.getSelectedItem());

                // modify sales record in CSV 
                ArrayList<Sale> allSales = SalesDataHandler.loadSales();
                for (int i = 0; i < allSales.size(); i++) {
                    if (allSales.get(i).getDateTime().equals(foundSale.getDateTime()) && 
                        allSales.get(i).getCustomerName().equalsIgnoreCase(customerSearchField.getText().trim())) {
                        allSales.set(i, foundSale);
                        break;
                    }
                }

                SalesDataHandler.saveAllSales(allSales); 
                JOptionPane.showMessageDialog(this, "Records successfully updated.");
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error during update: " + ex.getMessage());
            }
        }
    }
}