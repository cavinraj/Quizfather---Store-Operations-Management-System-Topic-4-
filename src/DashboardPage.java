package src;
import javax.swing.*;
import java.awt.event.*;

public class DashboardPage extends JFrame implements ActionListener{

    private JButton logout_button;
    private JButton register_button;
    private JButton stock_button; // NEW BUTTON
    
    DashboardPage(){
        // ... (Your existing window setup code) ...
        setTitle("Store Operations Management System -- Dashboard (Main Menu)");
        setSize(500,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // ... (Your existing Welcome label code) ...
        String name = "";
        if (Session.current_user != null) {
            name = Session.current_user.get_employee_name();
        }
        JLabel welcome_label = new JLabel("Welcome, " + name);
        welcome_label.setBounds(20, 20, 300, 30);
        welcome_label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        add(welcome_label);

        logout_button = new JButton("Log Out");
        logout_button.setBounds(350, 20, 100, 30);
        logout_button.addActionListener(this);
        add(logout_button);

        // --- NEW: STOCK MANAGEMENT BUTTON ---
        // Accessible by everyone (Manager and Full/Part time according to PDF instructions on page 5/6)
        stock_button = new JButton("Stock Management");
        stock_button.setBounds(20, 70, 200, 30);
        stock_button.addActionListener(this);
        add(stock_button);
        // ------------------------------------

        if (Session.current_user.get_role().equalsIgnoreCase("manager")) {
            register_button = new JButton("Register New Employee");
            register_button.setBounds(20, 120, 200, 30); // Moved down to y=120 to make space
            register_button.addActionListener(this);
            add(register_button);
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logout_button) {
            // ... (Your existing logout logic) ...
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                Session.current_user = null;
                this.dispose();
                new LoginPage();
            }
        }
        else if (e.getSource() == register_button) {
            new RegisterPage();
        }
        // --- NEW: BUTTON LOGIC ---
        else if (e.getSource() == stock_button) {
            new StockManagementPage();
        }
        // -------------------------
    }
}