package src;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JFrame;

public class ClockInPage extends JFrame{
    
    ClockInPage(){
        setTitle("Attendance Clock In");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        String id = "";
        String name = "";
        String outlet = "";
        String filename = "outlet.csv";

        if (Session.current_user != null) {
            id = Session.current_user.get_employee_id();// get the current employee id
            name = Session.current_user.get_employee_name();// get the current employee name
            ArrayList<Outlet> outlets = FileReader.outlet_transfer_data(filename);
            
        }
    }
}
