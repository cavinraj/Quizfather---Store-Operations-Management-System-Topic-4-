package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockManagementPage extends JFrame implements ActionListener {
    JButton stockCountBtn, stockTransferBtn, backBtn;
    String currentOutletId; 

    // Inner class to hold pending transfer items
    class TransferItem {
        Model model;
        int quantity;
        public TransferItem(Model m, int q) { this.model = m; this.quantity = q; }
    }

    public StockManagementPage() {
        setTitle("Store Operations - Stock Management");
        setSize(450, 350);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Determine Current Outlet
        if (Session.current_user != null) {
            String empId = Session.current_user.get_employee_id();
            currentOutletId = (empId.length() >= 3) ? empId.substring(0, 3) : "C60";
        } else {
            currentOutletId = "C60";
        }

        JLabel title = new JLabel("Stock Management (" + currentOutletId + ")");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(100, 20, 250, 30);
        add(title);

        stockCountBtn = new JButton("Stock Count (Morning/Night)");
        stockCountBtn.setBounds(70, 70, 300, 40);
        stockCountBtn.addActionListener(this);
        add(stockCountBtn);

        stockTransferBtn = new JButton("Stock Transfer (In/Out)");
        stockTransferBtn.setBounds(70, 130, 300, 40);
        stockTransferBtn.addActionListener(this);
        add(stockTransferBtn);

        backBtn = new JButton("Back to Dashboard");
        backBtn.setBounds(130, 220, 180, 30);
        backBtn.addActionListener(this);
        add(backBtn);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backBtn) this.dispose();
        else if (e.getSource() == stockCountBtn) performStockCount();
        else if (e.getSource() == stockTransferBtn) performStockTransfer();
    }


    // STOCK COUNT (Same as before)
    private void performStockCount() {
        ArrayList<Model> models = StockDataHandler.loadModels();
        int mismatches = 0;
        int tallyCorrect = 0;

        for (Model model : models) {
            int systemRecord = model.getStockForOutlet(currentOutletId);
            String input = JOptionPane.showInputDialog(this, 
                "Model: " + model.getModelName() + "\nSystem Record: " + systemRecord + "\nEnter physical count:", 
                "Stock Count", JOptionPane.QUESTION_MESSAGE);
            
            if (input == null) break;

            try {
                int counted = Integer.parseInt(input);
                if (counted == systemRecord) {
                    tallyCorrect++;
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "MISMATCH DETECTED!\nModel: " + model.getModelName() + 
                        "\nCounted: " + counted + "\nSystem Record: " + systemRecord, 
                        "Stock Mismatch Warning", JOptionPane.WARNING_MESSAGE);
                    mismatches++;
                }
            } catch (NumberFormatException ex) {
                
            }
        }
        JOptionPane.showMessageDialog(this, "Stock Count Summary:\nCorrect: " + tallyCorrect + "\nMismatches: " + mismatches);
    }

    // STOCK TRANSFER (Multi-Item)
    private void performStockTransfer() {
        JDialog d = new JDialog(this, "New Stock Transfer", true);
        d.setSize(500, 600); // Taller window for list
        d.setLayout(null);
        d.setLocationRelativeTo(this);

        // --- Header Controls ---
        JLabel typeLbl = new JLabel("Transaction Type:");
        typeLbl.setBounds(30, 20, 120, 25);
        d.add(typeLbl);

        String[] types = {"Stock In", "Stock Out"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        typeBox.setBounds(150, 20, 200, 25);
        d.add(typeBox);

        JLabel fromLbl = new JLabel("From (Source):");
        fromLbl.setBounds(30, 60, 120, 25);
        d.add(fromLbl);
        JComboBox<String> fromBox = new JComboBox<>();
        fromBox.setBounds(150, 60, 200, 25);
        d.add(fromBox);

        JLabel toLbl = new JLabel("To (Dest):");
        toLbl.setBounds(30, 100, 120, 25);
        d.add(toLbl);
        JComboBox<String> toBox = new JComboBox<>();
        toBox.setBounds(150, 100, 200, 25);
        d.add(toBox);

        // Load Data
        ArrayList<Outlet> allOutlets = StockDataHandler.loadOutlets();
        allOutlets.add(0, new Outlet("HQ", "Headquarters"));
        ArrayList<Model> modelList = StockDataHandler.loadModels();

        // Populate Locations Logic
        ActionListener updateBoxes = e -> {
            String selectedType = (String) typeBox.getSelectedItem();
            fromBox.removeAllItems();
            toBox.removeAllItems();

            if ("Stock In".equals(selectedType)) {
                // Coming TO me
                for (Outlet o : allOutlets) {
                    if (!o.getCode().equals(currentOutletId)) fromBox.addItem(o.getCode());
                }
                toBox.addItem(currentOutletId);
                toBox.setEnabled(false);
                fromBox.setEnabled(true);
            } else {
                // Going FROM me
                fromBox.addItem(currentOutletId);
                fromBox.setEnabled(false);
                for (Outlet o : allOutlets) {
                    if (!o.getCode().equals(currentOutletId)) toBox.addItem(o.getCode());
                }
                toBox.setEnabled(true);
            }
        };
        typeBox.addActionListener(updateBoxes);
        updateBoxes.actionPerformed(null);

        // Item Selection
        JSeparator sep = new JSeparator();
        sep.setBounds(20, 140, 440, 10);
        d.add(sep);

        JLabel modelLbl = new JLabel("Add Model:");
        modelLbl.setBounds(30, 160, 100, 25);
        d.add(modelLbl);

        String[] modelNames = new String[modelList.size()];
        for(int i=0; i<modelList.size(); i++) modelNames[i] = modelList.get(i).getModelName();
        JComboBox<String> modelBox = new JComboBox<>(modelNames);
        modelBox.setBounds(110, 160, 180, 25);
        d.add(modelBox);

        JLabel qtyLbl = new JLabel("Qty:");
        qtyLbl.setBounds(300, 160, 40, 25);
        d.add(qtyLbl);

        JTextField qtyField = new JTextField();
        qtyField.setBounds(330, 160, 60, 25);
        d.add(qtyField);

        JButton addBtn = new JButton("Add to List");
        addBtn.setBounds(150, 200, 150, 30);
        d.add(addBtn);

        // List Display
        JTextArea listArea = new JTextArea();
        listArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(listArea);
        scroll.setBounds(30, 240, 420, 150);
        scroll.setBorder(BorderFactory.createTitledBorder("Items to Transfer"));
        d.add(scroll);

        // Transfer Queue
        ArrayList<TransferItem> transferQueue = new ArrayList<>();

        // Add Button Logic
        addBtn.addActionListener(ev -> {
            String mName = (String) modelBox.getSelectedItem();
            try {
                int qty = Integer.parseInt(qtyField.getText());
                if(qty <= 0) throw new NumberFormatException();

                // Find model object
                Model selectedModel = null;
                for(Model m : modelList) if(m.getModelName().equals(mName)) selectedModel = m;

                if(selectedModel != null) {
                    transferQueue.add(new TransferItem(selectedModel, qty));
                    listArea.append(mName + " (Qty: " + qty + ")\n");
                    qtyField.setText(""); // Clear input
                }
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "Invalid Quantity");
            }
        });

        // Confirm Button
        JButton confirmBtn = new JButton("Confirm Transfer");
        confirmBtn.setBounds(150, 410, 180, 40);
        d.add(confirmBtn);

        confirmBtn.addActionListener(ev -> {
            if(transferQueue.isEmpty()) {
                JOptionPane.showMessageDialog(d, "No items in list!");
                return;
            }

            String source = (String) fromBox.getSelectedItem();
            String dest = (String) toBox.getSelectedItem();
            String type = (String) typeBox.getSelectedItem();
            int totalQty = 0;

            // Validation Loop
            for(TransferItem item : transferQueue) {
                if(!source.equals("HQ")) {
                    int currentStock = item.model.getStockForOutlet(source);
                    if(currentStock < item.quantity) {
                        JOptionPane.showMessageDialog(d, "Insufficient stock for " + item.model.getModelName() + " at " + source);
                        return; // Stop transaction
                    }
                }
            }

            // Processing Loop
            for(TransferItem item : transferQueue) {
                if(!source.equals("HQ")) {
                    int sStock = item.model.getStockForOutlet(source);
                    item.model.setStockForOutlet(source, sStock - item.quantity);
                }
                if(!dest.equals("HQ")) {
                    int dStock = item.model.getStockForOutlet(dest);
                    item.model.setStockForOutlet(dest, dStock + item.quantity);
                }
                totalQty += item.quantity;
            }

            StockDataHandler.saveModels(modelList);

            // Receipt Generation
            StringBuilder receipt = new StringBuilder();
            receipt.append("=== ").append(type.toUpperCase()).append(" ===\n");
            receipt.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
            receipt.append("Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))).append("\n");
            receipt.append("From: ").append(source).append("\n");
            receipt.append("To:   ").append(dest).append("\n");
            receipt.append("Models Received:\n");
            
            for(TransferItem item : transferQueue) {
                receipt.append(" - ").append(item.model.getModelName())
                       .append(" (Quantity: ").append(item.quantity).append(")\n");
            }
            
            receipt.append("Total Quantity: ").append(totalQty).append("\n");
            if(Session.current_user != null) {
                receipt.append("Employee: ").append(Session.current_user.get_employee_name()).append("\n");
            }
            
            StockDataHandler.appendReceipt(receipt.toString());
            
            JOptionPane.showMessageDialog(d, "Transfer Successful!\nReceipt generated.");
            d.dispose();
        });

        d.setVisible(true);
    }
}