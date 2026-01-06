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

    public StockManagementPage() {
        setTitle("Stock Management System");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Determine Outlet ID from current user ID (e.g., C6001 -> C60)
        if (Session.current_user != null) {
            String empId = Session.current_user.get_employee_id();
            if (empId.length() >= 3) {
                currentOutletId = empId.substring(0, 3);
            } else {
                currentOutletId = "C60"; // Fallback
            }
        }

        JLabel title = new JLabel("Stock Management (" + currentOutletId + ")");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(80, 20, 250, 30);
        add(title);

        stockCountBtn = new JButton("Stock Count (Morning/Night)");
        stockCountBtn.setBounds(50, 70, 280, 40);
        stockCountBtn.addActionListener(this);
        add(stockCountBtn);

        stockTransferBtn = new JButton("Stock Transfer (In/Out)");
        stockTransferBtn.setBounds(50, 130, 280, 40);
        stockTransferBtn.addActionListener(this);
        add(stockTransferBtn);

        backBtn = new JButton("Back to Dashboard");
        backBtn.setBounds(100, 200, 180, 30);
        backBtn.addActionListener(this);
        add(backBtn);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backBtn) {
            this.dispose();
            // Assuming Dashboard is already open, if not, new DashboardPage();
        } 
        else if (e.getSource() == stockCountBtn) {
            performStockCount();
        } 
        else if (e.getSource() == stockTransferBtn) {
            performStockTransfer();
        }
    }

    // --- FEATURE 1: STOCK COUNT ---
    private void performStockCount() {
        ArrayList<Model> models = StockDataHandler.loadModels();
        int mismatches = 0;
        int tallyCorrect = 0;

        // Simple input loop for each model
        for (Model model : models) {
            String input = JOptionPane.showInputDialog(this, 
                "Enter count for model: " + model.getModelName(), 
                "Stock Count", JOptionPane.QUESTION_MESSAGE);
            
            if (input == null) break; // User cancelled

            try {
                int counted = Integer.parseInt(input);
                int record = model.getStockForOutlet(currentOutletId);

                if (counted == record) {
                    JOptionPane.showMessageDialog(this, "Match! Count: " + counted);
                    tallyCorrect++;
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "MISMATCH!\nCounted: " + counted + "\nRecord: " + record, 
                        "Warning", JOptionPane.WARNING_MESSAGE);
                    mismatches++;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number entered.");
            }
        }
        
        JOptionPane.showMessageDialog(this, 
            "Stock Count Completed.\nCorrect: " + tallyCorrect + "\nMismatches: " + mismatches);
    }

    // --- FEATURE 2: STOCK TRANSFER ---
    private void performStockTransfer() {
        // Create a custom dialog for transfer details
        JDialog d = new JDialog(this, "Stock Transfer", true);
        d.setSize(400, 400);
        d.setLayout(null);
        d.setLocationRelativeTo(this);

        JLabel typeLbl = new JLabel("Type:");
        typeLbl.setBounds(30, 20, 100, 25);
        d.add(typeLbl);

        String[] types = {"Stock In", "Stock Out"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        typeBox.setBounds(140, 20, 200, 25);
        d.add(typeBox);

        JLabel outletLbl = new JLabel("Target Outlet:"); // Where is it coming from or going to?
        outletLbl.setBounds(30, 60, 100, 25);
        d.add(outletLbl);

        // Load outlets for dropdown
        ArrayList<Outlet> outlets = StockDataHandler.loadOutlets();
        JComboBox<Outlet> outletBox = new JComboBox<>(outlets.toArray(new Outlet[0]));
        outletBox.setBounds(140, 60, 200, 25);
        d.add(outletBox);

        JLabel modelLbl = new JLabel("Model:");
        modelLbl.setBounds(30, 100, 100, 25);
        d.add(modelLbl);

        // Load models for dropdown
        ArrayList<Model> modelList = StockDataHandler.loadModels();
        // Create String array for combo box
        String[] modelNames = new String[modelList.size()];
        for(int i=0; i<modelList.size(); i++) modelNames[i] = modelList.get(i).getModelName();
        
        JComboBox<String> modelBox = new JComboBox<>(modelNames);
        modelBox.setBounds(140, 100, 200, 25);
        d.add(modelBox);

        JLabel qtyLbl = new JLabel("Quantity:");
        qtyLbl.setBounds(30, 140, 100, 25);
        d.add(qtyLbl);

        JTextField qtyField = new JTextField();
        qtyField.setBounds(140, 140, 200, 25);
        d.add(qtyField);

        JButton processBtn = new JButton("Process Transfer");
        processBtn.setBounds(100, 250, 180, 30);
        d.add(processBtn);

        processBtn.addActionListener(ev -> {
            String type = (String) typeBox.getSelectedItem();
            Outlet selectedOutlet = (Outlet) outletBox.getSelectedItem();
            String selectedModelName = (String) modelBox.getSelectedItem();
            String qtyText = qtyField.getText();

            try {
                int qty = Integer.parseInt(qtyText);
                if (qty <= 0) throw new NumberFormatException();

                // Logic:
                // Stock In: Increases current outlet stock. Comes FROM selectedOutlet.
                // Stock Out: Decreases current outlet stock. Goes TO selectedOutlet.
                
                boolean isStockIn = type.equals("Stock In");
                
                // Find and update model
                for (Model m : modelList) {
                    if (m.getModelName().equals(selectedModelName)) {
                        int currentStock = m.getStockForOutlet(currentOutletId);
                        
                        if (!isStockIn && currentStock < qty) {
                            JOptionPane.showMessageDialog(d, "Insufficient stock for transfer!");
                            return;
                        }

                        int newStock = isStockIn ? (currentStock + qty) : (currentStock - qty);
                        m.setStockForOutlet(currentOutletId, newStock);
                        
                        // Save changes to CSV
                        StockDataHandler.saveModels(modelList);
                        
                        // Generate Receipt Text
                        StringBuilder receipt = new StringBuilder();
                        receipt.append("=== ").append(type).append(" ===\n");
                        receipt.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
                        receipt.append("Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))).append("\n");
                        
                        if (isStockIn) {
                            receipt.append("From: ").append(selectedOutlet.getCode()).append("\n");
                            receipt.append("To: ").append(currentOutletId).append(" (Current)\n");
                        } else {
                            receipt.append("From: ").append(currentOutletId).append(" (Current)\n");
                            receipt.append("To: ").append(selectedOutlet.getCode()).append("\n");
                        }
                        
                        receipt.append("Model: ").append(m.getModelName()).append("\n");
                        receipt.append("Quantity: ").append(qty).append("\n");
                        receipt.append("Employee: ").append(Session.current_user.get_employee_name()).append("\n");
                        
                        StockDataHandler.appendReceipt(receipt.toString());
                        
                        JOptionPane.showMessageDialog(d, "Transfer Successful! Receipt generated.");
                        d.dispose();
                        break;
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "Invalid Quantity");
            }
        });

        d.setVisible(true);
    }
}