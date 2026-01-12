package src.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import src.model.Sale;
import src.model.SaleItem;
import src.model.Model;
import src.utils.SalesDataHandler;
import src.utils.StockDataHandler;
import src.utils.Session;

public class SalesEditPage extends JFrame implements ActionListener{
    private JTextField dateSearchField, customerSearchField, editNameField, editTotalField;
    private JComboBox<String> editMethodBox;
    private DefaultListModel<String> itemsListModel;
    private JList<String> itemsList;
    private JButton searchButton, updateButton, editButton;
    private Sale foundSale = null; 
    private String currentOutletId;

    public SalesEditPage() {
        setTitle("Sales Record Manager");
        setSize(650, 700);
        setLayout(new BorderLayout(10, 10));

        if (Session.current_user != null) {
            String empId = Session.current_user.get_employee_id();
            if (empId.length() >= 3) {
                currentOutletId = empId.substring(0, 3);
            } else {
                currentOutletId = "C60";
            }
        } else {
            currentOutletId = "C60";
        }

        JPanel searchPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));
        searchPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        dateSearchField = new JTextField();
        searchPanel.add(dateSearchField);
        
        searchPanel.add(new JLabel("Customer Name:"));
        customerSearchField = new JTextField();
        searchPanel.add(customerSearchField);
        
        searchButton = new JButton("Perform Search");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        itemsListModel = new DefaultListModel<>();
        itemsList = new JList<>(itemsListModel);
        centerPanel.add(new JScrollPane(itemsList), BorderLayout.CENTER);

        editButton = new JButton("Edit Highlighted Item");
        editButton.setEnabled(false);
        editButton.addActionListener(this);
        centerPanel.add(editButton, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        bottomPanel.add(new JLabel("Update Name:"));
        editNameField = new JTextField();
        bottomPanel.add(editNameField);

        bottomPanel.add(new JLabel("Update Amount:"));
        editTotalField = new JTextField();
        bottomPanel.add(editTotalField);

        bottomPanel.add(new JLabel("Payment Method:"));
        String[] methods = {"Cash", "Credit card", "E-wallet", "Debit card"};
        editMethodBox = new JComboBox<>(methods);
        bottomPanel.add(editMethodBox);

        updateButton = new JButton("Confirm & Save Changes");
        updateButton.setEnabled(false);
        updateButton.addActionListener(this);
        bottomPanel.add(new JLabel("")); 
        bottomPanel.add(updateButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == searchButton) {
            performSearch();
        } 
        else if (event.getSource() == editButton) {
            editSelectedItem();
        } 
        else if (event.getSource() == updateButton) {
            performUpdate();
        }
    }

    private void performSearch() {
        ArrayList<Sale> salesList = SalesDataHandler.loadSales();
        String searchDate = dateSearchField.getText().trim();
        String searchName = customerSearchField.getText().trim();

        // Sequential/Linear Search
        for (int i = 0; i < salesList.size(); i++) {
            Sale s = salesList.get(i);
            String saleDate = s.getDateTime().toLocalDate().toString();
            
            if (saleDate.equals(searchDate) && s.getCustomerName().equalsIgnoreCase(searchName)) {
                foundSale = s;
                refreshFields();
                updateButton.setEnabled(true);
                editButton.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Success: Record found!");
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Error: No matching record.");
    }

    private void refreshFields() {
        editNameField.setText(foundSale.getCustomerName());
        editTotalField.setText(String.valueOf(foundSale.getTotalAmount()));
        editMethodBox.setSelectedItem(foundSale.getPaymentMethod());
        
        itemsListModel.clear();
        ArrayList<SaleItem> items = foundSale.getItems();
        for (int i = 0; i < items.size(); i++) {
            SaleItem item = items.get(i);
            itemsListModel.addElement(item.getModelName() + " | Quantity: " + item.getQuantity());
        }
    }

    private void editSelectedItem() {
        int index = itemsList.getSelectedIndex();
        if (index == -1) return;

        SaleItem item = foundSale.getItems().get(index);
        String oldModel = item.getModelName();
        int oldQty = item.getQuantity();

        String newModel = JOptionPane.showInputDialog(this, "Enter New Model Name:", oldModel);
        if (newModel == null || !doesModelExist(newModel)) {
            JOptionPane.showMessageDialog(this, "Model does not exist.");
            return;
        }

        String newQtyStr = JOptionPane.showInputDialog(this, "Enter New Quantity:", oldQty);
        
        try {
            int newQty = Integer.parseInt(newQtyStr);
            ArrayList<Model> inventory = StockDataHandler.loadModels();

            for (int i = 0; i < inventory.size(); i++) {
                Model m = inventory.get(i);
                if (m.getModelName().equalsIgnoreCase(oldModel)) {
                    int currentStock = m.getStockForOutlet(currentOutletId);
                    m.setStockForOutlet(currentOutletId, currentStock + oldQty);
                }
                if (m.getModelName().equalsIgnoreCase(newModel)) {
                    int currentStock = m.getStockForOutlet(currentOutletId);
                    m.setStockForOutlet(currentOutletId, currentStock - newQty);
                }
            }
            
            StockDataHandler.saveModels(inventory);
            item.setModelName(newModel);
            item.setQuantity(newQty);
            refreshFields();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number entered!");
        }
    }

    private boolean doesModelExist(String name) {
        ArrayList<Model> models = StockDataHandler.loadModels();
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).getModelName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private void performUpdate() {
        try {
            double price = Double.parseDouble(editTotalField.getText());
            foundSale.setCustomerName(editNameField.getText().trim());
            foundSale.setTotalAmount(price);
            foundSale.setPaymentMethod((String) editMethodBox.getSelectedItem());

            ArrayList<Sale> allSales = SalesDataHandler.loadSales();
            for (int i = 0; i < allSales.size(); i++) {
                Sale s = allSales.get(i);
                if (s.getDateTime().equals(foundSale.getDateTime()) && s.getCustomerName().equalsIgnoreCase(customerSearchField.getText().trim())) {
                    allSales.set(i, foundSale);
                    break;
                }
            }

            SalesDataHandler.saveAllSales(allSales);
            JOptionPane.showMessageDialog(this, "File Updated Successfully!");
            this.dispose();  
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format Error: Please check your inputs.");
        }
    }
}