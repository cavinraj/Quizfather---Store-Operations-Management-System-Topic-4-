package src;
import javax.swing.*;
import java.awt.event.*;

public class DashboardPage extends JFrame implements ActionListener{

    private JButton logout_button;
    private JButton register_button;
    private JButton stock_button; 
    private JButton clock_in_button;
    private JButton clock_out_button;
    private JButton analytics_button;
    private JButton sales_entry_button;
    private JButton sales_button;
    private JButton history_button;
    private JButton search_info_button; // button for stock search page
    private JButton edit_info_button; // button for stock edit page
    
    DashboardPage(){
        // ... (Your existing window setup code) ...
        setTitle("Store Operations Management System -- Dashboard (Main Menu)");
        setSize(500,700);
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

        int y_position = 70;
        int gap_between_buttons = 50;

        if (Session.current_user.get_role().equalsIgnoreCase("manager")) {
            register_button = new JButton("Register New Employee");
            register_button.setBounds(20, y_position, 200, 30); // Moved down to y=120 to make space
            register_button.addActionListener(this);
            add(register_button);

            y_position += gap_between_buttons; 
        }

        stock_button = new JButton("Stock Management");
        stock_button.setBounds(20, y_position, 200, 30);
        stock_button.addActionListener(this);
        add(stock_button);
        
        y_position += gap_between_buttons;

        sales_button = new JButton("Record New Sale");
        sales_button.setBounds(20, 120, 200, 30);
        sales_button.addActionListener(this);
        add(sales_button);

        y_position += gap_between_buttons;

        history_button = new JButton("Sales History & Filter");
        history_button.setBounds(20, 170, 200, 30);
        history_button.addActionListener(this);
        add(history_button);

        y_position += gap_between_buttons;

        // ===== ADDED (Data Analytics Button) =====
        analytics_button = new JButton("Data Analytics");
        analytics_button.setBounds(20, y_position, 200, 30);
        analytics_button.addActionListener(this);
        add(analytics_button);
        
        y_position += gap_between_buttons;
        // ========================================
        
        // ===== SALES ENTRY BUTTON =====
        sales_entry_button = new JButton("Enter Sales");
        sales_entry_button.setBounds(20, y_position, 200, 30);
        sales_entry_button.addActionListener(this);
        add(sales_entry_button);

        y_position += gap_between_buttons;

        // ===== SEARCH INFO BUTTON =====
        search_info_button = new JButton("Search Stock");
        search_info_button.setBounds(20, y_position, 200, 30);
        search_info_button.addActionListener(this);
        add(search_info_button);

        y_position += gap_between_buttons;

        // ===== EDIT INFO BUTTON =====
        edit_info_button = new JButton("Edit Stock");
        edit_info_button.setBounds(20, y_position, 200, 30);
        edit_info_button.addActionListener(this);
        add(edit_info_button);

        y_position += gap_between_buttons;

        //this upcoming two buttons are for attendance feature which includes clock in button and clock out button
        clock_in_button = new JButton("Clock In");
        clock_in_button.setBounds(20, y_position, 150, 40);
        clock_in_button.addActionListener(this);
        add(clock_in_button);

        clock_out_button = new JButton("Clock Out");
        clock_out_button.setBounds(180, y_position, 150, 40); // Next to it
        clock_out_button.addActionListener(this);
        add(clock_out_button);

        setLocationRelativeTo(null);
        setVisible(true);
    }


    public void actionPerformed(ActionEvent e) {
        handle_dashboard_page(e);
    }

    private void handle_logout_button(){
        // first check and confirm they want to logout
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            // if user put yes then must clear the session and reset the current user to null because nobody is logged in
            Session.current_user = null;

            // Close this Dashboard window
            this.dispose();

            // but important note based on requirements we must invoke the Login Page again after a user logs out.
            new LoginPage();
        }
    }

    private void handle_dashboard_page(ActionEvent e){
        if (e.getSource() == logout_button) {// check if the source event came from logout_button when clicked
            handle_logout_button();
        }
        else if (e.getSource() == register_button) {//check if the source event came from reigster_button when clicked
            // if true open the Registration window
            new RegisterPage();
        }
        else if (e.getSource() == clock_in_button) {
            new ClockInPage();
        }
        else if (e.getSource() == clock_out_button) {
            new ClockOutPage();
        }
        // --- NEW: BUTTON LOGIC ---
        else if (e.getSource() == stock_button) {
            new StockManagementPage();
        }
        // ===== ADDED (Data Analytics Logic) =====
        else if (e.getSource() == analytics_button) {
            DataAnalytics.runAnalytics();
        }
        // ===== ADDED (Sales Entry Logic) =====
        else if (e.getSource() == sales_entry_button) {
            DataAnalytics.enterSales(new java.util.Scanner(System.in));
        } else if (e.getSource() == sales_button) {
            new SalesPage();
        }
         else if (e.getSource() == history_button) {
            new SalesHistoryPage();
        }
        else if (e.getSource() == search_info_button) {
            new StockSearchPage();
        }
        else if (e.getSource() == edit_info_button) {
            new StockEditPage();
        }
    }
}
