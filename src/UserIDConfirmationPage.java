package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class UserIDConfirmationPage extends JFrame implements ActionListener{

    private JLabel user_id_label;
    private JTextField user_id_text;
    private JButton confirm_button;

    UserIDConfirmationPage(){
        setTitle("User ID Confirmation");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        user_id_label = new JLabel("Enter User ID :");
        user_id_label.setBounds(50, 30, 100, 25);
        add(user_id_label);

        user_id_text = new JTextField(20);
        user_id_text.setBounds(170, 30, 125, 25);
        add(user_id_text);

        confirm_button = new JButton("Confirm");
        confirm_button.setBounds(130, 100, 100, 25);
        confirm_button.addActionListener(this);
        add(confirm_button);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        boolean found = false;
        String user_id = user_id_text.getText();
        if (user_id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email!");
            return;
        }
        String filename = "data/employee.csv";
        ArrayList<Object> employees = FileReader.transfer_data(filename);
        for (Object employee : employees) {
            if (((Employee) employee).get_employee_id().equals(user_id)) {
                Session.current_user = (Employee) employee;
                found = true;
                this.dispose();
                new PasswordResetPage();
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Invalid User ID","WARNING : User ID not found !",JOptionPane.WARNING_MESSAGE);
        }
    }
}
