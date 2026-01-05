import javax.swing.*;
import java.awt.event.*;

public class DashboardPage extends JFrame implements ActionListener{

    private JButton logout_button;
    private JButton register_button;
    
    DashboardPage(){
        //1.Window Steup
        setTitle("Store Operations Management System -- Dashboard (Main Menu)");
        setSize(500,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);// this is for manual coordinate positioning for each component

        //we welcome the current user who is logged in
        String name = "";
        if (Session.current_user != null) {
            name = Session.current_user.get_employee_name();
        }

        JLabel welcome_label = new JLabel("Welcome, " + name);
        welcome_label.setBounds(20, 20, 300, 30);
        welcome_label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18)); // Make it big
        add(welcome_label);

        logout_button = new JButton("Log Out");//allows the user to log out 
        logout_button.setBounds(350, 20, 100, 30); // Top right corner
        logout_button.addActionListener(this);
        add(logout_button);//adds the logout button component to the dashboard window

        setLocationRelativeTo(null);

        if (Session.current_user.get_role().equalsIgnoreCase("manager")) {

            // we doing this to make sure that register button is only exclusive to manager

            register_button = new JButton("Register New Employee");
            register_button.setBounds(20, 70, 200, 30); // Below the welcome label
            register_button.addActionListener(this); // add event listener to perform action when the button is clicked later
            add(register_button);// add the register button on the Dashboard window
            
        }

        setVisible(true);//makes the wundow visible
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logout_button) {// we check if the source event came from logout_button when clicked
            
            // first we check and confirm they want to logout
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                // if user put yes then we must clear the session and reset the current user to null because nobody is logged in
                Session.current_user = null;
                
                // Close this Dashboard window
                this.dispose();
                
                // but important note based on requirements we must invoke the Login Page again after a user logs out.
                new LoginPage();
            }
        }
        else if (e.getSource() == register_button) {//we check if the source event came from reigster_button when clicked
            // if true we open the Registration window
            new RegisterPage();
        }
    }
}
