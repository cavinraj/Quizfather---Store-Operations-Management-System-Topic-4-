package src.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import src.model.Sale;
import src.model.SaleItem;
import src.utils.SalesDataHandler;

public class SalesSearchPage extends JFrame implements ActionListener {
    private JTextField inputField;
    private JTextArea display;
    private JButton searchButton, backButton;

    public SalesSearchPage() {
        setTitle("Search Sales");
        setSize(600, 600);
        
        setLayout(new BorderLayout());

        JPanel top = new JPanel(); 
        backButton = new JButton("Back");
        top.add(backButton);
        
        top.add(new JLabel("Search (Customer Name, Date, or Item):"));
        inputField = new JTextField(20);
        top.add(inputField);
        
        searchButton = new JButton("Go");
        top.add(searchButton);
        add(top, BorderLayout.NORTH);

        display = new JTextArea();
        display.setEditable(false);
        add(new JScrollPane(display), BorderLayout.CENTER);

        searchButton.addActionListener(this);
        backButton.addActionListener(this);

        setLocationRelativeTo(null);
        setVisible(true);
        }

    // standard action listeners
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            startSearch();
        } 
        else if (e.getSource() == backButton) {
            this.dispose();
        }
    }

    private void startSearch() {
        String query = inputField.getText().trim().toLowerCase();
        
        if (query.equals("")) {
            JOptionPane.showMessageDialog(this, "Enter a keyword to search.");
            return;
        }

        ArrayList<Sale> list = SalesDataHandler.loadSales();
        display.setText("");
        
        boolean foundAtLeastOne = false;

        for (int i = 0; i < list.size(); i++) {
            Sale s = list.get(i);
            boolean hasItem = false;
            
            // Checking items inside the sale
            for (SaleItem item : s.getItems()) {
                if (item.getModelName().toLowerCase().contains(query)) {
                    hasItem = true;
                }
            }

            // check name, date or item match
            if (s.getCustomerName().toLowerCase().contains(query) || 
                s.getDateTime().toString().contains(query) || 
                hasItem) {
                
                foundAtLeastOne = true;
                
                display.append("Date: " + s.getDateTime().toLocalDate() + "\n");
                display.append("Customer: " + s.getCustomerName() + "\n");
                display.append("Total: RM " + s.getTotalAmount() + "\n");
                display.append("Method: " + s.getPaymentMethod() + "\n");
                
                String itemsStr = "";
                for(SaleItem si : s.getItems()) {
                    itemsStr += si.getModelName() + " (x" + si.getQuantity() + "), ";
                }
                display.append("Items: " + itemsStr + "\n");
                display.append("----------------------------------\n");
            }
        }

        if (foundAtLeastOne == false) {
            display.setText("No results found for " + query);
        }
    }
}
