package src;
import javax.swing.*;
import java.awt.event.*;
import java.io.FileWriter;// to write files
import java.io.PrintWriter;//to write files
import java.util.ArrayList;

public class RegisterPage extends JFrame implements ActionListener{

    JLabel name_label,id_label,password_label,role_label;
    JTextField name_field, id_field, password_field;
    JComboBox<String> role_box; // Option menu for Role
    JButton save_button, cancel_button;

    public RegisterPage(){
        setTitle("Store Operations Management System -- Register New Employee");
        setSize(550, 400);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//displose on close because i dont want to exit the entire app it wont look nice i just want to exit the current window

        // Name
        name_label = new JLabel("Enter Employee Name:");
        name_label.setBounds(30, 50, 150, 25);
        add(name_label);
        
        name_field = new JTextField();
        name_field.setBounds(180, 50, 200, 25);
        add(name_field);

        // Employee ID
        JLabel id_label = new JLabel("Enter Employee ID:");
        id_label.setBounds(30, 90, 150, 25);
        add(id_label);
        
        id_field = new JTextField();
        id_field.setBounds(180, 90, 200, 25);
        add(id_field);

        // Password
        password_label = new JLabel("Set Password:");
        password_label.setBounds(30, 130, 100, 25);
        add(password_label);
        
        password_field = new JTextField();
        password_field.setBounds(180, 130, 200, 25);
        add(password_field);

        // Employee Role 
        role_label = new JLabel("Set Role:");
        role_label.setBounds(30, 170, 100, 25);
        add(role_label);
        
        String[] roles = {"Full-time", "Manager", "Part-time"};
        role_box = new JComboBox<>(roles);
        role_box.setBounds(180, 170, 200, 25);
        add(role_box);

        // Save and Cancel Buttons
        save_button = new JButton("Save");
        save_button.setBounds(80, 220, 100, 30);
        save_button.addActionListener(this);
        add(save_button);

        cancel_button = new JButton("Cancel");
        cancel_button.setBounds(200, 220, 100, 30);
        cancel_button.addActionListener(this);
        add(cancel_button);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        handle_register_employee_page(e);
    }

    private void write_employee_file(String filename,String name,String id,String password,String role){
        //if inputted datas/infos are not empty and not already exist in the employee.csv file,i write the new entries to employee.csv
            try {
                //append = true because i want to update from the last line of the csv file so that we dont mess up the original data in csv file.
                PrintWriter pw = new PrintWriter(new FileWriter(filename, true));

                // write in exact format: ID,Name,Role,Password
                // important note i write in the same order as i coded in Filereader class so that the order doesnt mess up
                pw.println(id + "," + name + "," + role + "," + password);

                pw.close(); // i close file for memory optimzation
                
                // successful save message
                JOptionPane.showMessageDialog(this, "Employee successfully registered!");
                this.dispose(); // Close the registration window

            } catch (Exception exception) {//we use exception name because e is already used for ActionEvent we cant use again
                JOptionPane.showMessageDialog(this, "Error saving to file: " + exception.getMessage());
            }
    }

    private boolean is_valid_password(String filename,String name,String id,String password,String role){
        boolean is_invalid_password = false;
        // i check if the inputted user_id is already in the csv file because user_id is unique for each employee but name can be same sometimes.
        ArrayList<Object> employees = FileReader.transfer_data(filename);

        for (Object employee : employees) {
            if (((Employee) employee).get_employee_id().equals(id)) {
                JOptionPane.showMessageDialog(this, "Employee ID " + id + " already exists.");
                is_invalid_password = true;
                return is_invalid_password;
            }
        }

        if (id.length() != 5) {
            JOptionPane.showMessageDialog(this, "User ID needs to be 5 characters only!");
            is_invalid_password = true;
            return is_invalid_password;
        }

        if (password.length() != 6) {
            JOptionPane.showMessageDialog(this, "Password length must only be 6 characters!");
            is_invalid_password = true;
            return is_invalid_password;
        }
        for (int i = 0;i < password.length();i++){
            if (!Character.isLetterOrDigit(password.charAt(i))) {
                JOptionPane.showMessageDialog(this, "Password should only contain letters and digits!");
                is_invalid_password = true;
                return is_invalid_password;
            }
        }

        return is_invalid_password;
    }

    private void handle_register_employee_page(ActionEvent e){
        if (e.getSource() == cancel_button) {
            this.dispose(); // Close the current register page window
        } else if (e.getSource() == save_button) {
            String filename = "data/employee.csv";
            String name = name_field.getText().trim();
            String id = id_field.getText().trim();
            String password = password_field.getText().trim();
            String role = (String) role_box.getSelectedItem();// this returns the current selected item from the role_box field.

            // now i check if the infos are null because i dont want to send empty datas or entries into the csv file
            if (name.isEmpty() || id.isEmpty() || password.isEmpty()) {//i use OR because even if one of it is true send warning that "Please fill in all the details !"
                JOptionPane.showMessageDialog(this, "Please fill in all the details !");
                return;
            }

            boolean is_invalid_password = is_valid_password(filename, name, id, password, role);
            if (!is_invalid_password) {
                write_employee_file(filename, name, id, password, role);
            }
        }
    }
}
