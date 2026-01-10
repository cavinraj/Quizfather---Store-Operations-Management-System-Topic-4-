package src.ui;

import javax.swing.*;

import src.model.Model;
import src.utils.Session;
import src.utils.StockDataHandler;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class StockEditPage extends JFrame implements ActionListener {
    // UI components for interaction and display
    private JTextField modelSearchField, newStockField;
    private JButton loadButton, updateButton, backButton; // Added backButton
    private JLabel currentStockLabel, modelNameLabel;
    private Model foundModel = null; // Stores the model object currently being edited

    public StockEditPage() {
        setTitle("Edit Stock Information"); 
        setSize(400, 350);
        setLayout(new GridLayout(7, 1, 10, 10));

        // model search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Model Name:"));
        modelSearchField = new JTextField(10);
        searchPanel.add(modelSearchField);
        loadButton = new JButton("Load");
        loadButton.addActionListener(this);
        searchPanel.add(loadButton);
        add(searchPanel);

        // display model name and current stock
        modelNameLabel = new JLabel("Model: -", SwingConstants.CENTER);
        currentStockLabel = new JLabel("Current Stock: -", SwingConstants.CENTER);
        add(modelNameLabel);
        add(currentStockLabel);

        // input new stock
        JPanel updatePanel = new JPanel(new FlowLayout());
        updatePanel.add(new JLabel("New Stock:"));
        newStockField = new JTextField(5);
        newStockField.setEnabled(false); // Disabled until a model is found
        updatePanel.add(newStockField);
        add(updatePanel);

        // update stock button
        updateButton = new JButton("Update Stock Value");
        updateButton.setEnabled(false);
        updateButton.addActionListener(this);
        add(updateButton);

        // back button
        backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(this);
        add(backButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadButton) {
            handleSearch();
        } else if (e.getSource() == updateButton) {
            handleUpdate();
        } else if (e.getSource() == backButton) {
            this.dispose(); // close window and return to dashboard
        }
    }

    private void handleSearch() {
        ArrayList<Model> modelList = StockDataHandler.loadModels();
        String name = modelSearchField.getText().trim();
        foundModel = null;

        // Implement searching algorithm as a prerequisite
        for (Model m : modelList) {
            if (m.getModelName().equalsIgnoreCase(name)) {
                foundModel = m;
                break;
            }
        }

        if (foundModel != null) {
            // retrieve data for the current employee's outlet
            String outletCode = Session.current_user.get_employee_id().substring(0, 3); 
            int stock = foundModel.getStockForOutlet(outletCode);
            
            modelNameLabel.setText("Model: " + foundModel.getModelName());
            currentStockLabel.setText("Current Stock: " + stock);
            newStockField.setEnabled(true);
            updateButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Model not found.");
        }
    }

    private void handleUpdate() {
    try {
        int newVal = Integer.parseInt(newStockField.getText().trim());
        if (newVal < 0) {
            throw new NumberFormatException();
        }

        String outletCode = Session.current_user.get_employee_id().substring(0, 3);

        ArrayList<Model> allModels = StockDataHandler.loadModels();

        boolean found = false;
        for (Model m : allModels) {
            if (m.getModelName().equalsIgnoreCase(foundModel.getModelName())) {
                m.setStockForOutlet(outletCode, newVal);
                found = true;
                break;
            }
        }

        if (found) {
            StockDataHandler.saveModels(allModels); 
            JOptionPane.showMessageDialog(this, "Stock updated successfully in model.csv!");
            this.dispose(); // Close window and return to dashboard
        } else {
            JOptionPane.showMessageDialog(this, "Error: Model not found in the master list.");
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Error: Please enter a valid positive number.");
    }
}
}
