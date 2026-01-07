package src;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClockOutPage extends JFrame{

    ClockOutPage(){
        setTitle("Attendance Clock Out");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        String id = Session.current_user.get_employee_id();// get the current employee id;
        String name = Session.current_user.get_employee_name();// get the current employee name;
        String outlet_code = Session.user_current_outlet.getCode();
        String outlet_name = Session.user_current_outlet.getName();
        String filename = "data/attendance.csv";

        LocalTime clock_in_time = null;// initialize to null because will assign the clock_in_time to it by reading the attendance

        // By default Java DateTime gives year first then month and then day so must format it.
        // This tells Java: "I want Day first, then Month, then Year"
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date_string = today.format(date_formatter);

        // Format time to look like "09:00:00"
        DateTimeFormatter time_formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time_string = now.format(time_formatter);

        try {
            Scanner scanner = new Scanner(new File (filename));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] infos = line.split(",");

                if (infos.length == 6) {
                    String user_id = infos[0];
                    String date = infos[2];
                    String time = infos[3];
                    String outlet = infos[4];
                    String status = infos[5];

                    if (user_id.equals(id) && date.equals(date_string) && status.equals("IN")) {
                        clock_in_time = LocalTime.parse(time, time_formatter);
                    }
                    else if (user_id.equals(id) && status.equals("OUT")) {
                        JOptionPane.showMessageDialog(this, "You have already clocked out!","WARNING : CLOCK OUT NOT ALLOWED",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            double hours_worked = 0.0;
            if (clock_in_time != null) {
                long minutes = Duration.between(clock_in_time, now).toMinutes();
                hours_worked = minutes/60;
            }
            else {
                JOptionPane.showMessageDialog(this,"You have not clocked in today!","Warning: No 'Clock In' record found for today.",JOptionPane.WARNING_MESSAGE);
            }


            PrintWriter printWriter = new PrintWriter(new FileWriter(filename, true));
            printWriter.println(id + "," + name + "," + date_string + "," + time_string + "," + outlet_name + ",OUT");
            printWriter.close();
            JOptionPane.showMessageDialog(this, "Clock Out Successful!\nTotal Hours Worked: " + String.format("%.1f", hours_worked));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
