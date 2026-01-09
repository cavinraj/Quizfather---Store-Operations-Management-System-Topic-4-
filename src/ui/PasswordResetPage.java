package src.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import src.model.Employee;
import src.utils.FileReader;
import src.utils.Session;

public class PasswordResetPage extends JFrame implements ActionListener{

    JLabel enter_password_label;
    JLabel confirm_password_label;
    JPasswordField enter_password_text;
    JPasswordField confirm_password_text;
    JButton confirm_button;

    PasswordResetPage(){
        setTitle("Reset Password");//This is for the title bar when we open the reset password page
        setSize(400, 200);//This is to set the width and height for our reset password page
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//We close the app when 'x' on the edge is clicked
        setLayout(null);//Turn off auto layout so that we can set coordinates manually for components if not it adjusts based on our window screen size

        enter_password_label = new JLabel("Enter new password :");
        enter_password_label.setBounds(50, 30, 150, 25);
        add(enter_password_label);

        enter_password_text = new JPasswordField(20);
        enter_password_text.setBounds(180, 30, 165, 25);
        add(enter_password_text);

        confirm_password_label = new JLabel("Confirm password :");
        confirm_password_label.setBounds(50, 80, 140, 25);
        add(confirm_password_label);

        confirm_password_text = new JPasswordField(20);
        confirm_password_text.setBounds(180, 80, 165, 25);
        add(confirm_password_text);

        confirm_button = new JButton("Confirm Password");
        confirm_button.setBounds(116, 134, 160, 25);
        confirm_button.addActionListener(this);
        add(confirm_button);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        handle_password_reset_page(e);
    }

    private void handle_password_reset_page(ActionEvent e){
        String first_password_text = String.valueOf(enter_password_text.getPassword());
        String second_password_text = String.valueOf(confirm_password_text.getPassword());
        String user_id = "";
        String filename = "data/employee.csv";

        reset_password(filename, first_password_text, second_password_text, user_id);
    }

    private void reset_password(String filename,String first_password_text,String second_password_text,String user_id){
        if (first_password_text.isEmpty() || second_password_text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "New Password must not be empty!");
            return;
        }

        if (first_password_text.equals(second_password_text)) {
            if (Session.current_user != null) {
                user_id = Session.current_user.get_employee_id();
            }

            ArrayList<Object> employees = FileReader.transfer_data(filename);
            try (PrintWriter printwriter = new PrintWriter(new FileWriter(filename, false))){// Non-append mode
                printwriter.println("Employee ID,Name,Role,Password");//we print the header for the columns first because in filereader we made the scanner to skip the first line
                for (Object employee : employees) {
                    if (((Employee) employee).get_employee_id().equals(user_id)) {
                        ((Employee) employee).set_password(first_password_text);
                    }
                    printwriter.println(((Employee) employee).get_employee_id() + "," + ((Employee) employee).get_employee_name() + "," + ((Employee) employee).get_role() + "," + ((Employee) employee).get_password());
                }
                printwriter.close();
                JOptionPane.showMessageDialog(this, "Password successfully changed!");
                this.dispose();
            } catch (Exception exception) {
                System.out.println("Error : nanana");
                //exception.printStackTrace();
            }
        }
        else {
            JOptionPane.showMessageDialog(this, "The Passwords do not match!\n" + "Please re-enter them");
        }
    }
}
