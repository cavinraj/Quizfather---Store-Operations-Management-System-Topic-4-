package src.ui;

import javax.swing.*;

import src.model.Model;
import src.model.Outlet;
import src.utils.StockDataHandler;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;

public class StockSearchPage extends JFrame implements ActionListener {
    private JTextField searchField;
    private JButton searchButton;
    private JButton backButton;
    private JTextArea resultArea;

    public StockSearchPage() {
        setTitle("Search Stock Information");
        setSize(450, 450);
        setLayout(new BorderLayout(10, 10));

        // top panel for search input
        JPanel topPanel = new JPanel(new FlowLayout());
        
        // back button
        backButton = new JButton("Back");
        backButton.addActionListener(this);
        topPanel.add(backButton);
        
        topPanel.add(new JLabel("Enter Model:"));
        searchField = new JTextField(10);
        topPanel.add(searchField);
        
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // panel for results
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // search logic
        if (e.getSource() == searchButton) {
            performSearch();
        } 
        // back button logic
        else if (e.getSource() == backButton) {
            this.dispose();
        }
    }


    private void performSearch() {
        ArrayList<Model> modelList = StockDataHandler.loadModels();
        ArrayList<Outlet> allOutlets = StockDataHandler.loadOutlets();
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a model name.");
            return;
        }

        resultArea.setText("Searching...\n\n");
        boolean found = false;

        // search through models
        for (Model m : modelList) {
            if (m.getModelName().equalsIgnoreCase(query)) {
                found = true;
                resultArea.append("Model: " + m.getModelName() + "\n");
                resultArea.append("Unit Price: RM" + m.getPrice() + "\n");
                resultArea.append("----------------------------\n");
                resultArea.append("Stock by Outlet:\n");

                // retrieve stock info
                Map<String, Integer> stockMap = m.getStockMap();
                for (Outlet outlet : allOutlets) {
                    int qty = stockMap.getOrDefault(outlet.getCode(), 0);
                    resultArea.append(String.format("%-15s: %d\n", outlet.getName(), qty));
                }
                break;
            }
        }

        if (!found) {
            resultArea.append("No records found for: " + query);
        }
    }
}