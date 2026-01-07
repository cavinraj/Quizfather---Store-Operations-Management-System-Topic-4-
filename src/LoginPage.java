package src;
import javax.swing.*; // Imports JFrame, JButton, JTextField, etc.
import java.awt.*;    // Imports Color, Font, etc.
import java.awt.event.*;
import java.util.ArrayList;

public class LoginPage extends JFrame implements ActionListener {
    private JLabel user_label;
    private JLabel pass_label;
    private JTextField user_text;
    private JPasswordField pass_text; // Hides the password with dots
    private JButton login_button;
    private JButton password_reset_button; // not required in our guidelines but im just doing for fun and to test my capabilities and logical thinking

    public LoginPage(){
        //First Phase - The Window
        setTitle("Store Operations Management System -- Employee Login");//This is for the title bar when we open the login page
        setSize(500, 300);//This is to set the width and height for our login page
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//We close the app when 'x' on the edge is clicked
        setLayout(null);//Turn off auto layout so that we can set coordinates manually for components if not it adjusts based on our window

        //Second phase - we create labels,text fields and buttons for our login page.

        //1. User ID label/header for user to fill up info in that textfield below it
        user_label = new JLabel("Enter User ID    :");
        user_label.setBounds(50, 70, 110, 25);//set the coordinates of this component and below manually with coordiantes
        add(user_label);// we connect the user_label and other componenets to the current login page window using add()

        //2. User ID text area
        user_text = new JTextField(20);
        user_text.setBounds(180, 70, 165, 25); // x=140 (Right of label), y=50 (Same height)
        add(user_text);

        // 3. Password Label
        pass_label = new JLabel("Enter Password :");
        pass_label.setBounds(50, 120, 140, 25); // y=100 (Lower down because we dont want it to be same level or nearer to user id area because will look ugly
        add(pass_label);

        // 4. Password Text Box
        pass_text = new JPasswordField(20);
        pass_text.setBounds(180, 120, 165, 25); // x=140 to beside of label, y=100 same height ith password label
        add(pass_text);

        // 5. Login Button
        login_button = new JButton("Login");
        login_button.setBounds(200, 180, 80, 25); // y=160 (Below all the other components so that it look systematic
        login_button.addActionListener(this);//Notify the current this class and connect the button to the code that must run when we use actionPerfromed() func
        add(login_button);

        password_reset_button = new JButton("Forgot Password");
        password_reset_button.setBounds(300, 233, 170, 25);// This forgot password button is set to be at bottom right by coordinates.
        password_reset_button.addActionListener(this);
        add(password_reset_button);

        setLocationRelativeTo(null); // Centers the window on your screen (according to java vscode documentation)
        setVisible(true);// makes the login page window visible


    }

    @Override
        public void actionPerformed(ActionEvent e){

            if (e.getSource() == login_button){
                //1.Fetch inputs from user
                String user_id = user_text.getText();
                String password = String.valueOf(pass_text.getPassword());
                String outlet_id;

                //2.Load employees to array from csv using FileReader class that we created.
                String employee_filename = "data/employee.csv";
                String outlet_filename = "data/outlet.csv";
                ArrayList<Object> employees = FileReader.transfer_data(employee_filename);
                ArrayList<Object> outlets = FileReader.transfer_data(outlet_filename);
                boolean found = false;

                if (user_id.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "The fields must not be empty!");
                    return;
                }

                //3.We search the inputted info from user about user_id and password by looping the array.
                for (Object employee : employees) {
                    for (Object outlet : outlets) {
                        if (((Employee) employee).get_employee_id().equals(user_id) && ((Employee) employee).get_password().equals(password)) {

                            outlet_id = user_id.substring(0, 3);

                            //we found match
                            found = true;

                            //we save the matched employee temporarily to Session class as long as the current round of program is running so that we recorded who logged in.
                            Session.current_user = (Employee) employee;
                            Session.user_current_outlet = (Outlet) outlet;

                            JOptionPane.showMessageDialog(this,"Login Successful!\n Welcome, " + ((Employee) employee).get_employee_name() + " (" + ((Outlet) outlet).getCode() + ")");

                            //close the current login window
                            this.dispose();

                            //when a user logs in,the window switched to Dashboard window
                            new DashboardPage();

                            break;
                        }
                    }
                }

                if (!found) {
                    JOptionPane.showMessageDialog(this,"Login Failed: Invalid User ID or Password.");
                }
            }
            if (e.getSource() == password_reset_button) {
                new UserIDConfirmationPage();
            }
        }
}
