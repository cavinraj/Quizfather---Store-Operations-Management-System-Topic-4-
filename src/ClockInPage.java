package src;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClockInPage extends JFrame{
    
    ClockInPage(){
        setTitle("Attendance Clock In");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        String id = Session.current_user.get_employee_id();// get the current employee id;
        String name = Session.current_user.get_employee_name();// get the current employee name;
        String outlet_code = Session.user_current_outlet.getCode();
        String outlet_name = Session.user_current_outlet.getName();

        // By default Java DateTime gives year first then month and then day so must format it.
        // This tells Java: "I want Day first, then Month, then Year"
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date_string = today.format(date_formatter);

        // Format time to look like "09:00:00"
        DateTimeFormatter time_formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time_string = now.format(time_formatter);

        try {
            String filename = "data/attendance.csv";
            File file = new File(filename);
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] infos = line.split(",");

                    //Check length of the infos to avoid errors or even crashes
                    if (infos.length == 6){
                        String user_id = infos[0];
                        String date = infos[2];
                        String outlet = infos[4];
                        String status = infos[5];
                        if (user_id.equals(id) && date.equals(date_string) && outlet.equals(outlet_name) && status.equals("IN")) {
                            JOptionPane.showMessageDialog(this, "You have already clocked in today!","WARNING : CLOCK IN NOT ALLOWED",JOptionPane.WARNING_MESSAGE);
                            scanner.close();
                            return;// stop everything
                        }
                    }
                }
                scanner.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        try {
            PrintWriter pw = new PrintWriter(new FileWriter("data/attendance.csv", true));// Append mode
            
            // Format: ID,Date,Time,Status
            pw.println(id + "," + name + "," + date_string + "," + time_string + "," + outlet_name + ",IN");
            
            pw.close();
            
            JOptionPane.showMessageDialog(this, "Employee ID : " + id + "\n" +
                                                "Name : " + name + "\n" +
                                                "Outlet : " + outlet_code + " (" + outlet_name + ") \n\n" +
                                                "Clock In Successful!\nDate: " + date_string + "\nTime: " + time_string);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
