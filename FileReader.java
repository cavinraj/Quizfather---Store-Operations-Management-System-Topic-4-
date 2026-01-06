import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {
    static ArrayList<Employee> employees;
    static ArrayList<Outlet> outlets;

    static ArrayList<Employee> employee_transfer_data(String filename){
        employees = new ArrayList<>();

        try (Scanner scanner = new Scanner(new FileInputStream(filename))) {
            scanner.nextLine(); //This is to eliminate the first line of the csv from being read
            while (scanner.hasNextLine()) {
                String current_line = scanner.nextLine();
                String[] infos = current_line.split(",");
                if (infos.length == 4) {
                    String employee_id = infos[0];
                    String employee_name = infos[1];
                    String role = infos[2];
                    String password = infos[3];
                    Employee employee = new Employee(employee_id, employee_name, role, password);
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }

        return employees;
    }

    static ArrayList<Outlet> outlet_transfer_data(String filename){
        outlets = new ArrayList<>();

        try (Scanner scanner = new Scanner(new FileInputStream(filename))) {
            scanner.nextLine(); //This is to eliminate the first line of the csv from being read
            while (scanner.hasNextLine()) {
                String current_line = scanner.nextLine();
                String[] infos = current_line.split(",");
                String outlet_id = infos[0];
                String outlet_name = infos[1];
                Outlet outlet = new Outlet(outlet_id, outlet_name);
                outlets.add(outlet);
            }
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }

        return outlets;
    }
}
